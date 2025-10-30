// backend/src/main/java/com/FeedEmGreens/HealthyAura/HashGenerator.java (Temporary File)

package com.FeedEmGreens.HealthyAura;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encodedPassword = encoder.encode(password);
        System.out.println("New BCrypt Hash for 'admin123': " + encodedPassword);
    }
}