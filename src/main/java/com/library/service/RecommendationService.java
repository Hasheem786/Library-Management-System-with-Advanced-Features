package com.library.service;

import com.library.model.book.Book;
import com.library.model.book.BookCategory;
import com.library.model.member.Member;
import com.library.model.transaction.BorrowTransaction;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Recommendation Engine providing personalized recommendations, category popularity recommendations,
 * and collaborative filtering across similar members using Java Streams.
 */
public class RecommendationService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public RecommendationService(BookRepository bookRepository,
                                 MemberRepository memberRepository,
                                 TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Feature 5.1: Suggest books based on member's borrowing history.
     * Analyzes member's borrowed books to find top categories, then suggests top-rated unread books in those categories.
     */
    public List<Book> suggestBooksByMemberHistory(String memberId, int limit) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) return Collections.emptyList();

        Member member = memberOpt.get();
        Set<String> readIsbns = new HashSet<>(member.getBorrowingHistoryIsbns());

        // Find member's top borrowed categories
        Map<BookCategory, Long> categoryFrequency = readIsbns.stream()
                .map(bookRepository::findByIsbn)
                .flatMap(Optional::stream)
                .collect(Collectors.groupingBy(Book::getCategory, Collectors.counting()));

        if (categoryFrequency.isEmpty()) {
            // Fallback to top overall popular books if member has no history
            return suggestPopularBooksOverall(limit);
        }

        List<BookCategory> preferredCategories = categoryFrequency.entrySet().stream()
                .sorted(Map.Entry.<BookCategory, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        return bookRepository.findAll().stream()
                .filter(book -> !readIsbns.contains(book.getIsbn()))
                .filter(book -> preferredCategories.contains(book.getCategory()))
                .sorted(Comparator.comparingInt(Book::getBorrowCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Feature 5.2: Recommend popular books in preferred categories.
     */
    public List<Book> suggestPopularBooksInCategory(BookCategory category, int limit) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getCategory() == category)
                .sorted(Comparator.comparingInt(Book::getBorrowCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Suggests overall popular books across all categories.
     */
    public List<Book> suggestPopularBooksOverall(int limit) {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Book::getBorrowCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Feature 5.3: Cross-recommendations based on similar members' preferences (Collaborative Filtering).
     */
    public List<Book> suggestCollaborativeFiltering(String targetMemberId, int limit) {
        Optional<Member> targetOpt = memberRepository.findById(targetMemberId);
        if (targetOpt.isEmpty()) return Collections.emptyList();

        Member targetMember = targetOpt.get();
        Set<String> targetHistory = new HashSet<>(targetMember.getBorrowingHistoryIsbns());

        if (targetHistory.isEmpty()) {
            return suggestPopularBooksOverall(limit);
        }

        // Find other members with overlapping reading history
        List<Member> otherMembers = memberRepository.findAll().stream()
                .filter(m -> !m.getMemberId().equalsIgnoreCase(targetMemberId))
                .toList();

        Map<String, Double> similarityScores = new HashMap<>();
        for (Member other : otherMembers) {
            Set<String> otherHistory = new HashSet<>(other.getBorrowingHistoryIsbns());
            long commonBooks = targetHistory.stream().filter(otherHistory::contains).count();
            if (commonBooks > 0) {
                // Jaccard Similarity index = intersection / union
                Set<String> union = new HashSet<>(targetHistory);
                union.addAll(otherHistory);
                double similarity = (double) commonBooks / union.size();
                similarityScores.put(other.getMemberId(), similarity);
            }
        }

        if (similarityScores.isEmpty()) {
            return suggestBooksByMemberHistory(targetMemberId, limit);
        }

        // Collect candidate books borrowed by similar members that target member hasn't read
        Map<String, Double> recommendedIsbnWeights = new HashMap<>();

        similarityScores.forEach((otherId, similarityWeight) -> {
            Member otherMember = memberRepository.findById(otherId).orElse(null);
            if (otherMember != null) {
                for (String isbn : otherMember.getBorrowingHistoryIsbns()) {
                    if (!targetHistory.contains(isbn)) {
                        recommendedIsbnWeights.merge(isbn, similarityWeight, Double::sum);
                    }
                }
            }
        });

        return recommendedIsbnWeights.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(entry -> bookRepository.findByIsbn(entry.getKey()))
                .flatMap(Optional::stream)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
