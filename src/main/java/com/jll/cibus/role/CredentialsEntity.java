package com.jll.cibus.role;

import com.jll.cibus.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CredentialsEntity implements UserDetails
{
    @Id
    @GeneratedValue(strategy =
            jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{6}", message = "PIN must have six digits")
    private String password;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean enabled;

    @Column(name = "refresh_token",length = 2048,unique = true, nullable = false)
    private String refreshToken;

    @OneToOne
    @JoinColumn (name = "usuario_id", referencedColumnName = "id", unique = true)
    private UserEntity usuario;

    @ManyToMany (cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable (
            name= "credentials_roles",
            joinColumns = @JoinColumn (name = "credential_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles= new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities ()
    {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach( rol -> authorities.add(
                new SimpleGrantedAuthority(rol.getRole().name())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean isEnabled ()
    {
        return Boolean.TRUE.equals(this.enabled);
    }
}
