package com.base.api.service;

import com.base.api.dto.LocalUser;
import com.base.api.dto.SignUpRequest;
import com.base.api.dto.UserInfo;
import com.base.api.exception.UserAlreadyExistAuthenticationException;
import com.base.api.model.AppUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    AppUser registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;

    AppUser findUserByEmail(String email);

    Optional<AppUser> findUserById(Long id);

    LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo);

    void updateUserStatus(long id);

    List<UserInfo> getAllUsers(Long id);

    UserInfo createUser(UserInfo userInfo);

    UserInfo updateUser(Long id, UserInfo userInfo);

    void deleteUser(Long id);

    List<UserInfo> getAllUsers();

    UserInfo convertToDto(AppUser user);

    Long getCurrentUserId();

    UserInfo getUserById(Long id);

}
