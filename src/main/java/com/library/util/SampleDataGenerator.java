package com.library.util;

import com.library.model.book.*;
import com.library.model.member.*;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.FineRecord;
import com.library.model.transaction.Reservation;
import com.library.model.transaction.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Utility class seeding rich, realistic sample data for testing and demonstration.
 */
public class SampleDataGenerator {

    public static void populateSampleData(BookRepository bookRepo,
                                          MemberRepository memberRepo,
                                          TransactionRepository txRepo) {
        // Clear existing
        bookRepo.clear();
        memberRepo.clear();
        txRepo.clearAll();

        // 1. Seed Books (Physical, E-Book, AudioBook across categories)
        Book b1 = new PhysicalBook("978-0134685991", "Effective Java", List.of("Joshua Bloch"),
                "Addison-Wesley", 2018, 416, BookCategory.TECHNOLOGY, 5, "Sec A-102", 0.85, BookCondition.NEW);
        Book b2 = new PhysicalBook("978-0596009205", "Head First Design Patterns", List.of("Eric Freeman", "Elisabeth Robson"),
                "O'Reilly Media", 2004, 694, BookCategory.TECHNOLOGY, 3, "Sec A-105", 1.20, BookCondition.GOOD);
        Book b3 = new EBook("978-0132350884", "Clean Code", List.of("Robert C. Martin"),
                "Prentice Hall", 2008, 464, BookCategory.TECHNOLOGY, 10, 14.5, "PDF", "https://library.org/dl/cleancode.pdf");
        Book b4 = new AudioBook("978-0307474278", "The Da Vinci Code", List.of("Dan Brown"),
                "Anchor", 2009, 592, BookCategory.MYSTERY, 4, 480, "Paul Michael", "MP3");

        Book b5 = new PhysicalBook("978-0451524935", "1984", List.of("George Orwell"),
                "Signet Classic", 1950, 328, BookCategory.FICTION, 4, "Sec B-201", 0.40, BookCondition.GOOD);
        Book b6 = new PhysicalBook("978-0061120084", "To Kill a Mockingbird", List.of("Harper Lee"),
                "Harper Perennial", 1960, 336, BookCategory.FICTION, 2, "Sec B-204", 0.45, BookCondition.FAIR);
        Book b7 = new EBook("978-0345391803", "The Hitchhiker's Guide to the Galaxy", List.of("Douglas Adams"),
                "Del Rey", 1979, 224, BookCategory.FANTASY, 8, 8.2, "EPUB", "https://library.org/dl/hitchhiker.epub");

        Book b8 = new PhysicalBook("978-0307269980", "Steve Jobs", List.of("Walter Isaacson"),
                "Simon & Schuster", 2011, 656, BookCategory.BIOGRAPHY, 3, "Sec C-301", 1.10, BookCondition.GOOD);
        Book b9 = new AudioBook("978-0525657743", "Becoming", List.of("Michelle Obama"),
                "Random House Audio", 2018, 448, BookCategory.BIOGRAPHY, 6, 1140, "Michelle Obama", "MP3");

        Book b10 = new PhysicalBook("978-0143127741", "Sapiens: A Brief History of Humankind", List.of("Yuval Noah Harari"),
                "Harper", 2015, 464, BookCategory.HISTORY, 5, "Sec D-401", 0.70, BookCondition.NEW);
        Book b11 = new PhysicalBook("978-0393317558", "Guns, Germs, and Steel", List.of("Jared Diamond"),
                "W. W. Norton", 1997, 528, BookCategory.HISTORY, 3, "Sec D-405", 0.80, BookCondition.GOOD);

        Book b12 = new PhysicalBook("978-0451526342", "Animal Farm", List.of("George Orwell"),
                "Signet", 1945, 140, BookCategory.FICTION, 3, "Sec B-202", 0.25, BookCondition.GOOD);

        b1.setBorrowCount(18);
        b2.setBorrowCount(12);
        b3.setBorrowCount(25);
        b4.setBorrowCount(9);
        b5.setBorrowCount(15);
        b6.setBorrowCount(8);
        b7.setBorrowCount(14);
        b8.setBorrowCount(11);
        b9.setBorrowCount(16);
        b10.setBorrowCount(22);
        b11.setBorrowCount(7);
        b12.setBorrowCount(6);

        bookRepo.save(b1);
        bookRepo.save(b2);
        bookRepo.save(b3);
        bookRepo.save(b4);
        bookRepo.save(b5);
        bookRepo.save(b6);
        bookRepo.save(b7);
        bookRepo.save(b8);
        bookRepo.save(b9);
        bookRepo.save(b10);
        bookRepo.save(b11);
        bookRepo.save(b12);

        // 2. Seed Members (Student, Faculty, General Public)
        Member m1 = new StudentMember("STU-1001", "Alice Smith", "alice.smith@university.edu",
                "555-0101", "123 Campus Way", LocalDate.now().minusMonths(6), "Computer Science", "CS-2024-001");
        Member m2 = new StudentMember("STU-1002", "Bob Jones", "bob.jones@university.edu",
                "555-0102", "456 College Ave", LocalDate.now().minusMonths(4), "Electrical Engineering", "EE-2024-042");
        Member m3 = new FacultyMember("FAC-2001", "Dr. Robert Vance", "r.vance@university.edu",
                "555-0201", "789 Faculty Row", LocalDate.now().minusYears(2), "Computer Science", "Professor");
        Member m4 = new FacultyMember("FAC-2002", "Dr. Sarah Connor", "s.connor@university.edu",
                "555-0202", "321 Tech Blvd", LocalDate.now().minusYears(1), "Physics", "Associate Professor");
        Member m5 = new GeneralMember("GEN-3001", "Charlie Brown", "charlie.brown@gmail.com",
                "555-0301", "12 Oak Street", LocalDate.now().minusMonths(3), "Software Engineer");
        Member m6 = new GeneralMember("GEN-3002", "Diana Prince", "diana.prince@outlook.com",
                "555-0302", "55 Museum Plaza", LocalDate.now().minusMonths(8), "Archivist");

        m1.addBorrowedBook(b1.getIsbn());
        m1.addBorrowedBook(b3.getIsbn());
        m2.addBorrowedBook(b5.getIsbn());
        m3.addBorrowedBook(b2.getIsbn());
        m3.addBorrowedBook(b10.getIsbn());

        memberRepo.save(m1);
        memberRepo.save(m2);
        memberRepo.save(m3);
        memberRepo.save(m4);
        memberRepo.save(m5);
        memberRepo.save(m6);

        // 3. Seed Transactions
        LocalDate today = LocalDate.now();

        // Active non-overdue
        BorrowTransaction tx1 = new BorrowTransaction("TX-10001", m1.getMemberId(), b1.getIsbn(), today.minusDays(5), today.plusDays(9));
        // Active non-overdue
        BorrowTransaction tx2 = new BorrowTransaction("TX-10002", m1.getMemberId(), b3.getIsbn(), today.minusDays(2), today.plusDays(12));
        // Overdue transaction
        BorrowTransaction tx3 = new BorrowTransaction("TX-10003", m2.getMemberId(), b5.getIsbn(), today.minusDays(25), today.minusDays(11));
        tx3.setStatus(TransactionStatus.OVERDUE);

        // Returned transaction with fine
        BorrowTransaction tx4 = new BorrowTransaction("TX-10004", m5.getMemberId(), b8.getIsbn(), today.minusDays(40), today.minusDays(26));
        tx4.setReturnDate(today.minusDays(10)); // 16 days overdue -> grace 1 day -> 15 days effective -> 14*$1 + 1*$1.5 = $15.50
        tx4.setStatus(TransactionStatus.RETURNED);
        tx4.setFineAmount(15.50);

        BorrowTransaction tx5 = new BorrowTransaction("TX-10005", m3.getMemberId(), b10.getIsbn(), today.minusDays(10), today.plusDays(20));

        txRepo.saveTransaction(tx1);
        txRepo.saveTransaction(tx2);
        txRepo.saveTransaction(tx3);
        txRepo.saveTransaction(tx4);
        txRepo.saveTransaction(tx5);

        // Seed Fine Records
        FineRecord f1 = new FineRecord("FINE-9001", m5.getMemberId(), tx4.getTransactionId(), 15.50, "Overdue return (16 days)", today.minusDays(10));
        m5.addUnpaidFine(15.50);
        memberRepo.save(m5);
        txRepo.saveFineRecord(f1);

        // Seed Reservation
        Reservation r1 = new Reservation("RES-5001", m4.getMemberId(), b6.getIsbn(), today.minusDays(1));
        txRepo.saveReservation(r1);
        b6.incrementReservedCopies();
        bookRepo.save(b6);
    }
}
