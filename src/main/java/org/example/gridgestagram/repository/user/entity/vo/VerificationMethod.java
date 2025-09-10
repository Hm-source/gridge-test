package org.example.gridgestagram.repository.user.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VerificationMethod {
    SMS("문자메시지");

    private final String description;
}