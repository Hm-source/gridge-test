package org.example.gridgestagram.controller.feed.dto;

public interface UserProjection {

    Long getId();

    String getUsername();

    String getName();

    String getProfileImageUrl();

    String getRole();

    String getSubscriptionStatus();
}