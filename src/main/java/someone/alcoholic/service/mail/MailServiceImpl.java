package someone.alcoholic.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.MailDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;

    public ResponseEntity<?> sendAuthEmail(String email) throws MessagingException {
        // 이메일 중복 체크
        checkSameEmail(email);
        log.info("회원가입 이메일 중복체크 통과 : {}", email);
        String randomNumber = String.valueOf(getRandomNumber());
        log.info("회원가입 이메일 난수 : {} - {}", email, randomNumber);
        redisTemplate.opsForValue().set(email, randomNumber, Duration.ofMinutes(5));
        log.info("회원가입 이메일 난수 redis 저장 완료 : {} - {}", email, randomNumber);
        // 이메일 메세지 생성
        MimeMessage message = mailSender.createMimeMessage();
        //String msg = String.format("<h2><a href='http://alcoholic.tk/api/signup?email=%s&number=%d'>인증하기</a></h2>", email, randomNumber);
        String content = getMailContent(randomNumber, "아래 번호를 회원가입 이메일 인증번호에 입력 후 확인 버튼을 눌러주세요!");
        message.setSubject("Alcoholic 인증 번호입니다.");
        message.setRecipients(Message.RecipientType.TO, email);
        message.setContent(content, "text/html;charset=euc-kr");
        log.info("회원가입 이메일 메세지 생성 완료 : {}", email);
        mailSender.send(message);
        log.info("회원가입 인증 이메일 전송 성공 : {}", email);
        return new ResponseEntity<>(ApiProvider.success(null, MessageEnum.EMAIL_SEND_SUCCESS), HttpStatus.OK);
    }

    public ResponseEntity<?> checkAuthEmail(MailDto mailDto) {
        String email = mailDto.getEmail();
        log.info("회원가입 이메일 인증 redis 조회 : {}", email);
        String savedNumber = (String) redisTemplate.opsForValue().get(email);
        log.info("회원가입 이메일 인증 숫자 체크 : {}", email);
        return checkEmailNumber(email, mailDto.getNumber(), savedNumber);
    }

    private ResponseEntity<ApiResult<?>> checkEmailNumber(String email, String number, String savedNumber) {
        if (savedNumber == null) {
            log.info("email 인증 시도 실패 - email redis 미저장 : {}", email);
            return new ResponseEntity<>(ApiProvider.fail(ExceptionEnum.EMAIL_CHECK_UNKNOWN), HttpStatus.OK);
        }
        if (!savedNumber.equals(number)) {
            log.info("email 인증 시도 실패 - 인증번호 불일치 : {} (input : {}, saved : {})", email, number, savedNumber);
            return new ResponseEntity<>(ApiProvider.fail(ExceptionEnum.EMAIL_CHECK_DISCORD), HttpStatus.OK);
        }
        log.info("email 인증 성공 : {}", email);
        return new ResponseEntity<>(ApiProvider.success(null, MessageEnum.EMAIL_SEND_SUCCESS), HttpStatus.OK);
    }

    private void checkSameEmail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent((mem) -> {
                    log.info("회원가입 이메일 중복체크 탈락 : {}", email);
                    throw new CustomRuntimeException(ExceptionEnum.EMAIL_ALREADY_EXIST);
                });
    }

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

    // 5자리 난수 생성
    private int getRandomNumber() {
        return (int) (Math.random() * (99999 - 10000 + 1)) + 10000;
    }
}
