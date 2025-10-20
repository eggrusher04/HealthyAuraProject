package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.entity.Points;

import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    //Fetch profile details
    public Users getUserProfile(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        return userOpt.get();
    }

    //Update preferences field
    public void updatePreferences(String username, String preferences) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        user.setPreferences(preferences); // your manual setter
        userRepository.save(user);
    }

    //Optional: Link a new Points record to a user (only if needed)
    public void assignPointsEntityToUser(String username, Points pointsEntity) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        user.setPoints(pointsEntity);
        userRepository.save(user);
    }
}
