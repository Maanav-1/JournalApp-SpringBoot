package com.chellanim.journalApp.controller;

import com.chellanim.journalApp.annotation.RateLimit;
import com.chellanim.journalApp.dto.UserDTO;
import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.service.CustomUserDetailsService;
import com.chellanim.journalApp.service.UserService;
import com.chellanim.journalApp.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private JwtUtils jwtUtils;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDto) {
    User user = new User();
    user.setUserName(userDto.getUserName());
    user.setPassword(userDto.getPassword());
    user.setEmail(userDto.getEmail());
    userService.saveNewUser(user);
    log.info("New user registered: {}", userDto.getUserName());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
  @RateLimit(attempts = 5, windowMinutes = 15)
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody UserDTO userDto) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
      String jwt = jwtUtils.generateToken(userDto.getUserName());
      log.info("User {} logged in successfully", userDto.getUserName());
      return new ResponseEntity<>(jwt, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Login failed for user: {}", userDto.getUserName());
      return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
    }
  }
}