package someone.alcoholic.controller.member;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.dto.member.NicknameDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.member.MemberService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "유저 아이디 찾기", description = "이메일 인증을 통한 아이디 찾기")
    @GetMapping("/forget/id")
    public ResponseEntity<ApiResult<String>> findId(@RequestParam @Valid @ApiParam(value = "이메일", required = true) String email) {
        return ApiProvider.success(memberService.findMemberId(email), MessageEnum.MEMBER_ID_SUCCESS);
    }


    @Operation(summary = "유저 비밀번호 초기화", description = "비밀번호 분실시 유저 계정의 비밀번호를 이메일 인증을 통해 재설정")
    @PostMapping("/forget/password")
    public ResponseEntity<ApiResult> resetPassword(@RequestBody @Valid @ApiParam(value = "비밀번호 초기화 정보", required = true) AccountDto accountDto) {
        memberService.resetMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }
    
    
    @Operation(summary = "유저 비밀번호 변경", description = "유저 계정의 기존 비밀번호를 새 비밀번호로 변경")
    @PostMapping("/change/password")
    public ResponseEntity<ApiResult> changePassword(@RequestBody @Valid @ApiParam(value = "비밀번호 변경 정보", required = true) AccountDto accountDto) {
        memberService.changeMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }

    @Operation(summary = "유저 닉네임 변경", description = "유저 계정의 기존 닉네임을 새 닉네임으로 변경")
    @PutMapping("/nickname/{memberId}")
    public ResponseEntity<ApiResult> changeNickname(@PathVariable @ApiParam(value = "닉네임 변경 요청 유저 아이디", required = true) @Valid @NotEmpty(message = "닉네임 변경 요청 아이디가 없습니다.") String memberId, @RequestBody @ApiParam(value = "변경 후 닉네임", required = true) @Valid NicknameDto nicknameDto) {
        memberService.changeMemberNickname(memberId, nicknameDto);
        return ApiProvider.success(MessageEnum.MEMBER_NICKNAME_CHANGE_SUCCESS);
    }

    @Operation(summary = "유저 프로필 이미지 변경", description = "유저 계정의 기존 프로필 이미지를 새 이미지로 변경")
    @PutMapping("/image/{memberId}")
    public ResponseEntity<ApiResult> changeImage(@PathVariable @ApiParam(value = "이미지 변경 요청 유저 아이디", required = true) @Valid @NotEmpty(message = "이미지 변경 요청 아이디가 없습니다.") String memberId, @RequestPart(value = "file") @ApiParam(value = "변경 후 이미지", required = true) MultipartFile multipartFile) {
        memberService.changeMemberImage(memberId, multipartFile);
        return ApiProvider.success(MessageEnum.MEMBER_IMAGE_CHANGE_SUCCESS);
    }

}
