package com.FeedEmGreens.HealthyAura.config;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.entity.Reward;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.repository.PointsRepository;
import com.FeedEmGreens.HealthyAura.repository.RewardRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration class responsible for seeding initial data into the database upon application startup.
 *
 * <p>This includes:
 * <ul>
 *   <li>Default reward entries (if none exist)</li>
 *   <li>A default admin account with associated points record</li>
 * </ul>
 *
 * The seeding process ensures that the application has essential baseline data for use during
 * local development and deployment. The configuration is excluded from the "test" profile
 * to avoid interfering with automated testing environments.
 * </p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@Configuration
@org.springframework.context.annotation.Profile("!test")  // <-- add this line
public class DatabaseSeeder {

    /**
     * Initializes the default set of {@link Reward} entries in the database.
     *
     * <p>This method is executed automatically at startup through Spring Boot’s {@link CommandLineRunner}.
     * It checks if the rewards table is empty and inserts a predefined list of sample rewards
     * (e.g., “Free Healthy Drink”, “10% Discount Voucher”, etc.). If rewards already exist, seeding is skipped.</p>
     *
     * @param rewardRepository the {@link RewardRepository} used to interact with the rewards table
     * @return a {@link CommandLineRunner} that performs the reward initialization
     */

    //   Reward Seeder
    @Bean
    CommandLineRunner initRewards(RewardRepository rewardRepository) {
        return args -> {
            if (rewardRepository.count() == 0) {
                List<Reward> rewards = List.of(
                        new Reward("Free Healthy Drink", "Redeem a sugar-free drink at participating hawker stalls.", 100),
                        new Reward("10% Discount Voucher", "Get 10% off at selected healthy eateries.", 150),
                        new Reward("Free Salad Bowl", "Enjoy one free salad bowl from partner stalls.", 250),
                        new Reward("Protein Snack Bar", "Redeem a free protein snack bar at HealthyAura kiosk.", 80),
                        new Reward("Reusable Bottle", "Get an eco-friendly reusable bottle.", 300)
                );
                rewardRepository.saveAll(rewards);
                System.out.println(" Dummy rewards loaded into database.");
            } else {
                System.out.println("Rewards already exist, skipping seeding.");
            }
        };
    }

    /**
     * Seeds the database with a default administrator account and initializes its corresponding points record.
     *
     * <p>The default admin account has:
     * <ul>
     *   <li>Username: <code>admin</code></li>
     *   <li>Email: <code>admin@healthyaura.com</code></li>
     *   <li>Password: <code>admin123</code> (hashed with {@link BCryptPasswordEncoder})</li>
     *   <li>Role: <code>ADMIN</code></li>
     * </ul>
     * If an admin already exists, seeding is skipped to prevent duplication.</p>
     *
     * @param userRepository the {@link UserRepository} for persisting admin user data
     * @param pointsRepository the {@link PointsRepository} for creating the admin’s associated points record
     * @return a {@link CommandLineRunner} that performs admin account initialization
     */

    //  Admin Seeder that creates the first admin account
    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PointsRepository pointsRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@healthyaura.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);

                //Points record created for this admin
                Points points = new Points();
                points.setUser(admin);
                points.setTotalPoints(0);
                points.setRedeemedPoints(0);
                points.setLastUpdated(LocalDateTime.now());
                pointsRepository.save(points);

                System.out.println("Created default admin account: admin (password: admin123)");
            } else {
                System.out.println("Admin user already exists, skipping seeding.");
            }
        };
    }

    /**
     * (Optional) Seeds a sample user account and its associated points record.
     *
     * <p>This block is commented out by default but can be enabled for testing or demonstration purposes.
     * It creates a user named <code>rajath</code> with an initial 500 points.</p>
     *
     * @param userRepository the {@link UserRepository} for saving the sample user
     * @param pointsRepository the {@link PointsRepository} for creating the user’s points record
     * @return a {@link CommandLineRunner} that seeds the user and points data (if enabled)
     */

    //  New User + Points Seeder
    //@Bean
    /*CommandLineRunner initUserAndPoints(UserRepository userRepository, PointsRepository pointsRepository) {
        return args -> {
            if (userRepository.findByUsername("rajath").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Users user = new Users();
                user.setUsername("rajath");
                user.setEmail("rajath@example.com");
                user.setPassword(encoder.encode("123")); // securely store password
                user.setRole("USER");
                userRepository.save(user);

                // Seed his points
                Points points = new Points();
                points.setUser(user);
                points.setTotalPoints(500); // give some starting points
                points.setRedeemedPoints(0);
                points.setLastUpdated(LocalDateTime.now());
                pointsRepository.save(points);

                System.out.println(" Created default user: rajath (password: 123, 500 starting points)");
            } else {
                System.out.println("User 'rajath' already exists, skipping seeding.");
            }
        };
    }*/
}
