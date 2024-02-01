package io.mohajistudio.tangerine.prototype.global.enums;

public enum CommentStatus {
    PUBLISHED, // 게시됨 : 게시글이 정상적으로 공개된 상태
    DELETED, // 삭제됨 : 게시글이 삭제된 상태 (실제로 데이터베이스에서 삭제되지 않고, DeletedAt과 함께 관리)
    FLAGGED, // 신고됨 : 다른 사용자에 의해 신고된 게시글. 이 상태에서 관리자의 검토를 거치게 될 수 있습니다
}
