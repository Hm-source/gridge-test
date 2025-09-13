package org.example.gridgestagram.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserInfo {

    private String providerId;
    private String name;
    private String profileImageUrl;
}
