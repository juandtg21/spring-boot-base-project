package com.base.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {
    private static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    //Only to test login with Google or facebook without a frontend.
                    if (cookie.getName().equals("redirect_uri") &&
                        (cookie.getValue() == null || cookie.getValue().isEmpty())) {
                        cookie.setValue("http://localhost:8080/api/profile");
                    }
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        try {
            // Convert an object to JSON string and Base64 encode
            return Base64.getUrlEncoder()
                    .encodeToString(objectMapper.writeValueAsString(object).getBytes());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object", e);
            return null;
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            String value = cookie.getValue();
            String jsonStr = new String(Base64.getUrlDecoder().decode(value));
            return objectMapper.readValue(jsonStr, cls);
        } catch (IOException e) {
            logger.error("Error deserializing cookie value", e);
            return null;
        }
    }
}
