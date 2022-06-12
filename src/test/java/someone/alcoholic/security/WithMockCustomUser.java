package someone.alcoholic.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String id() default "tester";

    String pw() default "pw";

    String role() default "ROLE_USER";
}
