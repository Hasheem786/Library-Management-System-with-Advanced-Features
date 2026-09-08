package com.library;

import com.library.model.book.Book;
import com.library.model.book.BookCategory;
import com.library.model.book.PhysicalBook;
import com.library.model.member.Member;
import com.library.model.member.StudentMember;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import com.library.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    private BookRepository bookRepository;
    private MemberRepository memberRepository;
    private TransactionRepository transactionRepository;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        memberRepository = new MemberRepository();
        transactionRepository = new TransactionRepository();
        analyticsService = new AnalyticsService(bookRepository, memberRepository, transactionRepository);

        Book b1 = new PhysicalBook("ISBN-1", "Book 1", List.of("Author 1"), "Pub", 2020, 300,
                BookCategory.FICTION, 2, "Shelf", 0.5, com.library.model.book.BookCondition.NEW);
        b1.setBorrowCount(10);
        bookRepository.save(b1);

        Member m1 = new StudentMember("M1", "Alice", "a@test.com", "123", "Addr", LocalDate.now(), "CS", "1");
        memberRepository.save(m1);

        BorrowTransaction tx1 = new BorrowTransaction("T1", "M1", "ISBN-1", LocalDate.now().minusDays(30), LocalDate.now().minusDays(16));
        tx1.setStatus(TransactionStatus.OVERDUE);
        transactionRepository.saveTransaction(tx1);
    }

    @Test
    void testGetDefaulterMembers() {
        List<Member> defaulters = analyticsService.getDefaulterMembers(LocalDate.now());
        assertEquals(1, defaulters.size());
        assertEquals("M1", defaulters.get(0).getMemberId());
    }

    @Test
    void testGetMostPopularBooks() {
        List<Book> popular = analyticsService.getMostPopularBooks(5);
        assertFalse(popular.isEmpty());
        assertEquals("Book 1", popular.get(0).getTitle());
    }

    @Test
    void testTurnoverRates() {
        Map<String, Double> rates = analyticsService.getBookTurnoverRates();
        assertEquals(5.0, rates.get("Book 1")); // 10 borrows / 2 copies = 5.0
    }
}
