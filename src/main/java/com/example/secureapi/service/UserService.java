package com.example.secureapi.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.secureapi.dto.LoginDto;
import com.example.secureapi.dto.UserDto;
import com.example.secureapi.model.User;
import com.example.secureapi.model.User.Role;
import com.example.secureapi.repository.UserRepository;
import com.example.secureapi.util.JwtUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void registerUser(UserDto userDto) {
        User USER = new User();
        USER.setUsername(userDto.getUsername());
        USER.setPassword(passwordEncoder.encode(userDto.getPassword()));
        USER.setRole(Role.USER);
        userRepository.save(USER);
    }

    public String login(LoginDto loginDto) {
        User USER = userRepository.findByUsername(loginDto.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDto.getPassword(), USER.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(USER.getUsername(), USER.getRole().name());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); 
    }
}
