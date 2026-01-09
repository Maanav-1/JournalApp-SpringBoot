package com.chellanim.journalApp.service;

import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUserName(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUserName())
        .password(user.getPassword())
        .roles(user.getRoles().toArray(new String[0]))
        .build();
  }
}
