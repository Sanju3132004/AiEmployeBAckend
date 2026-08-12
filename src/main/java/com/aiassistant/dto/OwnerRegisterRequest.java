package com.aiassistant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OwnerRegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    // Must match app.owner-setup-key from application.properties.
    // This is a one-time bootstrap mechanism to create the very first ADMIN (owner) account.
    @NotBlank
    private String setupKey;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSetupKey() {
		return setupKey;
	}

	public void setSetupKey(String setupKey) {
		this.setupKey = setupKey;
	}
}
