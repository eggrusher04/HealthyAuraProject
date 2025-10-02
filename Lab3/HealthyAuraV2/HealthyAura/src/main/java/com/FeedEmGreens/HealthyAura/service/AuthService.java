package com.FeedEmGreens.HealthyAura.service;



import com.FeedEmGreens.HealthyAura.security.JwtUtil;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.dto.AuthResponse;
import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Users signup(String email, String username, String rawPassword, String role){
        if(userRepo.existsByUsername(username)){
            throw new RuntimeException("Username already in use");
        }
        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("USER");

        return userRepo.save(user);
    }

    public Users createAdmin(String email, String username, String rawPassword,String role){
        if(userRepo.existsByUsername(username)){
            throw new RuntimeException("Username already in use");
        }
        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("ADMIN");

        return userRepo.save(user);
    }

    public AuthResponse login(String username, String rawPassword){
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!encoder.matches(rawPassword,user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token,user.getUsername(), user.getRole());
    }

}
