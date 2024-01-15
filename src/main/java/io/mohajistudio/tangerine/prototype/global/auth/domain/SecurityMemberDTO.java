package io.mohajistudio.tangerine.prototype.global.auth.domain;

import io.jsonwebtoken.Claims;
import io.mohajistudio.tangerine.prototype.global.enums.Provider;
import io.mohajistudio.tangerine.prototype.global.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class SecurityMemberDTO {
    private final Long id;
    @Setter
    private Role role;
    private final String email;
    private final Provider provider;

    public static SecurityMemberDTO fromClaims(Claims claims) {
        return SecurityMemberDTO.builder().id(Long.valueOf(claims.getId())).email(claims.get("email", String.class)).provider(Provider.fromValue(claims.get("provider", String.class))).role(Role.fromValue(claims.get("role", String.class))).build();
    }
}
