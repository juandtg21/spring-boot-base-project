package com.base.api.repo;

import com.base.api.model.AppUser;
import com.base.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByIdNotAndRolesIn(Long id, Set<Role> roles);

    List<AppUser> findAllByIdNot(Long myId);

    Optional<AppUser> findByDisplayName(String displayName);
}
