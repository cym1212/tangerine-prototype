package io.mohajistudio.tangerine.prototype.global.enums;

public enum PostStatus {
    DRAFT, // 초안: 게시글이 작성되었지만 아직 공개되지 않은 상태
    PUBLISHED, // 게시됨 : 게시글이 정상적으로 공개된 상태
    ARCHIVED, // 보관됨 : 게시글이 유효하지 않거나 작성자가 자신만 볼 수 있게 숨긴 상태
    DELETED, // 삭제됨 : 게시글이 삭제된 상태 (실제로 데이터베이스에서 삭제되지 않고, DeletedAt과 함께 관리)
    FLAGGED, // 신고됨 : 다른 사용자에 의해 신고된 게시글. 이 상태에서 관리자의 검토를 거치게 될 수 있습니다
}
