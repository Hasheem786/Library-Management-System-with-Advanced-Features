package com.library.service;

import com.library.model.book.Book;
import com.library.model.book.BookCategory;
import com.library.model.member.Member;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.FineRecord;
import com.library.model.transaction.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reporting and Advanced Analytics engine leveraging Java 21 Streams & Collectors.
 */
public class AnalyticsService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public AnalyticsService(BookRepository bookRepository,
                            MemberRepository memberRepository,
                            TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    // 1. Member Reports
    public List<Member> getActiveMembers() {
        return memberRepository.findAll().stream()
                .filter(m -> m.getStatus().name().equalsIgnoreCase("ACTIVE"))
                .collect(Collectors.toList());
    }

    public List<Member> getDefaulterMembers(LocalDate currentDate) {
        Set<String> overdueMemberIds = transactionRepository.findAllTransactions().stream()
                .filter(tx -> tx.getStatus() != TransactionStatus.RETURNED && tx.isOverdue(currentDate))
                .map(BorrowTransaction::getMemberId)
                .collect(Collectors.toSet());

        return memberRepository.findAll().stream()
                .filter(m -> m.getUnpaidFinesBalance() > 0 || overdueMemberIds.contains(m.getMemberId()))
                .sorted(Comparator.comparingDouble(Member::getUnpaidFinesBalance).reversed())
                .collect(Collectors.toList());
    }

    public List<Member> getTopBorrowers(int limit) {
        return memberRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Member m) -> m.getBorrowingHistoryIsbns().size()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 2. Book Reports
    public List<Book> getMostPopularBooks(int limit) {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Book::getBorrowCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Book> getLeastBorrowedBooks(int limit) {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Book::getBorrowCount))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Book> getNeverBorrowedBooks() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getBorrowCount() == 0)
                .collect(Collectors.toList());
    }

    // 3. Financial Reports
    public double getTotalFineCollectionRevenue() {
        return transactionRepository.findAllFineRecords().stream()
                .filter(FineRecord::isPaid)
                .mapToDouble(FineRecord::getAmount)
                .sum();
    }

    public double getTotalPendingFinesAmount() {
        return memberRepository.findAll().stream()
                .mapToDouble(Member::getUnpaidFinesBalance)
                .sum();
    }

    // 4. Advanced Analytics
    /**
     * Monthly borrowing trends by category.
     */
    public Map<YearMonth, Map<BookCategory, Long>> getMonthlyBorrowingTrendsByCategory() {
        return transactionRepository.findAllTransactions().stream()
                .collect(Collectors.groupingBy(
                        tx -> YearMonth.from(tx.getIssueDate()),
                        Collectors.groupingBy(
                                tx -> bookRepository.findByIsbn(tx.getBookIsbn())
                                        .map(Book::getCategory)
                                        .orElse(BookCategory.FICTION),
                                Collectors.counting()
                        )
                ));
    }

    /**
     * Book turnover rates: total borrow count / total available copies.
     */
    public Map<String, Double> getBookTurnoverRates() {
        return bookRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Book::getTitle,
                        b -> (double) b.getBorrowCount() / Math.max(1, b.getTotalCopies())
                ));
    }

    /**
     * Member engagement metrics.
     */
    public String generateMemberEngagementReport() {
        List<Member> members = memberRepository.findAll();
        long totalMembers = members.size();
        long activeMembers = members.stream().filter(m -> m.getStatus().name().equalsIgnoreCase("ACTIVE")).count();
        long membersWithActiveBorrows = members.stream().filter(m -> !m.getCurrentBorrowedIsbns().isEmpty()).count();
        double avgBorrowsPerMember = members.stream().mapToInt(m -> m.getBorrowingHistoryIsbns().size()).average().orElse(0.0);

        return String.format("""
            === MEMBER ENGAGEMENT ANALYSIS ===
            Total Registered Members       : %d
            Active Status Members          : %d (%.1f%%)
            Members Currently Borrowing    : %d (%.1f%%)
            Avg Books Borrowed Per Member  : %.2f
            """,
                totalMembers,
                activeMembers, totalMembers > 0 ? (activeMembers * 100.0 / totalMembers) : 0,
                membersWithActiveBorrows, totalMembers > 0 ? (membersWithActiveBorrows * 100.0 / totalMembers) : 0,
                avgBorrowsPerMember);
    }
}
