package someone.alcoholic.controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import someone.alcoholic.service.MemberService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login-success")
    public String retrieveOAuthCode(@RequestParam String code) {
        System.out.println("code = " + code);
//        final String CLIENT_ID = "9182d496ee2d4e3865ddb36e8d01b08e";
//        final String CLIENT_SECRET = "5nfNTKsvZ4r2Dx6mUILzzY7tpUMrNpfY";
//        final String GRANT_TYPE = "authorization_code";
//        final String REDIRECT_URI = "/login-success";
//        final String SERVER_URL = "https://kauth.kakao.com/oauth/token";
//        String clientCredentials = CLIENT_ID + ":" + CLIENT_SECRET;
//        String base64ClientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes()));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Authorization", "Basic " + base64ClientCredentials);
//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//        parameters.set("grant_type", GRANT_TYPE);
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
//        ResponseEntity<Map> response; response = new RestTemplate().postForEntity(SERVER_URL, request, Map.class);
//        System.out.println("======================================================================");
//        System.out.println("====> response : "+ response);
//        System.out.println("======================================================================");
        return code;
    }

}
