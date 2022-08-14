package someone.alcoholic.controller.member;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.member.newPasswordChangeDto;
import someone.alcoholic.dto.member.NicknameDto;
import someone.alcoholic.dto.member.newPasswordResetDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.member.MemberService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "유저 아이디 찾기", description = "이메일 인증을 통한 아이디 찾기")
    @GetMapping("/forget")
    public ResponseEntity<ApiResult<String>> findId(@RequestParam @Valid @ApiParam(value = "이메일", required = true) String email) {
        return ApiProvider.success(memberService.findMemberId(email), MessageEnum.MEMBER_ID_SUCCESS);
    }


    @Operation(summary = "유저 비밀번호 초기화", description = "비밀번호 분실시 유저 계정의 비밀번호를 이메일 인증을 통해 재설정")
    @PutMapping("/forget/{id}")
    public ResponseEntity<ApiResult> resetPassword(@PathVariable @Valid @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{8,16}$",
            message = "아이디는 영문자만 혹은 영문자 + 숫자 조합 형태여야 합니다. 길이는 8자에서 최대 16자까지 가능합니다.") @ApiParam(value = "비밀번호 초기화 요청 유저 아이디", required = true) @NotEmpty(message = "비밀번호 초기화 요청 아이디가 없습니다.") String id,
                                                   @RequestBody @Valid @ApiParam(value = "비밀번호 초기화 정보", required = true) newPasswordResetDto passwordResetDto) {
        memberService.resetMemberPassword(id, passwordResetDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }


    @Operation(summary = "유저 비밀번호 변경", description = "유저 계정의 기존 비밀번호를 새 비밀번호로 변경")
    @Secured("ROLE_USER")
    @PutMapping("/password/{id}")
    public ResponseEntity<ApiResult> changePassword(@PathVariable @Valid @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{8,16}$",
            message = "아이디는 영문자만 혹은 영문자 + 숫자 조합 형태여야 합니다. 길이는 8자에서 최대 16자까지 가능합니다.") @ApiParam(value = "비밀번호 변경 요청 유저 아이디", required = true) @NotEmpty(message = "비밀번호 변경 요청 아이디가 없습니다.") String id,
                                                    @RequestBody @Valid @ApiParam(value = "비밀번호 변경 정보", required = true) newPasswordChangeDto passwordChangeDto, Principal principal) {
        memberService.changeMemberPassword(principal, id, passwordChangeDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }

    @Operation(summary = "유저 닉네임 변경", description = "유저 계정의 기존 닉네임을 새 닉네임으로 변경")
    @Secured("ROLE_USER")
    @PutMapping("/nickname/{id}")
    public ResponseEntity<ApiResult> changeNickname(@PathVariable @ApiParam(value = "닉네임 변경 요청 유저 아이디", required = true) @Valid @NotEmpty(message = "닉네임 변경 요청 아이디가 없습니다.") String id,
                                                    @RequestBody @ApiParam(value = "변경 후 닉네임", required = true) @Valid NicknameDto nicknameDto, Principal principal) {
        memberService.changeMemberNickname(principal, id, nicknameDto);
        return ApiProvider.success(MessageEnum.MEMBER_NICKNAME_CHANGE_SUCCESS);
    }

    @Operation(summary = "유저 프로필 이미지 변경", description = "새로 받은 이미지 파일을 S3에 저장하고, 유저 프로필 이미지 주소 변경")
    @Secured("ROLE_USER")
    @PutMapping("/image/{id}")
    public ResponseEntity<ApiResult> changeImage(@PathVariable @ApiParam(value = "이미지 변경 요청 유저 아이디", required = true) @Valid @NotEmpty(message = "이미지 변경 요청 아이디가 없습니다.") String id,
                                                 @RequestPart(value = "file") @ApiParam(value = "변경 후 이미지", required = true) MultipartFile multipartFile, Principal principal) {
        memberService.changeMemberImage(principal, id, multipartFile);
        return ApiProvider.success(MessageEnum.MEMBER_IMAGE_CHANGE_SUCCESS);
    }

    @Operation(summary = "유저 프로필 이미지 변경", description = "새로 받은 이미지 파일을 S3에 저장하고, 유저 프로필 이미지 주소 변경")
    @Secured("ROLE_USER")
    @DeleteMapping("/image/{id}")
    public ResponseEntity<ApiResult> deleteImage(@PathVariable @ApiParam(value = "이미지 삭제 요청 유저 아이디", required = true) @Valid @NotEmpty(message = "이미지 삭제 요청 아이디가 없습니다.") String id,
                                                 Principal principal) {
        memberService.deleteMemberImage(principal, id);
        return ApiProvider.success(MessageEnum.MEMBER_IMAGE_REMOVE_SUCCESS);
    }

}
