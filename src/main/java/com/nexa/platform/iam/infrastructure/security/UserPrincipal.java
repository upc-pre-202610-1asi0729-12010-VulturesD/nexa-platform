package com.nexa.platform.iam.infrastructure.security;

import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.UserAccount;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
    private final UserAccount account;

    public UserPrincipal(UserAccount account) {
        this.account = account;
    }

    public Long id() { return account.getId(); }
    public String fullName() { return account.getFullName(); }
    public String email() { return account.getEmail(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream()
            .map(Role::getName)
            .map(Enum::name)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    @Override public String getPassword() { return account.getPasswordHash(); }
    @Override public String getUsername() { return account.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return account.isEnabled(); }
}
