package com.chellanim.journalApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
  @NotBlank(message = "Username is required")
  private String userName;
  @NotBlank(message = "Password is required")
  private String password;
  @Email(message = "Invalid email format")
  private String email;
}