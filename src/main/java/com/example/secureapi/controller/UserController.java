package com.example.secureapi.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.secureapi.dto.LoginDto;
import com.example.secureapi.dto.UserDto;
import com.example.secureapi.model.User;
import com.example.secureapi.service.UserService;
import com.example.secureapi.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")

public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> USER = userService.getUserById(id);
        if (USER.isPresent()) {
            return ResponseEntity.ok(USER.get());
        } else {
            return ResponseEntity.notFound().build(); // 404 if USER not found
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String token = userService.login(loginDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            String role = jwtUtil.getRoleFromToken(refreshToken);
            String newAccessToken = jwtUtil.generateToken(username, role);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}