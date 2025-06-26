package umc.duckmelang.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.duckmelang.global.apipayload.code.BaseErrorCode;
import umc.duckmelang.global.apipayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "존재하지 않는 회원입니다."),
    MEMBER_EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 공란으로 비워둘 수 없습니다."),
    MEMBER_EMPTY_INTRODUCTION(HttpStatus.BAD_REQUEST, "MEMBER4003", "자기소개는 공란으로 비워둘 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4004", "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4005", "이미 존재하는 닉네임입니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBER4006", "잘못된 인증입니다. 해당 작업에 대한 권한이 없습니다."),
    ALREADY_DELETED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBER4007", "이미 탈퇴된 사용자입니다."),

    // 멤버 프로필 사진 관련 에러
    MEMBER_PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_PROFILE_IMAGE4001", "프로필 이미지가 존재하지 않습니다."),
    CANNOT_DELETE_DEFAULT_PROFILE_IMAGE(HttpStatus.BAD_REQUEST," MEMBER_PROFILE_IMAGE4002", "기본 프로필 이미지는 삭제할 수 없습니다."),
    CANNOT_UPDATE_DEFAULT_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "MEMBER_PROFILE_IMAGE4003", "기본 프로필 이미지는 업데이트할 수 없습니다."),

    // 토큰 관련 응답
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN4000", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 토큰입니다."),
    MISSING_TOKEN(HttpStatus.NOT_FOUND, "TOKEN4004", "토큰을 찾을 수 없습니다."),

    // 로그인 관련 응답
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH4000", "인증 정보가 필요합니다."),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4001", "이메일 또는 비밀번호가 잘못되었습니다."),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4004", "이메일과 일치하는 사용자가 없습니다."),

    //게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4001", "해당하는 게시글이 없습니다"),
    PAGE_MUST_LEAST_ZERO(HttpStatus.BAD_REQUEST, "POST4002", "페이지는 0 이상이어야 합니다"),
    INVALID_WANTED(HttpStatus.BAD_REQUEST, "POST4003","게시글의 wanted 값이 유효하지 않습니다"),
    
    //게시글 이미지 관련 에러
    POST_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "POSTIMAGE4001","해당하는 게시글 이미지가 없습니다"),

    // 아이돌 카테고리 관련 에러
    IDOL_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "IDOLCATEGORY4001", "해당하는 아이돌 항목이 없습니다."),
    INVALID_IDOL_CATEGORY(HttpStatus.BAD_REQUEST, "IDOLCATEGORY4002", "선택한 아이돌 중 유효하지 않은 항목이 있습니다."),
    ALREADY_ADDED_IDOL(HttpStatus.BAD_REQUEST, "IDOLCATEGORY4003", "이미 선택한 아이돌 입니다."),

    // 지뢰 관련 에러
    DUPLICATE_LANDMINE(HttpStatus.BAD_REQUEST, "LANDMINE4001", "중복된 키워드가 존재합니다."),
    INVALID_LANDMINE(HttpStatus.BAD_REQUEST, "LANDMINE4002", "존재하지 않는 키워드입니다."),

    // 행사 카테고리 관련 에러
    INVALID_EVENT_CATEGORY(HttpStatus.BAD_REQUEST, "EVENTCATEGORY4001", "선택한 행사 중 유효하지 않은 항목이 있습니다."),
    EVENT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_CATEGORY4002", "이번트 카테고리가 없습니다."),

    // 동행요청 관련 에러
    ALREADY_PROCESSED_APPLICATION(HttpStatus.CONFLICT, "APPLICATION4001","이미 처리된 동행 요청입니다."),
    NOT_POSSESSED_APPLICATION(HttpStatus.CONFLICT, "APPLICATION4002", "본인 소유의 동행 요청이 아닙니다."),
    APPLICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "APPLICATION4003", "존재하지 않는 동행 요청입니다."),
    UNAVAILABLE_TO_APPLY_FOR_CONFIRMED_POST(HttpStatus.FORBIDDEN, "APPLICATION4004", "이미 확정된 게시글에 동행 요청을 처리할 수 없습니다."),
    UNAVAILABLE_TO_APPLY_FOR_OWN_POST(HttpStatus.FORBIDDEN, "APPLICATION4005", "본인 게시글에 동행 요청할 수 없습니다."),

    //스크랩 관련 에러
    INVALID_BOOKMARK(HttpStatus.NOT_FOUND, "BOOKMARK4001", "해당하는 스크랩이 없습니다."),
    DUPLICATE_BOOKMARK(HttpStatus.BAD_REQUEST, "BOOKMARK4002", "이미 스크랩 된 게시글입니다."),
    CANNOT_BOOKMARK_OWN_POST(HttpStatus.BAD_REQUEST,"BOOKMARK4003", "자신의 게시글은 스크랩할 수 없습니다."),

    // 후기 관련 에러 
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW4001", "해당하는 후기가 없습니다"),

    // 채팅 통신 관련 에러
    JSON_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "WEBSOCKET4001", "메시지 변환 중 매핑 오류가 발생했습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "WEBSOCKET4002", "유효하지 않은 JSON 형식입니다."),
    JSON_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WEBSOCKET5001", "JSON 변환에 실패했습니다."),

    //알림 관련 에러
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4001", "해당하는 알림이 없습니다"),

    //알림 설정 관련 에러
    NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_SETTING4001", "해당하는 알림 설정이 없습니다"),

    // 채팅 메세지 관련 에러
    INVALID_MESSAGE_TYPE(HttpStatus.BAD_REQUEST, "CHATMESSAGE4001", "지원하지 않는 메세지 타입입니다."),
    EMPTY_MESSAGE_TEXT(HttpStatus.BAD_REQUEST, "CHATMESSAGE4002", "메세지 내용이 없습니다."),
    EMPTY_MESSAGE_IMAGE(HttpStatus.BAD_REQUEST, "CHATMESSAGE4003", "메세지 이미지가 없습니다."),
    EMPTY_MESSAGE_FILE(HttpStatus.BAD_REQUEST, "CHATMESSAGE4004", "메세지 파일이 없습니다."),
    
    // 채팅방 관련 에러
    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "APPLICATION4001", "존재하지 않는 채팅방입니다."),
    CHATROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "APPLICATION4002", "이미 존재하는 채팅방입니다."),
    CHATROOM_BEING_CREATED(HttpStatus.BAD_REQUEST, "APPLICATION4003", "생성중인 채팅방입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
