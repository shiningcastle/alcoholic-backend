package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    EMAIL_SEND_SUCCESS("이메일 전송 성공"),
    EMAIL_AUTH_SUCCESS("이메일 인증 성공"),
    EMAIL_CHECK_SUCCESS("인증된 이메일"),
    EMAIL_ALREADY_CHECKED("현재로부터 2시간 내에 해당 이메일로 인증된 기록이 있습니다."),
    MEMBER_ID_SUCCESS("회원 아이디 찾기 성공"),
    MEMBER_PASSWORD_SUCCESS("회원 비밀번호 변경 성공"),
    BOARD_INSERT_SUCCESS("게시물 등록 성공"),
    BOARD_UPDATE_SUCCESS("게시물 수정 성공"),
    BOARD_DELETE_SUCCESS("게시물 삭제 성공"),
    REPLY_INSERT_SUCCESS("댓글 등록 성공"),
    REPLY_UPDATE_SUCCESS("댓글 수정 성공"),
    REPLY_DELETE_SUCCESS("댓글 삭제 성공"),
    HEART_DELETE_SUCCESS("좋아요 삭제 성공"),
    HEART_SAVE_SUCCESS("좋아요 저장 성공");

    private final String message;

    MessageEnum(String message) {
        this.message = message;
    }
}
