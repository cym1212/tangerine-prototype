package io.mohajistudio.tangerine.prototype.global.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력 값입니다"),
    METHOD_NOT_ALLOWED(405, "C002", "지원하지 않는 메서드입니다"),
    ENTITY_NOT_FOUND(404, "C003", "엔티티를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(500, "C004", "서버에서 에러가 발생했습니다"),
    INVALID_TYPE_VALUE(400, "C005", "유효하지 않은 형식의 값입니다"),
    HANDLE_ACCESS_DENIED(403, "C006", "접근 권한이 없습니다"),
    URL_NOT_FOUND(404, "C007", "요청한 주소의 API를 찾을 수 없습니다"),
    MISSING_PARAMETER(400, "C008", "필수 값인 매개변수를 찾을 수 없습니다"),
    DATA_INTEGRITY_VIOLATION(400, "C008", "잘못된 데이터에 접근하였습니다, 요청 값을 확인해주세요"),
    HTTP_MESSAGE_NOT_READABLE(400, "C009", "잘못된 JSON 요청 형식입니다"),
    ILLEGAL_ARGUMENT(400, "C010", "잘못된 인수 값이 포함된 요청입니다"),
    INVALID_DATE_RANGE(400, "C011", "시작 날짜는 도착 날짜보다 최근일 수 없습니다"),
    NICKNAME_DUPLICATE(400, "C012", "이미 사용중인 닉네임입니다"),
    DATE_TIME_PARSE_FAILURE(400, "C013", "잘못된 DateTime 형식입니다"),

    // Member
    MEMBER_NOT_FOUND(404, "M001", "존재하지 않는 회원입니다"),
    MEMBER_PROFILE_DUPLICATION(400, "M002", "이미 존재하는 회원입니다"),
    SELF_FOLLOW(400, "M003", "자신을 팔로우 할 수 없습니다"),


    //TOKEN
    MISMATCH_REFRESH_TOKEN(401, "T001", "유효하지 않은 토큰입니다"),
    NO_PERMISSION(401, "T002", "요청에 대한 권한이 없습니다"),

    //POST
    CONTENT_IS_EMPTY(400, "P001", "본문 없이 블럭을 작성할 수 없습니다"),
    INVALID_ORDER_NUMBER(400, "P002", "잘못된 순서 번호입니다"),
    MAX_POSTS_PER_DAY(400, "P003", "하루에 작성 가능한 최대 게시글 개수에 도달했습니다"),
    TOO_FREQUENT_POST(400, "P004", "게시글을 작성한 지 얼마 지나지 않았으므로 잠시 후 작성해주세요"),
    INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER(400, "P005", "잘못된 대표 이미지 순서 번호"),


    //Region
    KAKAO_PLACE_SEARCH(500, "R001", "카카오 장소 검색 API를 호출하는 도중 에러가 발생했습니다"),
    KAKAO_REGULAR_EXPRESSION(500, "R002", "카카오 장소 검색 API가 반환한 주소가 정규식과 일치하지 않습니다. 고객센터에 문의해주세요"),

    //Multipart
    MULTIPART_FILE_EXCEPTION(400, "MP001", "파일을 찾을 수 없습니다"),
    STORAGE_UPLOAD_FAILURE(400, "MP002", "스토리지로 업로드를 실패했습니다"),
    MAX_UPLOAD_SIZE_EXCEEDED(400, "MP003", "파일 최대 크기를 초과했습니다"),
    NO_SUCH_KEY(400, "MP004", "잘못된 파일 이름으로 스토리지에 업로드를 요청했습니다");


    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

}
