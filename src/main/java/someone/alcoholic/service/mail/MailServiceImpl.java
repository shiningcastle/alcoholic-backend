package someone.alcoholic.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.mail.Mail;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.mail.MailRepository;
import someone.alcoholic.repository.redis.RedisRepository;
import someone.alcoholic.util.DateUtil;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Calendar;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Value("${mail.minutes}")
    private int minutes;
    @Value("${mail.hours}")
    private int hours;
    @Value("${mail.link}")
    private String mailLink;
    @Value("${mail.response}")
    private String mailResponse;
    private final RedisRepository redisRepository;
    private final MailRepository mailRepository;
    private final JavaMailSender mailSender;

    public ResponseEntity<ApiResult> sendEmail(MailType type, String email) throws MessagingException {
        log.info("{} 이메일 전송요청 시작 : {}", type, email);
        Optional<Mail> mailOpt = mailRepository.findTop1ByEmailAndTypeAndCompletionOrderByAuthDateDesc(email, type, true);
        Mail mail = mailOpt.get();
        log.info("mail :" + mail.getEmail());
        log.info("type :" +mail.getType().getType());
        log.info("authDate :" +String.valueOf(mail.getAuthDate()));
        if (mailOpt.isPresent()) {
            log.info("{} 인증 이메일, Mail 테이블 존재 - {}", type, email);
            // 인증메일 기록 2시간 내면 다시 이메일 요청 필요없음
//            Mail mail = mailOpt.get();
            Timestamp mailDatePlusHours = DateUtil.getDateAfterTime(mail.getAuthDate(), Calendar.HOUR, hours);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            log.info("now : " + String.valueOf(now));
            log.info("mailDatePlusHours : " + String.valueOf(mailDatePlusHours));
            if (now.before(mailDatePlusHours)) {
                log.info("{} 시간 내 {} 이메일 인증완료 : 이메일 미발송 - {}", hours, type, email);
//                throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.EMAIL_ALREADY_CHECKED);
                return ApiProvider.success(MessageEnum.EMAIL_ALREADY_CHECKED);
            }
        }
        int number = getRandomNumber(email);
        redisRepository.set(type.getPrefix() + email, number, Duration.ofMinutes(minutes));
        MimeMessage message = getMimeMessage(type, email, number);
        mailSender.send(message);
        saveMail(type, email, number, new Timestamp(System.currentTimeMillis()));
        log.info("{} 이메일 전송요청 성공 : {}", type, email);
        return ApiProvider.success(MessageEnum.EMAIL_SEND_SUCCESS);
    }

    // 이메일 인증 성공 Response 생성
    public void authEmail(MailType type, String email, int number, HttpServletResponse response) {
        log.info("{} 이메일 인증절차 시작 : {}", type, email);
        int savedNumber = redisGetNumber(email, type);
        checkSavedNumber(email, number, savedNumber);
        updateMail(type, email, number, new Timestamp(System.currentTimeMillis()));
        redisRepository.delete(type.getPrefix() + email);
        log.info("{} 이메일 인증절차 완료 : {}", type, email);
        makeAuthResponse(response);
    }

    // 인증된 이메일인지 조회
    public ResponseEntity<ApiResult> checkEmailCertified(MailType type, String email) {
        log.info("{} 인증된 이메일 여부 조회 시작 - {}", type, email);
        // 해당 이메일의 가장 최근 인증 기록 조회
        Optional<Mail> mailOpt = mailRepository.findTop1ByEmailAndTypeAndCompletionOrderByAuthDateDesc(email, type, true);
        if (!mailOpt.isPresent()) {
            log.info("{} 미인증 이메일, Mail 테이블 미존재 - {}", type, email);
            throw new CustomRuntimeException(HttpStatus.UNAUTHORIZED, ExceptionEnum.EMAIL_CHECK_UNKNOWN);
        }
        // 인증메일 기록 2시간 내인지 체크
        Mail mail = mailOpt.get();
        Timestamp mailDatePlusHours = DateUtil.getDateAfterTime(mail.getAuthDate(), Calendar.HOUR, hours);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (!now.before(mailDatePlusHours)) {
            log.info("{} 시간 내 {} 이메일 인증요청 기록 없음 - {}", hours, type, email);
            throw new CustomRuntimeException(HttpStatus.UNAUTHORIZED, ExceptionEnum.EMAIL_CHECK_TIME);
        }
        log.info("{} 인증된 이메일 여부 조회 완료 - {}", type, email);
        return ApiProvider.success(MessageEnum.EMAIL_CHECK_SUCCESS);
    }

    private void makeAuthResponse(HttpServletResponse response) {
        log.info("이메일 인증성공 Response 메세지 생성 시작");
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(mailResponse);
            out.flush();
            log.info("이메일 인증성공 Response 메세지 생성 완료");
        } catch (IOException e) {
            log.info("이메일 인증 Response 메세지 생성 실패");
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    // 이메일 인증 기록 저장
    private void saveMail(MailType type, String email, int number, Timestamp time) {
        log.info("{} 이메일 인증 요청 DB 저장 시작 : {} - {}", type, email, time);
        Mail mail = Mail.builder().email(email).type(type).number(number).sendDate(time).completion(false).build();
        mailRepository.save(mail);
        log.info("{} 이메일 인증 요청 DB 저장 완료 : {} (date : {})", type, email, time);
    }

    // 이메일 인증 완료 시간 Update
    private void updateMail(MailType type, String email, int number, Timestamp time) {
        log.info("{} 이메일 인증 요청 DB 갱신 시작 : {} - {}", type, email, time);
        Mail mail = mailRepository.findByEmailAndTypeAndNumberAndCompletion(email, type, number, false)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.PAGE_NOT_FOUND));
        mail.recordAuthDate(time);
        mail.changeCompletion(true);
        mailRepository.save(mail);
        log.info("{} 이메일 인증 요청 DB 갱신 완료 : {} (date : {})", type, email, time);
    }

    // redis에서 인증번호 조회
    private int redisGetNumber(String email, MailType type) {
        log.info("redis 인증번호 조회 시도 : {}", email);
        String key = type.getPrefix() + email;
        Integer savedNumber = (Integer) redisRepository.get(key);
        if (savedNumber == null) {
            log.info("이메일 인증시도 실패 - redis 이메일 인증번호 미저장 : {}", email);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.EMAIL_CHECK_UNKNOWN);
        }
        log.info("redis 인증번호 조회 : {} - {}", email, savedNumber);
        return savedNumber;
    }

    // 들어온 인증번호와 redis 저장 인증번호 비교
    private void checkSavedNumber(String email, int number, int savedNumber) {
        log.info("이메일 인증시도 숫자 체크 : {}", email);
        if (savedNumber != number) {
            log.info("이메일 인증시도 실패 - 인증번호 불일치 : {} (input : {}, saved : {})", email, number, savedNumber);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.EMAIL_CHECK_DISCORD);
        }
        log.info("이메일 인증시도 성공 - 인증번호 일치 : {} (input : {}, saved : {})", email, number, savedNumber);
    }

    // 이메일 메세지 설정
    private MimeMessage getMimeMessage(MailType type, String email, int number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        String content = getMailContent(getAuthLinkTag(type, email, number));
        message.setSubject("Alcoholic 인증 메일입니다.");
        message.setRecipients(Message.RecipientType.TO, email);
        message.setContent(content, "text/html;charset=euc-kr");
        log.info("이메일 메세지 생성 완료 : {}", email);
        return message;
    }

    // 이메일 인증 링크 태그 생성
    private String getAuthLinkTag(MailType type, String email, int number) {
        String linkTag = String.format(mailLink, type.toString().toLowerCase(), email, number);
        log.info("이메일 인증 링크 태그 생성완료 : {}", linkTag);
        return linkTag;
    }

    // 이메일 메세지 내용 생성
    private String getMailContent(String linkTag) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:100px;'>");
        sb.append("<h1> 안녕하세요 Alcoholic 입니다. </h1>");
        sb.append("<br>");
        sb.append("<p>아래 링크를 클릭하면 이메일 인증 절차가 완료됩니다!</p>");
        sb.append("<br>");
        sb.append(linkTag);
        log.info("이메일 메세지 HTML 생성 완료");
        return sb.toString();
    }

    // 6자리 인증번호 난수 생성
    private int getRandomNumber(String email) {
        int randomNumber = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        log.info("인증번호 : {} - {}", email, randomNumber);
        return randomNumber;
    }
}
