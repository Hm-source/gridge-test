package org.example.gridgestagram.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String credentials;

    // 1. AuthenticationFilter에서 가장 처음 인증정보를 담아 최초 JwtAuthenticationToken 생성
    public JwtAuthenticationToken(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        super.setAuthenticated(false);
    }

    // 2. AuthenticationProvider에서 인증정보 일치여부를 판단한 뒤 최종 JwtAuthenticationToken 생성
    public JwtAuthenticationToken(String subject,
        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = subject;
        this.credentials = null;
        super.setAuthenticated(true);
    }

    public String getToken() {
        return this.credentials;
    }

}
