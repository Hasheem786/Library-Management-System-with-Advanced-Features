package com.library;

import com.library.model.member.*;
import com.library.model.transaction.BorrowTransaction;
import com.library.service.FineCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FineCalculatorServiceTest {

    private FineCalculatorService fineCalculatorService;

    @BeforeEach
    void setUp() {
        fineCalculatorService = new FineCalculatorService();
    }

    @Test
    void testFineZeroDuringGracePeriod() {
        StudentMember student = new StudentMember("S1", "Alice", "a@e.com", "1", "A", LocalDate.now(), "CS", "1");
        // Student: Grace period = 2 days, Daily rate = $0.50
        LocalDate issueDate = LocalDate.now().minusDays(16);
        LocalDate dueDate = issueDate.plusDays(14); // Overdue by 2 days (which equals grace period)

        BorrowTransaction tx = new BorrowTransaction("T1", "S1", "ISBN1", issueDate, dueDate);
        double fine = fineCalculatorService.calculateFine(tx, student, LocalDate.now());
        assertEquals(0.0, fine, 0.001);
    }

    @Test
    void testFineCalculationStandardOverdue() {
        StudentMember student = new StudentMember("S1", "Alice", "a@e.com", "1", "A", LocalDate.now(), "CS", "1");
        // Student: Grace = 2 days, Daily rate = $0.50.
        // Overdue by 7 days -> effective overdue days = 7 - 2 = 5 days.
        // Fine = 5 * $0.50 = $2.50.
        LocalDate issueDate = LocalDate.now().minusDays(21);
        LocalDate dueDate = issueDate.plusDays(14);

        BorrowTransaction tx = new BorrowTransaction("T1", "S1", "ISBN1", issueDate, dueDate);
        double fine = fineCalculatorService.calculateFine(tx, student, LocalDate.now());
        assertEquals(2.50, fine, 0.001);
    }

    @Test
    void testMaxFineCapEnforcement() {
        GeneralMember general = new GeneralMember("G1", "Bob", "b@g.com", "2", "B", LocalDate.now(), "Dev");
        // General: Daily rate = $1.00, Grace = 1 day, Max fine cap = $30.00.
        // 100 days overdue would be $100+, capped at $30.00.
        LocalDate issueDate = LocalDate.now().minusDays(120);
        LocalDate dueDate = issueDate.plusDays(14);

        BorrowTransaction tx = new BorrowTransaction("T2", "G1", "ISBN2", issueDate, dueDate);
        double fine = fineCalculatorService.calculateFine(tx, general, LocalDate.now());
        assertEquals(30.00, fine, 0.001);
    }
}
