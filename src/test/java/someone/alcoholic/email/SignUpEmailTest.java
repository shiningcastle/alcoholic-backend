//package someone.alcoholic.email;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//import someone.alcoholic.domain.token.RefreshToken;
//
//import java.time.Duration;
//import java.util.UUID;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class SignUpEmailTest {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Test
//    public void test() {
//        // given
//        String email = "shiningcastle";
//        String userId2 = "kwkw1994";
//        String refreshToken1 = "Refresh1";
//        String refreshToken2 = "Refresh2";
//        String accessToken1 = "Access1";
//        String accessToken2 = "Access2";
//        RefreshToken token = RefreshToken.builder().memberId(userId1).tokenValue(refreshToken1).accessToken(accessToken1).build();
//        RefreshToken token2 = RefreshToken.builder().memberId(userId2).tokenValue(refreshToken2).accessToken(accessToken2).build();
//
//        UUID uuid1 = UUID.randomUUID();
//        System.out.println("uuid1 = " + uuid1);
//        UUID uuid2 = UUID.randomUUID();
//        System.out.println("uuid2 = " + uuid2);
//
//        // when
//        redisTemplate.opsForValue().set(uuid1.toString(), token, Duration.ofHours(1));
//        redisTemplate.opsForValue().set(uuid2.toString(), token2, Duration.ofHours(1));
//
//        // then
//        RefreshToken savedToken1 = (RefreshToken) redisTemplate.opsForValue().get(uuid1.toString());
//        RefreshToken savedToken2 = (RefreshToken) redisTemplate.opsForValue().get(uuid2.toString());
//
//        assertThat(savedToken1, is(notNullValue()));
//        assertThat(savedToken1.getMemberId(), equalTo(userId1));
//        assertThat(savedToken1.getTokenValue(), equalTo(refreshToken1));
//        assertThat(savedToken1.getAccessToken(), equalTo(accessToken1));
//
//        assertThat(savedToken2, is(notNullValue()));
//        assertThat(savedToken2.getMemberId(), equalTo(userId2));
//        assertThat(savedToken2.getTokenValue(), equalTo(refreshToken2));
//        assertThat(savedToken2.getAccessToken(), equalTo(accessToken2));
//    }
//
//}