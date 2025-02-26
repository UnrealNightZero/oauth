package com.example.service;

import com.example.model.AuthType;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //註冊帳號
    public User createUser(String name, String email, String rawPassword, Role role) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 加密密碼
        user.setRole(role);
        user.setAuthType(AuthType.LOCAL);
        return userRepository.save(user);
    }
    public User findOrCreateGoogleUser(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        return userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(userInfo));
    }

    private User createGoogleUser(Map<String, Object> userInfo) {
        User user = new User();
        user.setEmail((String) userInfo.get("email"));
        user.setName((String) userInfo.get("name"));
        user.setAuthType(AuthType.GOOGLE);  // 設置認證類型
        user.setRole(Role.USER);            // 默認角色
        return userRepository.save(user);
    }
}
