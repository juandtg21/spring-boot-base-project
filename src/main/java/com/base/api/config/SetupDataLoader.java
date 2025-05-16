package com.base.api.config;

import com.base.api.dto.SocialProvider;
import com.base.api.model.AppUser;
import com.base.api.model.Role;
import com.base.api.repo.RoleRepository;
import com.base.api.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // Create initial roles
        Role userRole = createRoleIfNotFound(Role.ROLE_USER);
        Role adminRole = createRoleIfNotFound(Role.ROLE_ADMIN);
        Role modRole = createRoleIfNotFound(Role.ROLE_MODERATOR);
        createUserIfNotFound("admin@test.com", Set.of(userRole, adminRole, modRole), "Admin");
        createUserIfNotFound("johndoe@test.com", Set.of(userRole), "john");
        createUserIfNotFound("janedoe@test.com", Set.of(userRole), "jane");
        createUserIfNotFound("jimdoe@test.com", Set.of(userRole), "jim");
        createUserIfNotFound("joecitizen@test.com", Set.of(userRole), "joe");
        alreadySetup = true;
    }

    @Transactional
    public AppUser createUserIfNotFound(final String email, Set<Role> roles, String displayName) {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            user = new AppUser();
            user.setDisplayName(displayName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRoles(roles);
            user.setProvider(SocialProvider.LOCAL);
            user.setPicture("//ssl.gstatic.com/accounts/ui/avatar_2x.png");
            user.setEnabled(true);
            Date now = Calendar.getInstance().getTime();
            user.setCreatedDate(now);
            user.setModifiedDate(now);
            user = userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.save(new Role(name));
        }
        return role;
    }
}
