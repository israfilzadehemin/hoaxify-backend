package com.hoaxify.ws.user;

import com.hoaxify.ws.hoax.Hoax;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "{hoaxify.constraint.username.NotNull.message}")
    @UniqueUsername
    @Size(min = 5, max = 255)
    private String username;
    @NotNull()
    @Size(min = 5, max = 255)
    private String displayName;
    @NotNull()
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "{hoaxify.constraint.password.Pattern.message}")
    private String password;

    @Lob
    private String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Hoax> hoaxes;

    @Override
    public String toString() {
        return String.format("User{username='%s', displayName='%s', password='%s'}", username, displayName, password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("Role_user");

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

