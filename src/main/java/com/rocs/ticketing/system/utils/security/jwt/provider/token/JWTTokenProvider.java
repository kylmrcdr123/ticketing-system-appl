package com.rocs.ticketing.system.utils.security.jwt.provider.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rocs.ticketing.system.domain.user.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.rocs.ticketing.system.utils.security.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generates JWT token
     * @param userPrincipal whenever a user logs in, details are extracted from the user principal
     *
     * @return the jwt token
     * */
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);

        return JWT.create()
                .withIssuer(ROCS)
                .withAudience(ROCS_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * Get authorities from the token.
     * @param token the jwt token
     * @return list of authorities from the token
     * */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Gets the authentication of the user. If the token is correct, then security can get the authentication of the user
     * and set that authentication in the spring security context.
     *
     * @param username username of the user.
     * @param authorities list of authorities of the user.
     * @param request the user request
     * @return authentication of the user.
     * */
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPassAuthToken;
    }

    /**
     * Checks if the token is valid.
     * @param username the username of the user.
     * @param token the jwt token
     * @return true if the token is valid.
     * */
    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    /**
     * Gets the subject from the token.
     * @param token the jwt token
     * @return the subject from the token.
     * */
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }
    /**
     * Get claims (authorities) from the UserPrincipal
     *
     * @param user the UserPrincipal
     * @return array of claims (authorities)
     */
    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority grantedAuthority: user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
    /**
     * Gets claims (authorities) from the JWT token.
     *
     * @param token JWT token
     * @return array of claims (authorities) from the token
     */
    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }
    /**
     * Get a JWTVerifier object using the secret key.
     *
     * @return a JWTVerifier
     */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(ROCS).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
    /**
     * Checks if the JWT token has expired.
     *
     * @param verifier JWTVerifier used to verify the token
     * @param token JWT token
     */
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }
}
