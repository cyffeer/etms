package org.fujitsu.codes.etms.controller;

import java.util.List;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.dao.LoginDao;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.LoginRequest;
import org.fujitsu.codes.etms.model.dto.LoginResponse;
import org.fujitsu.codes.etms.validator.LoginValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    private final LoginDao loginDao;

    public LoginRestController(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        List<String> errors = LoginValidator.validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        try {
            Login login = loginDao.authenticate(request.getUsername(), request.getPassword());

            LoginResponse response = new LoginResponse();
            response.setLoginId(login.getLoginId());
            response.setUsername(login.getUsername());
            response.setRole(login.getRole());

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Authentication failed", List.of(ex.getMessage())));
        }
    }
}
