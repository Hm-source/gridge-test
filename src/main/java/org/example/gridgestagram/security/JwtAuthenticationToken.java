package org.example.gridgestagram.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String credentials;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        super.setAuthenticated(false);
    }

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
