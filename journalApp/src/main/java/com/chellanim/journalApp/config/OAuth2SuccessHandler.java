package com.chellanim.journalApp.config;

import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.service.UserService;
import com.chellanim.journalApp.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired private UserService userService;
  @Autowired private JwtUtils jwtUtils;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
    String githubUsername = oauthUser.getAttribute("login");
    String email = oauthUser.getAttribute("email");

    // Map GitHub user to local User entity
    User user = userService.findByUserName(githubUsername);
    if (user == null) {
      user = new User();
      user.setUserName(githubUsername);
      user.setPassword("OAUTH_USER"); // Placeholder for non-password login
      user.setEmail(email);
      user.setRoles(List.of("USER"));
      userService.saveEntry(user); // Save new mapped user
    }

    // Generate stateless JWT
    String token = jwtUtils.generateToken(githubUsername);

    // Redirect to frontend with token in URL (or handle as per enterprise frontend needs)
    response.sendRedirect("http://localhost:5173/public/oauth-callback?token=" + token);
  }
}