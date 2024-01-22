package io.mohajistudio.tangerine.prototype.global.enums;

import lombok.Getter;

@Getter
public enum Provider {
    KAKAO("kakao", "카카오"),
    GOOGLE("google", "구글"),
    APPLE("apple", "애플");

    private final String value;
    private final String koreanName;

    Provider(String value, String koreanName) {
        this.value = value;
        this.koreanName = koreanName;
    }

    public static Provider fromValue(String value) {
        for (Provider provider : values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 제공자입니다: " + value);
    }
}
