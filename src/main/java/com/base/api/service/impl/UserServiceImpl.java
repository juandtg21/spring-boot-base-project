package com.base.api.service.impl;

import com.base.api.dto.*;
import com.base.api.exception.OAuth2AuthenticationProcessingException;
import com.base.api.exception.UserAlreadyExistAuthenticationException;
import com.base.api.model.AppUser;
import com.base.api.model.Role;
import com.base.api.repo.RoleRepository;
import com.base.api.repo.UserRepository;
import com.base.api.security.oauth2.user.OAuth2UserInfo;
import com.base.api.security.oauth2.user.OAuth2UserInfoFactory;
import com.base.api.service.UserService;
import com.base.api.util.CommonUtils;
import com.base.api.util.PasswordGeneratorUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(value = "transactionManager")
    public AppUser registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
        if (signUpRequest.getUserID() != null && userRepository.existsById(signUpRequest.getUserID())) {
            throw new UserAlreadyExistAuthenticationException("User with User id " + signUpRequest.getUserID() + " already exist");
        } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistAuthenticationException("User with email id " + signUpRequest.getEmail() + " already exist");
        }
        AppUser user = buildUser(signUpRequest);
        Date now = Calendar.getInstance().getTime();
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        user = userRepository.save(user);
        userRepository.flush();
        return user;
    }

    private AppUser buildUser(final SignUpRequest formDTO) {
        String password = formDTO.getPassword() != null ? formDTO.getPassword() : PasswordGeneratorUtils.generatePassword(12);
        AppUser user = new AppUser();
        user.setDisplayName(formDTO.getDisplayName());
        user.setPicture(formDTO.getPicture());
        user.setEmail(formDTO.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        final HashSet<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(Role.ROLE_USER));
        user.setRoles(roles);
        user.setProvider(formDTO.getSocialProvider());
        user.setEnabled(true);
        user.setProviderUserId(formDTO.getProviderUserId());
        return user;
    }

    @Override
    public AppUser findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (ObjectUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (ObjectUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        SignUpRequest userDetails = toUserRegistrationObject(registrationId, oAuth2UserInfo);
        AppUser user = findUserByEmail(oAuth2UserInfo.getEmail());
        if (user != null) {
            String existingProvider = user.getProvider().getProviderType();
            boolean isMismatchedProvider = !existingProvider.equals(registrationId) &&
                !existingProvider.equals(SocialProvider.LOCAL.getProviderType());
            if (isMismatchedProvider) {
                throw new OAuth2AuthenticationProcessingException(
                    "Looks like you're signed up with " + user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userDetails);
        }

        return LocalUser.create(user, attributes, idToken, userInfo);
    }

    private AppUser updateExistingUser(AppUser existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setDisplayName(oAuth2UserInfo.getName());
        existingUser.setPicture(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

    private SignUpRequest toUserRegistrationObject(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
        return SignUpRequest.getBuilder().addProviderUserID(oAuth2UserInfo.getId()).addDisplayName(oAuth2UserInfo.getName()).addEmail(oAuth2UserInfo.getEmail())
                .addSocialProvider(CommonUtils.toSocialProvider(registrationId)).addPicture(oAuth2UserInfo.getImageUrl()).build();
    }

    @Override
    public Optional<AppUser> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateUserStatus(long id) {
        Optional<AppUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setStatus(UserStatus.DISCONNECTED.name());
            userRepository.save(user.orElse(null));
        }
    }
    
    @Override
    public List<UserInfo> getAllUsers(Long id) {
        Set<Role> roles = roleRepository.findAllByName(Role.ROLE_USER);
        return userRepository.findByIdNotAndRolesIn(id, roles)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo convertToDto(AppUser user) {
        return buildUserInfo(user);
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LocalUser localUser) {
            return localUser.getUser().getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }

    @Override
    public UserInfo getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


    private UserInfo buildUserInfo(AppUser user) {
        List<String> roleStrings = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new UserInfo(user.getId().toString(),
                user.getDisplayName(), user.getPicture(),
                user.getEmail(), user.getStatus(), roleStrings);
    }

    @Override
    @Transactional
    public UserInfo createUser(UserInfo userInfo) {
        if (userRepository.existsByEmail(userInfo.email())) {
            throw new UserAlreadyExistAuthenticationException("User with email " + userInfo.email() + " already exists");
        }

        AppUser user = new AppUser();
        updateUserFields(user, userInfo);
        
        // Set the default password (should be changed on first login)
        String temporaryPassword = PasswordGeneratorUtils.generatePassword(12);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        
        // Set default fields
        user.setEnabled(true);
        user.setProvider(SocialProvider.LOCAL);
        Date now = Calendar.getInstance().getTime();
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        
        AppUser savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserInfo updateUser(Long id, UserInfo userInfo) {
        AppUser existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(userInfo.email()) &&
            userRepository.existsByEmail(userInfo.email())) {
            throw new UserAlreadyExistAuthenticationException("Email " + userInfo.email() + " is already in use");
        }

        updateUserFields(existingUser, userInfo);
        existingUser.setModifiedDate(Calendar.getInstance().getTime());
        
        AppUser updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Perform soft delete by disabling the user instead of hard delete
        user.setEnabled(false);
        user.setStatus(UserStatus.SUSPENDED.name());
        user.setModifiedDate(Calendar.getInstance().getTime());
        userRepository.save(user);
    }

    @Override
    public List<UserInfo> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Helper method to update user fields
    private void updateUserFields(AppUser user, UserInfo userInfo) {
        user.setDisplayName(userInfo.displayName());
        user.setEmail(userInfo.email());
        user.setPicture(userInfo.picture());
        
        // Update roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : userInfo.roles()) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
    }
}
