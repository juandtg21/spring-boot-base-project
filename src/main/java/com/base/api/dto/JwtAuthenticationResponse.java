package com.base.api.dto;

public record JwtAuthenticationResponse(String accessToken, UserInfo user) {
}
