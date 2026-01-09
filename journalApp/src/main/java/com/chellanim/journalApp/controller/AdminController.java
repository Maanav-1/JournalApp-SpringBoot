package com.chellanim.journalApp.controller;

import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private UserService userService;

  @GetMapping("/all-users")
  public ResponseEntity<?> getAllUsers() {
    List<User> all= userService.getAll();
    if(all!=null && !all.isEmpty()){
      return new ResponseEntity<>(all,HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PostMapping("create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user) {
    userService.saveAdmin(user);
    return new ResponseEntity<>(HttpStatus.OK);
    }


}
