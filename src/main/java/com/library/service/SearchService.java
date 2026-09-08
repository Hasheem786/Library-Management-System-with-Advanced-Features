package com.library.service;

import com.library.model.book.Book;
import com.library.model.book.BookCategory;
import com.library.model.member.Member;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced multi-criteria search engine built entirely with Java 21 Streams & Lambdas.
 */
public class SearchService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public SearchService(BookRepository bookRepository,
                         MemberRepository memberRepository,
                         TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Requirement 4.1: Search books by multiple authors using contains() matching.
     */
    public List<Book> searchBooksByAuthors(List<String> authorQueries) {
        if (authorQueries == null || authorQueries.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> cleanQueries = authorQueries.stream()
                .filter(q -> q != null && !q.trim().isEmpty())
                .map(String::toLowerCase)
                .map(String::trim)
                .toList();

        return bookRepository.findAll().stream()
                .filter(book -> book.getAuthors().stream()
                        .anyMatch(author -> cleanQueries.stream()
                                .anyMatch(query -> author.toLowerCase().contains(query))))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Requirement 4.2: Filter available books by category and publication year range.
     */
    public List<Book> filterAvailableBooks(BookCategory category, Integer startYear, Integer endYear) {
        return bookRepository.findAll().stream()
                .filter(Book::isAvailable)
                .filter(b -> category == null || b.getCategory() == category)
                .filter(b -> startYear == null || b.getPublicationYear() >= startYear)
                .filter(b -> endYear == null || b.getPublicationYear() <= endYear)
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }

    /**
     * Multi-criteria search by keyword (title, author, publisher, ISBN, category).
     */
    public List<Book> multiCriteriaSearch(String keyword, BookCategory category, String bookType, Boolean availableOnly) {
        String query = (keyword != null) ? keyword.trim().toLowerCase() : "";

        return bookRepository.findAll().stream()
                .filter(b -> query.isEmpty() ||
                             b.getTitle().toLowerCase().contains(query) ||
                             b.getIsbn().toLowerCase().contains(query) ||
                             b.getPublisher().toLowerCase().contains(query) ||
                             b.getAuthors().stream().anyMatch(a -> a.toLowerCase().contains(query)))
                .filter(b -> category == null || b.getCategory() == category)
                .filter(b -> bookType == null || bookType.isEmpty() || b.getBookType().equalsIgnoreCase(bookType))
                .filter(b -> availableOnly == null || !availableOnly || b.isAvailable())
                .collect(Collectors.toList());
    }

    /**
     * Requirement 4.3: Find members with overdue books as of currentDate.
     */
    public List<Member> findMembersWithOverdueBooks(LocalDate currentDate) {
        Set<String> overdueMemberIds = transactionRepository.findAllTransactions().stream()
                .filter(tx -> tx.getStatus() != TransactionStatus.RETURNED)
                .filter(tx -> tx.isOverdue(currentDate))
                .map(BorrowTransaction::getMemberId)
                .collect(Collectors.toSet());

        return memberRepository.findAll().stream()
                .filter(m -> overdueMemberIds.contains(m.getMemberId()))
                .collect(Collectors.toList());
    }

    /**
     * Requirement 4.4: Calculate average borrowing duration by book category in days using Streams.
     */
    public Map<BookCategory, Double> calculateAverageBorrowingDurationByCategory() {
        List<BorrowTransaction> returnedTxs = transactionRepository.findAllTransactions().stream()
                .filter(tx -> tx.getReturnDate() != null)
                .toList();

        if (returnedTxs.isEmpty()) {
            return Collections.emptyMap();
        }

        return returnedTxs.stream()
                .collect(Collectors.groupingBy(
                        tx -> bookRepository.findByIsbn(tx.getBookIsbn())
                                .map(Book::getCategory)
                                .orElse(BookCategory.FICTION),
                        Collectors.averagingDouble(tx -> ChronoUnit.DAYS.between(tx.getIssueDate(), tx.getReturnDate()))
                ));
    }

    /**
     * Requirement 4.5: Generate popularity rankings using groupingBy and counting.
     */
    public Map<String, Long> generatePopularityRankings() {
        return transactionRepository.findAllTransactions().stream()
                .collect(Collectors.groupingBy(
                        BorrowTransaction::getBookIsbn,
                        Collectors.counting()
                ));
    }
}
