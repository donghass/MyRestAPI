package com.boot3.myrestapi.security.userInfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
    AuthenticationManager 가 인즏 처리를 할 때 getUsername() 와 getPassword() 사용
    Entity(테이블) 에 저장된 username 와 password 를 가져와서 저장
 */
public class UserInfoUserDetails implements UserDetails {

    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserInfo userInfo;

    public UserInfoUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email=userInfo.getEmail();
        this.password=userInfo.getPassword();
        this.authorities= Arrays.stream(userInfo.getRoles().split(",")) // 리턴 타입이 Stream<string>
//                Stream<string> => Stream<SimpleGrantedAuthority>
                .map(roleName-> new SimpleGrantedAuthority(roleName))   // 함수형 인터페이스 function
//                .map(SimpleGrantedAuthority::new)
//                Stream<SimpleGrantedAuthority> => List<SimpleGrantedAuthority> 해주는게 collect
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
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