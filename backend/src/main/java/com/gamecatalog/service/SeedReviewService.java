package com.gamecatalog.service;

import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeedReviewService {
    private static final Long LEGACY_SYSTEM_USER_ID = 1L;

    private static final String[] POSITIVE_REVIEWS = {
        "Absolutely amazing game! The story kept me hooked from start to finish.",
        "One of the best games I've played this year. Highly recommend!",
        "Great gameplay mechanics and stunning visuals. Worth every penny!",
        "The attention to detail is incredible. A masterpiece!",
        "Loved every minute of it. Can't wait for the sequel!",
        "Perfect balance of challenge and fun. 10/10 would play again.",
        "The soundtrack alone is worth it. Gameplay is top-notch too!",
        "This game exceeded all my expectations. Truly phenomenal.",
        "Engaging story, smooth controls, and beautiful graphics. What more could you ask for?",
        "A must-play for fans of the genre. Absolutely brilliant!"
    };

    private static final String[] MIXED_REVIEWS = {
        "Good game overall, but the pacing could be better in some areas.",
        "Solid gameplay but the story felt a bit predictable.",
        "Great graphics and sound, but gameplay can get repetitive after a while.",
        "Enjoyable experience, though it has a few technical issues.",
        "Fun game but not without its flaws. Still worth playing.",
        "The concept is great but execution could be improved.",
        "Decent game with some memorable moments, but also some frustrating parts.",
        "Good but not great. Has potential but falls short in some areas.",
        "Entertaining enough, though it doesn't bring anything revolutionary to the table.",
        "Worth a playthrough, but don't expect perfection."
    };

    private static final String[] CRITICAL_REVIEWS = {
        "Had high hopes but was disappointed. Too many bugs and glitches.",
        "The gameplay feels outdated compared to similar titles.",
        "Not bad, but there are much better options in this genre.",
        "Struggled to stay engaged. The story just didn't click for me.",
        "Mediocre at best. Expected more based on the hype.",
        "Some good ideas but poor execution overall.",
        "The controls feel clunky and unresponsive at times.",
        "Repetitive gameplay loop gets boring quickly.",
        "Not worth the full price. Wait for a sale.",
        "Disappointing compared to previous entries in the series."
    };

    private static final Set<String> SEED_REVIEW_TEXTS = buildSeedReviewTexts();

    private final CommentRepository commentRepository;
    private final MockReviewerService mockReviewerService;
    private final Random random = new Random();

    public SeedReviewService(CommentRepository commentRepository, MockReviewerService mockReviewerService) {
        this.commentRepository = commentRepository;
        this.mockReviewerService = mockReviewerService;
    }

    public void createReviews(Product product, Double rating) {
        int reviewCount = 2 + random.nextInt(2);
        List<User> reviewers = mockReviewerService.getOrCreateReviewers();
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < reviewCount; i++) {
            User reviewer = reviewers.get(random.nextInt(reviewers.size()));
            Comment comment = new Comment(selectReviewText(rating), reviewer.getId(), product);
            comment.setCreationDate(randomRecentDate());
            comments.add(comment);
        }

        commentRepository.saveAll(comments);
    }

    public void repairLegacyOwners(Long productId) {
        List<Comment> legacyComments = commentRepository.findByProductIdAndIsDeletedFalse(productId)
                .stream()
                .filter(comment -> LEGACY_SYSTEM_USER_ID.equals(comment.getCreatorUserId()))
                .filter(comment -> SEED_REVIEW_TEXTS.contains(comment.getDescription()))
                .collect(Collectors.toList());

        if (legacyComments.isEmpty()) {
            return;
        }

        List<User> reviewers = mockReviewerService.getOrCreateReviewers();
        for (Comment comment : legacyComments) {
            User reviewer = reviewers.get(random.nextInt(reviewers.size()));
            comment.setCreatorUserId(reviewer.getId());
        }

        commentRepository.saveAll(legacyComments);
    }

    private String selectReviewText(Double rating) {
        if (rating == null) {
            return randomText(MIXED_REVIEWS);
        }
        if (rating >= 80) {
            return randomText(POSITIVE_REVIEWS);
        }
        if (rating >= 60) {
            return randomText(MIXED_REVIEWS);
        }
        return randomText(CRITICAL_REVIEWS);
    }

    private String randomText(String[] source) {
        return source[random.nextInt(source.length)];
    }

    private LocalDateTime randomRecentDate() {
        return LocalDateTime.now()
                .minusDays(random.nextInt(90))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
    }

    private static Set<String> buildSeedReviewTexts() {
        Set<String> texts = new HashSet<>();
        addAll(texts, POSITIVE_REVIEWS);
        addAll(texts, MIXED_REVIEWS);
        addAll(texts, CRITICAL_REVIEWS);
        return texts;
    }

    private static void addAll(Set<String> texts, String[] source) {
        for (String text : source) {
            texts.add(text);
        }
    }
}
