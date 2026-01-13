package dev.danvega.jwt.controller;

import dev.danvega.jwt.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        log.debug("Token request for user: {}", authentication.getName());
        String generatedToken = tokenService.generateToken(authentication);
        log.debug("Token generated for user: {}", authentication.getName());
        return generatedToken;
    }
}
