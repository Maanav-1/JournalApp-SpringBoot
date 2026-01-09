package com.chellanim.journalApp.controller;

import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.respository.UserRepository;
import com.chellanim.journalApp.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

//  @GetMapping
//  public List<User> getALl(){
//    return userService.getAll();
//  }

  @GetMapping
  public ResponseEntity<?> getUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userService.findByUserName(username);

    if (user != null) {
      // Create a dynamic response map to avoid modifying UserDTO
      Map<String, Object> response = new HashMap<>();
      response.put("userName", user.getUserName());
      response.put("email", user.getEmail());
      response.put("roles", user.getRoles()); // Roles from the User entity

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }


  @PutMapping
  public ResponseEntity<?> updateUser(@RequestBody User user) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User u = userService.findByUserName(username);
    if (u != null) {
      u.setUserName(user.getUserName());
      u.setPassword(user.getPassword());
      userService.saveNewUser(u);

    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  @Autowired
  private UserRepository userRepository;

  @DeleteMapping
  public ResponseEntity<?> deleteUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    userRepository.deleteByUserName(authentication.getName());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }


}