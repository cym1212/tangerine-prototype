package io.mohajistudio.tangerine.prototype.domain.notification.controller;


import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.notification.dto.NotificationDTO;
import io.mohajistudio.tangerine.prototype.domain.notification.mapper.NotificationMapper;
import io.mohajistudio.tangerine.prototype.domain.notification.service.NotificationService;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/members/{memberId}/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping
    @Operation(summary = "페이징 된 알림 목록", description = "page와 size 값을 넘기면 페이징 된 게시글 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<NotificationDTO> notificationListByPage(@ModelAttribute PageableParam pageableParam, @PathVariable(name = "memberId") Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        if (!Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        Page<Notification> notificationListByPage = notificationService.findNotificationListByPage(memberId, pageable);

        return notificationListByPage.map(notificationMapper::toDTO);
    }
}
