package com.base.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.base.api.dto.SocialProvider;
import com.base.api.dto.UserStatus;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 65981149772133526L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @JsonIgnore
    @Column(name = "PROVIDER_USER_ID")
    private String providerUserId;

    private String email;
    @JsonIgnore
    @Column(name = "enabled", columnDefinition = "BIT", length = 1)
    private boolean enabled;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "picture", length = 1000)
    private String picture = "//ssl.gstatic.com/accounts/ui/avatar_2x.png";

    @JsonIgnore
    @Column(name = "created_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedDate;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;

    private String status = UserStatus.ACTIVE.name();

    // bidirectional many-to-many association to Role
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    private Set<Role> roles;

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", email=" + email +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppUser appUser = (AppUser) obj;
        return Objects.equals(id, appUser.id) && Objects.equals(email, appUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }


}
