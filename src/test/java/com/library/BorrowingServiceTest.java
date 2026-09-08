package com.library;

import com.library.model.book.*;
import com.library.model.member.*;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.Reservation;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import com.library.service.BorrowingService;
import com.library.service.FineCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {

    private BookRepository bookRepository;
    private MemberRepository memberRepository;
    private TransactionRepository transactionRepository;
    private BorrowingService borrowingService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        memberRepository = new MemberRepository();
        transactionRepository = new TransactionRepository();
        FineCalculatorService fineCalculatorService = new FineCalculatorService();
        borrowingService = new BorrowingService(bookRepository, memberRepository, transactionRepository, fineCalculatorService);

        // Seed basic book and member
        Book book = new PhysicalBook("ISBN-100", "Test Book", List.of("Test Author"), "Test Pub", 2022,
                200, BookCategory.FICTION, 1, "Shelf 1", 0.5, BookCondition.NEW);
        bookRepository.save(book);

        Member member = new StudentMember("STU-100", "Student One", "s1@test.com", "123", "Addr",
                LocalDate.now(), "CS", "100");
        memberRepository.save(member);
    }

    @Test
    void testIssueBookSuccess() {
        BorrowTransaction tx = borrowingService.issueBook("STU-100", "ISBN-100");
        assertNotNull(tx);
        assertEquals("STU-100", tx.getMemberId());
        assertEquals("ISBN-100", tx.getBookIsbn());
        assertEquals(0, bookRepository.findByIsbn("ISBN-100").get().getAvailableCopies());
    }

    @Test
    void testIssueBookFailsWhenUnavailable() {
        borrowingService.issueBook("STU-100", "ISBN-100"); // 1 copy issued -> 0 remaining

        Member member2 = new FacultyMember("FAC-200", "Faculty Two", "f2@test.com", "456", "Addr",
                LocalDate.now(), "CS", "Prof");
        memberRepository.save(member2);

        assertThrows(IllegalStateException.class, () -> borrowingService.issueBook("FAC-200", "ISBN-100"));
    }

    @Test
    void testBorrowingLimitEnforcement() {
        GeneralMember general = new GeneralMember("GEN-300", "Gen Member", "g3@test.com", "789", "Addr",
                LocalDate.now(), "Dev");
        memberRepository.save(general);

        // General member limit = 3 books
        for (int i = 1; i <= 3; i++) {
            String isbn = "ISBN-LIMIT-" + i;
            bookRepository.save(new PhysicalBook(isbn, "Book " + i, List.of("Author"), "Pub", 2020, 100,
                    BookCategory.FICTION, 5, "Shelf", 0.5, BookCondition.NEW));
            borrowingService.issueBook("GEN-300", isbn);
        }

        // 4th issue should fail limit check
        bookRepository.save(new PhysicalBook("ISBN-LIMIT-4", "Book 4", List.of("Author"), "Pub", 2020, 100,
                BookCategory.FICTION, 5, "Shelf", 0.5, BookCondition.NEW));
        assertThrows(IllegalStateException.class, () -> borrowingService.issueBook("GEN-300", "ISBN-LIMIT-4"));
    }

    @Test
    void testReturnBookAndFineAssessment() {
        LocalDate pastIssue = LocalDate.now().minusDays(30);
        BorrowTransaction tx = borrowingService.issueBook("STU-100", "ISBN-100", pastIssue);

        double fine = borrowingService.returnBook(tx.getTransactionId(), LocalDate.now());
        assertTrue(fine > 0);
        assertEquals(1, bookRepository.findByIsbn("ISBN-100").get().getAvailableCopies());
    }

    @Test
    void testReserveBook() {
        Reservation reservation = borrowingService.reserveBook("STU-100", "ISBN-100");
        assertNotNull(reservation);
        assertEquals(1, bookRepository.findByIsbn("ISBN-100").get().getReservedCopies());
    }
}
