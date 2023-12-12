package com.example.purebasketbe.global.security.impl;

import com.example.purebasketbe.domain.admin.entity.Admin;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Object user;

    @Override
    public String getPassword() {
        if (user instanceof Member) {
            return ((Member) user).getPassword();
        } else if (user instanceof Admin) {
            return ((Admin) user).getPassword();
        }
        throw new CustomException(ErrorCode.UNSUPPORTED_USER_TYPE);
    }

    @Override
    public String getUsername() {
        if (user instanceof Member) {
            return ((Member) user).getEmail();
        } else if (user instanceof Admin) {
            return ((Admin) user).getEmail();
        }
        throw new CustomException(ErrorCode.UNSUPPORTED_USER_TYPE);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user instanceof Member) {
            String authority = ((Member) user).getRole().getAuthority();
            return Collections.singletonList(new SimpleGrantedAuthority(authority));
        } else if (user instanceof Admin) {
            String authority = ((Admin) user).getRole().getAuthority();
            return Collections.singletonList(new SimpleGrantedAuthority(authority));
        }
        throw new CustomException(ErrorCode.UNSUPPORTED_USER_TYPE);
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
