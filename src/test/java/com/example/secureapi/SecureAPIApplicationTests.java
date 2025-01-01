package com.example.secureapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.secureapi.model.User;
import com.example.secureapi.repository.UserRepository;
import com.example.secureapi.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class SecureAPIApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private String userJwt;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userJwt = "Bearer " + jwtUtil.generateToken("test_user", "USER");

        userRepository.deleteAll(); 
        User testUser = new User("test_user", passwordEncoder.encode("password123"), User.Role.USER);
        userRepository.save(testUser);
    
        User adminUser = new User("admin_user", passwordEncoder.encode("adminpassword"), User.Role.ADMIN);
        userRepository.save(adminUser);
    }

    @Test
    void contextLoads() {
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testGetUserById() throws Exception {
        User testUser = userRepository.findByUsername("test_user").orElseThrow();

        mockMvc.perform(get("/api/users/" + testUser.getId())
            .header("Authorization", userJwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    public void testLoginWithRole() throws Exception {
        User adminUser = userRepository.findByUsername("admin_user").orElseThrow();
        String adminJwt = jwtUtil.generateToken("admin_user", "ADMIN");

        mockMvc.perform(get("/api/users/" + adminUser.getId())
                .header("Authorization", adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testGetUser() throws Exception {
        User testUser = userRepository.findByUsername("test_user").orElseThrow();
    
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("Authorization", userJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"));
    }
  
    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testCreateUser() throws Exception {
        String userJson = "{\"username\":\"new_user\",\"password\":\"newpassword\"}";
    
        mockMvc.perform(post("/api/users")
                .header("Authorization", userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));
    
        User newUser = userRepository.findByUsername("new_user").orElseThrow();
        assert newUser.getUsername().equals("new_user");
    }
    

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        User testUser = userRepository.findByUsername("test_user").orElseThrow();
        String updatedUserJson = "{\"username\":\"updated_user\",\"password\":\"updatedpassword\"}";

        mockMvc.perform(put("/api/users/" + testUser.getId())
                .header("Authorization", userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getUsername().equals("updated_user");
    }


    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testDeleteUser() throws Exception {
        User testUser = userRepository.findByUsername("test_user").orElseThrow();
    
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                .header("Authorization", userJwt))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    
        assert userRepository.findById(testUser.getId()).isEmpty();
    }
    
    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testUserRegistrationValidation() throws Exception {
        String invalidUserJson = "{\"password\":\"password123\"}";
    
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("username must not be blank")));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testJwtAuthentication() throws Exception {
        String loginJson = "{\"username\":\"test_user\",\"password\":\"password123\"}";
    
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testRateLimiting() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/protected-endpoint")
                    .header("Authorization", userJwt))
                    .andExpect(status().isOk());
        }
    
        mockMvc.perform(get("/protected-endpoint")
                .header("Authorization", userJwt))
                .andExpect(status().isTooManyRequests());
    }    

    @Test
    @WithMockUser(username = "test_user", roles = {"USER"})
    public void testGlobalExceptionHandler() throws Exception {
        String invalidJson = "{\"invalid\":\"data\"}";
    
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Validation error")));
    }

    @Test
    public void testRefreshToken() throws Exception {
        String refreshToken = jwtUtil.generateRefreshToken("test_user");
    
        mockMvc.perform(post("/api/users/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }        

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isUnauthorized());
    }
}