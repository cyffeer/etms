package org.fujitsu.codes.etms.model.dto;

public class LoginResponse {

    private Long loginId;
    private String username;
    private String role;

    // Getters and Setters
    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}