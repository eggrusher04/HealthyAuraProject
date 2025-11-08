package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;

/**
 * Entity representing a user account within the HealthyAura system.
 *
 * <p>Each {@code Users} record stores authentication credentials, contact details,
 * role information, and user-specific preferences. This entity is the central
 * link connecting the authentication, rewards, and review subsystems.</p>
 *
 * <p>Entries are stored in the <b>users</b> table. User records can represent:</p>
 * <ul>
 *   <li>Standard users — who can log in, submit reviews, and earn/redeem points.</li>
 *   <li>Administrators — who can manage reviews, handle flags, and oversee moderation.</li>
 * </ul>
 *
 * <p>This entity has a one-to-one relationship with {@link Points}, linking each user
 * to their reward points record.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.controller.loginController
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 * @see com.FeedEmGreens.HealthyAura.dto.ProfileResponse
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "users")
public class Users {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user's chosen username (must be unique). */
    @Column(unique = true, nullable = false)
    private String username;

    /** The user's registered email address. */
    @Column(nullable = false)
    private String email;

    /** The user's encrypted password (hashed before persistence). */
    @Column(nullable = false)
    private String password;

    /**
     * The role assigned to the user, determining access privileges.
     * <p>Typical values include:
     * <ul>
     *   <li><b>USER</b> — standard application user</li>
     *   <li><b>ADMIN</b> — administrator with elevated privileges</li>
     * </ul>
     * </p>
     */
    private String role = "USER";

    /**
     * Optional field storing user dietary or lifestyle preferences.
     * <p>Used for generating personalized recommendations and filtering eateries.</p>
     * Example: <code>"Vegan, Low Sugar, High Protein"</code>
     */
    private String preferences;

    /**
     * One-to-one relationship mapping this user to their points record.
     * <p>Each user has exactly one {@link Points} record used to track total and redeemed points.</p>
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Points points;

    /** Default constructor required by JPA. */
    public Users() {}

    /**
     * Constructs a new user with the specified username, email, and password.
     * <p>By default, the user role is set to <b>USER</b>.</p>
     *
     * @param username the user's chosen username
     * @param email the user's registered email
     * @param password the user's hashed password
     */
    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "USER";
    }

    // ------------------- GETTERS AND SETTERS ------------------- //

    /** @return the unique ID of the user */
    public Long getId() { return id; }

    /** @param id sets the user’s unique identifier */
    public void setId(Long id) { this.id = id; }

    /** @return the username */
    public String getUsername() { return username; }

    /** @param username sets the username (must be unique) */
    public void setUsername(String username) { this.username = username; }

    /** @return the user's email */
    public String getEmail() { return email; }

    /** @param email sets the user’s email */
    public void setEmail(String email) { this.email = email; }

    /** @return the encrypted password */
    public String getPassword() { return password; }

    /** @param password sets the user’s encrypted password */
    public void setPassword(String password) { this.password = password; }

    /** @return the user’s assigned role */
    public String getRole() { return role; }

    /** @param role sets the user’s role (USER or ADMIN) */
    public void setRole(String role) { this.role = role; }

    /** @return the user’s saved dietary or lifestyle preferences */
    public String getPreferences() { return preferences; }

    /** @param preferences sets the user’s dietary or lifestyle preferences */
    public void setPreferences(String preferences) { this.preferences = preferences; }

    /** @return the {@link Points} entity associated with this user */
    public Points getPoints() { return points; }

    /**
     * Sets the user’s {@link Points} record and ensures the bidirectional relationship
     * is maintained by assigning this user back to the {@link Points} entity.
     *
     * @param points the points record associated with this user
     */
    public void setPoints(Points points) {
        this.points = points;
        if (points != null && points.getUser() != this) {
            points.setUser(this);
        }
    }
}
