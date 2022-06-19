package someone.alcoholic.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.domain.mail.AuthMail;
import someone.alcoholic.dto.mail.AuthMailDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.mail.AuthMailRepository;
import someone.alcoholic.repository.member.MemberRepository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final Duration NUMBER_DURATION = Duration.ofMinutes(5); // 인증번호 체크 제한시간 5분
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final AuthMailRepository authMailRepository;
    private final JavaMailSender mailSender;

    public ResponseEntity<?> sendAuthEmail(String email, MailType type) throws MessagingException {
        log.info("{} 이메일 전송요청 시작 : {}", type, email);
        checkSameEmail(email);
        String randomNumber = String.valueOf(getRandomNumber(email));
        redisSaveNumber(email, randomNumber, type);
        MimeMessage message = getMimeMessage(email, randomNumber);
        mailSender.send(message);
        log.info("{} 이메일 전송요청 성공 : {}", type, email);
        return new ResponseEntity<>(ApiProvider.success(null, MessageEnum.EMAIL_SEND_SUCCESS), HttpStatus.OK);
    }

    public ResponseEntity<?> checkAuthEmail(AuthMailDto mailDto, MailType type) {
        String email = mailDto.getEmail();
        log.info("이메일 인증절차 시작 : {}", email);
        String savedNumber = redisGetNumber(email, type);
        checkSavedNumber(email, mailDto.getNumber(), savedNumber);
        saveAuthMail(type, email);
        log.info("이메일 인증절차 완료 : {}", email);
        return new ResponseEntity<>(ApiProvider.success(null, MessageEnum.EMAIL_SEND_SUCCESS), HttpStatus.OK);
    }

    // 인증번호 절차 통과한 이메일 인증 기록 저장
    private void saveAuthMail(MailType type, String email) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String typeName = type.getType();
        log.info("{} 이메일 인증 기록 DB 저장 시작 : {} - {}", typeName, email, now);
        Optional<AuthMail> authMailOpt = authMailRepository.findByEmailAndType(email, type);
        if (authMailOpt.isPresent()) {
            AuthMail authMail = authMailOpt.get();
            authMail.setLastDate(now); // update auth_mail
            log.info("{} 이메일 인증 요청 성공 : {} (before : {}, now : {})", typeName, email, authMail.getLastDate(), now);
        } else {
            AuthMail authMail = AuthMail.builder().email(email).type(type).lastDate(now).build();
            authMailRepository.save(authMail);
            log.info("{} 이메일 인증 요청 성공 - 최초 요청 : {} (now : {})", typeName, email, now);
        }
        redisTemplate.delete(type.getPrefix() + email); // redis 인증번호 기록삭제
        log.info("{} 이메일 인증 기록 DB 저장 시작 : {} - {}", typeName, email, now);
    }

    private String redisGetNumber(String email, MailType type) {
        log.info("redis 인증번호 조회 시도 : {}", email);
        String key = type.getPrefix() + email;
        String savedNumber = (String) redisTemplate.opsForValue().get(key);
        log.info("redis 인증번호 조회 : {} - {}", email, savedNumber);
        return savedNumber;
    }

    // 들어온 인증번호와 redis 저장 인증번호 비교
    private void checkSavedNumber(String email, String number, String savedNumber) {
        log.info("이메일 인증시도 숫자 체크 : {}", email);
        if (savedNumber == null) {
            log.info("이메일 인증시도 실패 - 이메일 redis 미저장 : {} (input : {}, saved : {}", email, number, savedNumber);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.EMAIL_CHECK_UNKNOWN);
        }
        if (!savedNumber.equals(number)) {
            log.info("이메일 인증시도 실패 - 인증번호 불일치 : {} (input : {}, saved : {})", email, number, savedNumber);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.EMAIL_CHECK_DISCORD);
        }
        log.info("이메일 인증시도 성공 - 인증번호 일치 : {} (input : {}, saved : {})", email, number, savedNumber);
    }

    // 이메일 중복 체크
    private void checkSameEmail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent((mem) -> {
                    log.info("이메일 중복체크 탈락 : {}", email);
                    throw new CustomRuntimeException(HttpStatus.CONFLICT, ExceptionEnum.EMAIL_ALREADY_EXIST);
                });
        log.info("이메일 중복체크 통과 : {}", email);
    }

    // redis 인증번호 저장
    private void redisSaveNumber(String email, String randomNumber, MailType type) {
        // redis 저장 key 형태 : '[xx]abcd'
        String key = type.getPrefix() + email;
        redisTemplate.opsForValue().set(key, randomNumber, NUMBER_DURATION);
        log.info("이메일 인증번호 redis 저장 완료 : {} - {}", email, randomNumber);
    }

    // 이메일 메세지 설정
    private MimeMessage getMimeMessage(String email, String randomNumber) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        //String msg = String.format("<h2><a href='http://alcoholic.tk/api/signup?email=%s&number=%d'>인증하기</a></h2>", email, randomNumber);
        String content = getMailContent(randomNumber, "아래 번호를 회원가입 이메일 인증번호에 입력 후 확인 버튼을 눌러주세요!");
        message.setSubject("Alcoholic 인증 번호입니다.");
        message.setRecipients(Message.RecipientType.TO, email);
        message.setContent(content, "text/html;charset=euc-kr");
        log.info("이메일 메세지 생성 완료 : {}", email);
        return message;
    }

    // 이메일 메세지 내용 생성
    private String getMailContent(String randomNumber, String sentence) {
        String msg = String.format("<h2>인증번호 : %s</a></h2>", randomNumber);
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:100px;'>");
        sb.append("<h1> 안녕하세요 Alcoholic 입니다. </h1>");
        sb.append("<br>");
        //sb.append("<p>아래 링크를 클릭하면 이메일 인증 절차가 완료됩니다!</p>");
        //sb.append("<p>아래 번호를 회원가입 이메일 인증번호에 입력 후 확인 버튼을 눌러주세요!</p>");
        sb.append("<p>%s</p>");
        sb.append("<br>");
        sb.append(msg);
        return String.format(sb.toString(), sentence);
    }

    // 6자리 인증번호 난수 생성
    private int getRandomNumber(String email) {
        int randomNumber = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
        log.info("인증번호 : {} - {}", email, randomNumber);
        return randomNumber;
    }
}
