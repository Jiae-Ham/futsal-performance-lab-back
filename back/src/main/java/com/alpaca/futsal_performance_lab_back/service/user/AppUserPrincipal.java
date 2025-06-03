package com.alpaca.futsal_performance_lab_back.service.user;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class AppUserPrincipal implements UserDetails {
    private final AppUser appUser;

    public AppUser getAppUser() {
        return appUser;
    }

    @Override
    public String getUsername() {
        return appUser.getUserId();
    }

    @Override public String getPassword() {return appUser.getPassword();}
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return Collections.emptyList(); }
    @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return true;}
}
