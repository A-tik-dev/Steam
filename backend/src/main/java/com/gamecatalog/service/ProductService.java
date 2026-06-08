package com.gamecatalog.service;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.CommentRepository;
import com.gamecatalog.repository.ProductRepository;
import com.gamecatalog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Manages local products and comments.
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    private static final Long SYSTEM_USER_ID = 1L;
    private static final String[] MOCK_REVIEWER_USERNAMES = {
        "AlexReview",
        "PixelNina",
        "RetroMax",
        "IndieVika",
        "QuestLeo",
        "ArcadeMila"
    };

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

    public ProductService(ProductRepository productRepository, CommentRepository commentRepository,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Saves a game if it does not exist yet, then seeds starter reviews.
    @Transactional
    public Product saveGameWithReviews(GameDTO gameDTO) {
        Product existingProduct = productRepository.findByIgdbId(gameDTO.getId());
        if (existingProduct != null) {
            repairLegacySeededReviewOwners(existingProduct.getId());
            return existingProduct;
        }

        Product product = new Product(
            gameDTO.getName(),
            gameDTO.getSummary(),
            SYSTEM_USER_ID,
            gameDTO.getCover() != null ? gameDTO.getCover().getUrl() : null
        );
        product.setIgdbId(gameDTO.getId());

        Product savedProduct = productRepository.save(product);

        int reviewCount = 2 + random.nextInt(2);
        List<Comment> comments = generateMockReviews(
                savedProduct,
                gameDTO.getRating(),
                reviewCount,
                getOrCreateMockReviewers()
        );
        commentRepository.saveAll(comments);

        return savedProduct;
    }

    // Generates fake starter comments for a product.
    private List<Comment> generateMockReviews(Product product, Double rating, int count, List<User> mockReviewers) {
        List<Comment> reviews = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String reviewText = selectReviewBasedOnRating(rating);
            LocalDateTime reviewDate = generateRandomRecentDate();
            User reviewer = mockReviewers.get(random.nextInt(mockReviewers.size()));

            Comment comment = new Comment(reviewText, reviewer.getId(), product);
            comment.setCreationDate(reviewDate);
            reviews.add(comment);
        }

        return reviews;
    }

    // Keeps seeded review authors separate from real registered users.
    private List<User> getOrCreateMockReviewers() {
        List<User> reviewers = new ArrayList<>();

        for (String username : MOCK_REVIEWER_USERNAMES) {
            User reviewer = userRepository.findByUsernameAndIsDeletedFalse(username)
                    .orElseGet(() -> userRepository.save(new User(
                            username,
                            passwordEncoder.encode(UUID.randomUUID().toString()),
                            "ROLE_REVIEWER"
                    )));
            reviewers.add(reviewer);
        }

        return reviewers;
    }

    // Older seeded comments were stored with creator_user_id=1, which can be a real user.
    private void repairLegacySeededReviewOwners(Long productId) {
        List<Comment> legacySeededReviews = commentRepository.findByProductIdAndIsDeletedFalse(productId)
                .stream()
                .filter(comment -> SYSTEM_USER_ID.equals(comment.getCreatorUserId()))
                .filter(comment -> isSeedReviewText(comment.getDescription()))
                .collect(Collectors.toList());

        if (legacySeededReviews.isEmpty()) {
            return;
        }

        List<User> mockReviewers = getOrCreateMockReviewers();
        for (Comment comment : legacySeededReviews) {
            User reviewer = mockReviewers.get(random.nextInt(mockReviewers.size()));
            comment.setCreatorUserId(reviewer.getId());
        }
        commentRepository.saveAll(legacySeededReviews);
    }

    private boolean isSeedReviewText(String description) {
        return SEED_REVIEW_TEXTS.contains(description);
    }

    // Picks a review tone based on the game rating.
    private String selectReviewBasedOnRating(Double rating) {
        if (rating == null) {
            return MIXED_REVIEWS[random.nextInt(MIXED_REVIEWS.length)];
        }

        if (rating >= 80) {
            return POSITIVE_REVIEWS[random.nextInt(POSITIVE_REVIEWS.length)];
        } else if (rating >= 60) {
            return MIXED_REVIEWS[random.nextInt(MIXED_REVIEWS.length)];
        } else {
            return CRITICAL_REVIEWS[random.nextInt(CRITICAL_REVIEWS.length)];
        }
    }

    // Creates a recent-looking timestamp for seeded comments.
    private LocalDateTime generateRandomRecentDate() {
        int daysAgo = random.nextInt(90);
        int hoursAgo = random.nextInt(24);
        int minutesAgo = random.nextInt(60);

        return LocalDateTime.now()
            .minusDays(daysAgo)
            .minusHours(hoursAgo)
            .minusMinutes(minutesAgo);
    }

    // Finds a product by local database id.
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Finds a product by external IGDB id.
    public Product getProductByIgdbId(Long igdbId) {
        return productRepository.findByIgdbId(igdbId);
    }

    // Returns non-deleted comments for a product.
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductIdAndIsDeletedFalse(productId);
    }

    // Returns non-deleted comments for a product as DTOs with username resolved from users table.
    @Transactional
    public List<CommentDTO> getCommentsAsDTOByProductId(Long productId) {
        repairLegacySeededReviewOwners(productId);
        List<Comment> comments = commentRepository.findByProductIdAndIsDeletedFalse(productId);
        return comments.stream().map(this::toCommentDTO).collect(Collectors.toList());
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

    // Converts a Comment entity to a DTO, resolving the creator's username.
    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setDescription(comment.getDescription());
        dto.setCreationDate(comment.getCreationDate());
        dto.setIsDeleted(comment.getIsDeleted());
        dto.setCreatorUserId(comment.getCreatorUserId());

        String username = userRepository.findById(comment.getCreatorUserId())
                .map(User::getUsername)
                .orElse("System");
        dto.setCreatorUsername(username);

        return dto;
    }

    // Creates a comment for an existing product.
    @Transactional
    public CommentDTO createComment(Long productId, String description, Long creatorUserId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Long userId = creatorUserId != null ? creatorUserId : SYSTEM_USER_ID;
        Comment comment = new Comment(description, userId, product);
        Comment saved = commentRepository.save(comment);
        return toCommentDTO(saved);
    }

    // Soft-deletes a comment only if it belongs to the requesting user.
    @Transactional
    public boolean deleteCommentByOwner(Long commentId, Long requestingUserId) {
        return commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(commentId, requestingUserId)
                .map(comment -> {
                    comment.setIsDeleted(true);
                    commentRepository.save(comment);
                    return true;
                })
                .orElse(false);
    }
}
