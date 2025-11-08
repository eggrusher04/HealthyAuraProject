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

/**
 * Service layer handling authentication and user account management logic
 * for the HealthyAura application.
 *
 * <p>This class encapsulates the core business rules for:
 * <ul>
 *   <li>User registration (for both standard users and administrators)</li>
 *   <li>Credential validation and login authentication</li>
 *   <li>JWT token generation for secure client sessions</li>
 *   <li>Automatic initialization of a {@link Points} record upon signup</li>
 * </ul>
 * </p>
 *
 * <p>By separating this logic from the controller, the service promotes a clean
 * architecture following the <b>Service–Repository</b> pattern, where each component
 * focuses on a single responsibility.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.security.JwtUtil
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.repository.UserRepository
 * @see com.FeedEmGreens.HealthyAura.repository.PointsRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class AuthService {

    /** Repository for user data persistence and retrieval. */
    @Autowired
    private UserRepository userRepo;

    /** Utility for generating and validating JWT tokens. */
    @Autowired
    private JwtUtil jwtUtil;

    /** Repository managing points records associated with users. */
    @Autowired
    private PointsRepository pointsRepository;

    /** Encoder for securely hashing user passwords before storage. */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Custom runtime exception thrown when a user tries to register
     * with a duplicate username or email.
     */
    public class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    /**
     * Registers a new standard user in the system.
     *
     * <p>This method:
     * <ol>
     *   <li>Validates that the username and email are unique.</li>
     *   <li>Encodes the password using {@link BCryptPasswordEncoder}.</li>
     *   <li>Persists the new {@link Users} entity.</li>
     *   <li>Creates and associates a {@link Points} record initialized with 0 points.</li>
     * </ol>
     * </p>
     *
     * @param email the email address of the new user
     * @param username the unique username of the user
     * @param rawPassword the plain-text password (to be encoded)
     * @param role the user role (typically "USER")
     * @return the newly registered {@link Users} entity
     * @throws DuplicateUserException if username or email already exists
     */
    public Users signup(String email, String username, String rawPassword, String role) {
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

        // Save user first
        Users savedUser = userRepo.save(user);

        // Initialize and associate points record
        Points points = new Points();
        points.setUser(savedUser);
        points.setTotalPoints(0);
        points.setRedeemedPoints(0);

        pointsRepository.save(points);
        savedUser.setPoints(points);
        return userRepo.save(savedUser);
    }

    /**
     * Registers a new administrator account.
     *
     * <p>Similar to {@link #signup}, but sets the role as "ADMIN" instead of "USER".
     * The method also initializes an empty {@link Points} record for the admin user.</p>
     *
     * @param email the email address of the admin
     * @param username the desired admin username
     * @param rawPassword the plain-text password
     * @param role the user role (typically "ADMIN")
     * @return the newly created admin {@link Users} entity
     * @throws DuplicateUserException if username or email already exists
     */
    public Users createAdmin(String email, String username, String rawPassword, String role) {
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

        Users savedAdmin = userRepo.save(user);

        Points points = new Points();
        points.setUser(savedAdmin);
        points.setTotalPoints(0);
        points.setRedeemedPoints(0);

        pointsRepository.save(points);
        savedAdmin.setPoints(points);
        return userRepo.save(savedAdmin);
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * <p>The method performs:
     * <ul>
     *   <li>Username lookup in the database</li>
     *   <li>Password verification using BCrypt</li>
     *   <li>JWT generation with embedded username and role claims</li>
     * </ul>
     * </p>
     *
     * <p>Console logging is used to trace authentication flow during development.</p>
     *
     * @param username the username provided by the client
     * @param rawPassword the password entered during login
     * @return an {@link AuthResponse} containing the JWT token and user details
     * @throws RuntimeException if the username does not exist or the password is invalid
     */
    public AuthResponse login(String username, String rawPassword) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + username);
        System.out.println("Raw password: " + rawPassword);

        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("USER NOT FOUND: " + username);
                    return new RuntimeException("User not found");
                });

        System.out.println("User found: " + user.getUsername());
        System.out.println("Stored hash: " + user.getPassword());
        System.out.println("User role: " + user.getRole());

        boolean passwordMatches = encoder.matches(rawPassword, user.getPassword());
        System.out.println("Password matches: " + passwordMatches);

        if (!passwordMatches) {
            System.out.println("PASSWORD MISMATCH for user: " + username);
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        System.out.println("LOGIN SUCCESSFUL - Token generated");
        System.out.println("=== LOGIN COMPLETE ===");

        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}
