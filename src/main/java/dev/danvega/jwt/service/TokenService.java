package dev.danvega.jwt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;


    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Authentication authentication) {

        log.info("Generating token for user: {}", authentication.getName());
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().iterator().next().getAuthority();

        assert scope != null;
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        log.info("Generated token for user: {}", authentication.getName());

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
