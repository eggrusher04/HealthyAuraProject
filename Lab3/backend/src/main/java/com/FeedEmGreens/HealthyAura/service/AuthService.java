package com.FeedEmGreens.HealthyAura.service;



import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.repository.PointsRepository;
import com.FeedEmGreens.HealthyAura.security.JwtUtil;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.dto.AuthResponse;
import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//Service class handles the business logic to decide what happens and when. It uses repositories and flow/rules of the app

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PointsRepository pointsRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    public Users signup(String email, String username, String rawPassword, String role){
        if (userRepo.existsByUsername(username)) {
            throw new DuplicateUserException("Username already exists");
        }
        if (userRepo.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("Email already exists");
        }

        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("USER");

        //  Save user first
        Users savedUser = userRepo.save(user);

        // Create empty points record linked to the saved user
        Points points = new Points();
        points.setUser(savedUser);
        points.setTotalPoints(0);
        points.setRedeemedPoints(0);

        // Save the points entity
        pointsRepository.save(points);

        // Set the reference back into the user object
        savedUser.setPoints(points);
        return userRepo.save(savedUser);
    }

    public Users createAdmin(String email, String username, String rawPassword,String role){
        if (userRepo.existsByUsername(username)) {
            throw new DuplicateUserException("Username already exists");
        }
        if (userRepo.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("Email already exists");
        }


        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("ADMIN");

        //  Save user first
        Users savedAdmin = userRepo.save(user);

        Points points = new Points();
        points.setUser(savedAdmin);
        points.setTotalPoints(0);
        points.setRedeemedPoints(0);
        pointsRepository.save(points);

        savedAdmin.setPoints(points);
        return userRepo.save(savedAdmin);
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
