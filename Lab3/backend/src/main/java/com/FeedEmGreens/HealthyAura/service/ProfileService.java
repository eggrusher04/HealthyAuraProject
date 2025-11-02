package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Fetch profile details
    public Users getUserProfile(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        return userOpt.get();
    }

    // Update preferences field
    public Users updatePreferences(String username, String preferences) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        user.setPreferences(preferences);
        return userRepository.save(user);
    }

    // Create and assign Points entity to user if it doesn't exist
    public void initializeUserPoints(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        if (user.getPoints() == null) {
            Points points = new Points(user);
            user.setPoints(points);
            userRepository.save(user);
        }
    }

    public Users updateEmail(String username, String newEmail) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    public void updatePassword(String username, String rawPassword) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(encoder.encode(rawPassword));
        userRepository.save(user);
    }
}