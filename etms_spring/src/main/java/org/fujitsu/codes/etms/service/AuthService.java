package org.fujitsu.codes.etms.service;

import java.util.Base64;
import java.util.Optional;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.dao.LoginDao;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final LoginDao loginDao;
    private final JwtService jwtService;

    public AuthService(LoginDao loginDao, JwtService jwtService) {
        this.loginDao = loginDao;
        this.jwtService = jwtService;
    }

    public Login authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username is required");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password is required");
        }

        return loginDao.findByUsername(username.trim())
                .filter(user -> user.getPassword() != null && user.getPassword().equals(password))
                .orElseThrow(() -> new InvalidInputException("Invalid username or password"));
    }

    public Login authenticateBasicHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new InvalidInputException("Authorization header is required");
        }

        if (authorizationHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authenticateBearerHeader(authorizationHeader);
        }

        if (!authorizationHeader.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new InvalidInputException("Authorization header must use Bearer or Basic authentication");
        }

        String token = authorizationHeader.substring(6).trim();
        if (token.isEmpty()) {
            throw new InvalidInputException("Authorization token is required");
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            int separatorIndex = decoded.indexOf(':');
            if (separatorIndex < 0) {
                throw new InvalidInputException("Authorization token must contain username and password");
            }

            String username = decoded.substring(0, separatorIndex);
            String password = decoded.substring(separatorIndex + 1);
            return authenticate(username, password);
        } catch (IllegalArgumentException ex) {
            throw new InvalidInputException("Authorization token is not valid Base64");
        }
    }

    public Login authenticateBearerHeader(String authorizationHeader) {
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new InvalidInputException("Bearer token is required");
        }

        String username = jwtService.extractUsername(token);
        UserRole role = jwtService.extractRole(token);

        Login login = findByUsername(username)
                .orElseThrow(() -> new InvalidInputException("User account does not exist"));
        login.setRole(role);
        return login;
    }

    public String issueToken(Login login) {
        return jwtService.generateToken(login);
    }

    public long getTokenExpirationSeconds() {
        return jwtService.getExpirationSeconds();
    }

    public Optional<Login> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return loginDao.findByUsername(username.trim());
    }
}
