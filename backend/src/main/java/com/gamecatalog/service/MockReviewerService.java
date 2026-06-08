package com.gamecatalog.service;

import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MockReviewerService {
    private static final String[] REVIEWER_USERNAMES = {
        "AlexReview",
        "PixelNina",
        "RetroMax",
        "IndieVika",
        "QuestLeo",
        "ArcadeMila"
    };

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MockReviewerService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getOrCreateReviewers() {
        List<User> reviewers = new ArrayList<>();

        for (String username : REVIEWER_USERNAMES) {
            User reviewer = userRepository.findByUsernameAndIsDeletedFalse(username)
                    .orElseGet(() -> createReviewer(username));
            reviewers.add(reviewer);
        }

        return reviewers;
    }

    private User createReviewer(String username) {
        return userRepository.save(new User(
                username,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                "ROLE_REVIEWER"
        ));
    }
}
