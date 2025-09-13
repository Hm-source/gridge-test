package org.example.gridgestagram.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;

@AllArgsConstructor
@Getter
@Builder
public class UserSimpleResponse {

    private Long id;
    private String username;
    private String name;
    private String profileImageUrl;
    private String role;
    private String subscriptionStatus;

    public static UserSimpleResponse from(User user) {
        return UserSimpleResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .profileImageUrl(user.getProfileImageUrl())
            .role(user.getRole().name())
            .subscriptionStatus(user.getSubscriptionStatus().name())
            .build();
    }
}
