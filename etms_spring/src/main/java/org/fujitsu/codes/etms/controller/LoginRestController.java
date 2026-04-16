package org.fujitsu.codes.etms.controller;

import java.util.List;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.LoginRequest;
import org.fujitsu.codes.etms.model.dto.LoginResponse;
import org.fujitsu.codes.etms.service.AuditTrailService;
import org.fujitsu.codes.etms.service.AuthService;
import org.fujitsu.codes.etms.validator.LoginValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginRestController.class);

    private final AuthService authService;
    private final AuditTrailService auditTrailService;

    public LoginRestController(AuthService authService, AuditTrailService auditTrailService) {
        this.authService = authService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login request received for username={}", request == null ? null : request.getUsername());
        List<String> errors = LoginValidator.validate(request);
        if (!errors.isEmpty()) {
            log.warn("Login validation failed for username={}", request == null ? null : request.getUsername());
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        try {
            Login login = authService.authenticate(request.getUsername(), request.getPassword());

            LoginResponse response = new LoginResponse();
            response.setLoginId(login.getLoginId());
            response.setUsername(login.getUsername());
            response.setRole(login.getRole() == null ? null : login.getRole().name());
            response.setAccessToken(authService.issueToken(login));
            response.setTokenType("Bearer");
            response.setExpiresInSeconds(authService.getTokenExpirationSeconds());
            auditTrailService.log(login, "LOGIN", "AUTH", login.getUsername(), "User logged in successfully", null);
            log.info("Login success for username={}", login.getUsername());

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (InvalidInputException ex) {
            log.error("Login failed for username={}", request.getUsername(), ex);
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Authentication failed", List.of(ex.getMessage())));
        }
    }
}
