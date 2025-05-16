package com.base.api.dto;

import java.util.List;

public record UserInfo(String id, String displayName, String picture, String email, String status, List<String> roles) {
}