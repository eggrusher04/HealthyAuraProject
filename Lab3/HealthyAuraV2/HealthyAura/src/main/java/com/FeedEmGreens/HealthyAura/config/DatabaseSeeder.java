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

@Configuration
public class DatabaseSeeder {

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
