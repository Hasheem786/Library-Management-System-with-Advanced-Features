package com.library.service;

import com.library.model.book.Book;
import com.library.model.member.Member;
import com.library.model.member.MembershipStatus;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.FineRecord;
import com.library.model.transaction.Reservation;
import com.library.model.transaction.ReservationStatus;
import com.library.model.transaction.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Main transactional service handling Book Issuance, Returns, Renewals, Reservations,
 * and Overdue tracking enforcing business validation logic.
 */
public class BorrowingService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;
    private final FineCalculatorService fineCalculatorService;

    public BorrowingService(BookRepository bookRepository,
                            MemberRepository memberRepository,
                            TransactionRepository transactionRepository,
                            FineCalculatorService fineCalculatorService) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
        this.fineCalculatorService = fineCalculatorService;
    }

    /**
     * Issues a book to a member with Scenario 1 validation checks.
     */
    public synchronized BorrowTransaction issueBook(String memberId, String isbn) {
        return issueBook(memberId, isbn, LocalDate.now());
    }

    public synchronized BorrowTransaction issueBook(String memberId, String isbn, LocalDate issueDate) {
        // Validation check 1: Member exists and is active
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Issue Failed: Member ID " + memberId + " not found."));

        if (member.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Issue Failed: Member account status is " + member.getStatus().getLabel() + ".");
        }

        // Validation check 2: Book exists and is available
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Issue Failed: Book with ISBN " + isbn + " not found."));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Issue Failed: No available copies left for '" + book.getTitle() + "'.");
        }

        // Validation check 3: Member borrowing limit check
        if (member.getCurrentBorrowedIsbns().size() >= member.getMaxBorrowingLimit()) {
            throw new IllegalStateException(String.format(
                    "Issue Failed: Member has reached maximum borrowing limit of %d books.", member.getMaxBorrowingLimit()));
        }

        // Validation check 4: Unpaid fines check
        if (member.getUnpaidFinesBalance() >= 15.0) {
            throw new IllegalStateException(String.format(
                    "Issue Failed: Member has outstanding unpaid fines of $%.2f (Threshold: $15.00).", member.getUnpaidFinesBalance()));
        }

        // Validation check 5: Reservation priority check
        List<Reservation> pendingReservations = transactionRepository.findReservationsByBookIsbn(isbn).stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());

        if (!pendingReservations.isEmpty()) {
            Reservation nextReservation = pendingReservations.get(0);
            if (!nextReservation.getMemberId().equalsIgnoreCase(memberId)) {
                throw new IllegalStateException("Issue Failed: Book is currently reserved by another member (" + nextReservation.getMemberId() + ").");
            } else {
                nextReservation.setStatus(ReservationStatus.FULFILLED);
                book.decrementReservedCopies();
            }
        }

        // Issue process execution
        book.decrementAvailableCopies();
        member.addBorrowedBook(isbn);

        LocalDate dueDate = issueDate.plusDays(member.getBorrowingPeriodDays());
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BorrowTransaction tx = new BorrowTransaction(transactionId, memberId, isbn, issueDate, dueDate);
        transactionRepository.saveTransaction(tx);
        bookRepository.save(book);
        memberRepository.save(member);

        return tx;
    }

    /**
     * Returns a book, calculates fine using FineCalculatorService, updates stock & reservations.
     */
    public synchronized double returnBook(String transactionId) {
        return returnBook(transactionId, LocalDate.now());
    }

    public synchronized double returnBook(String transactionId, LocalDate returnDate) {
        BorrowTransaction tx = transactionRepository.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Return Failed: Transaction ID " + transactionId + " not found."));

        if (tx.getStatus() == TransactionStatus.RETURNED) {
            throw new IllegalStateException("Return Failed: Book has already been returned.");
        }

        Member member = memberRepository.findById(tx.getMemberId())
                .orElseThrow(() -> new IllegalStateException("Return Error: Member not found."));

        Book book = bookRepository.findByIsbn(tx.getBookIsbn())
                .orElseThrow(() -> new IllegalStateException("Return Error: Book not found."));

        tx.setReturnDate(returnDate);
        tx.setStatus(TransactionStatus.RETURNED);

        // Calculate fines using FineCalculatorService
        double fineAmount = fineCalculatorService.calculateFine(tx, member, returnDate);
        tx.setFineAmount(fineAmount);

        if (fineAmount > 0) {
            member.addUnpaidFine(fineAmount);
            FineRecord fineRecord = new FineRecord(
                    "FINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    member.getMemberId(),
                    tx.getTransactionId(),
                    fineAmount,
                    "Overdue return (" + tx.getOverdueDays(returnDate) + " days overdue)",
                    returnDate
            );
            transactionRepository.saveFineRecord(fineRecord);
        }

        // Return book to inventory
        book.incrementAvailableCopies();
        member.removeBorrowedBook(book.getIsbn());

        // Check if book has pending reservations
        List<Reservation> pending = transactionRepository.findReservationsByBookIsbn(book.getIsbn()).stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .toList();

        if (!pending.isEmpty()) {
            book.incrementReservedCopies();
        }

        transactionRepository.saveTransaction(tx);
        bookRepository.save(book);
        memberRepository.save(member);

        return fineAmount;
    }

    /**
     * Renews borrowing period for a transaction if conditions are satisfied.
     */
    public synchronized boolean renewBook(String transactionId) {
        return renewBook(transactionId, LocalDate.now());
    }

    public synchronized boolean renewBook(String transactionId, LocalDate currentDate) {
        BorrowTransaction tx = transactionRepository.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Renewal Failed: Transaction not found."));

        if (tx.getStatus() == TransactionStatus.RETURNED) {
            throw new IllegalStateException("Renewal Failed: Book is already returned.");
        }

        Member member = memberRepository.findById(tx.getMemberId())
                .orElseThrow(() -> new IllegalStateException("Member not found."));

        if (tx.getRenewalCount() >= member.getMaxRenewalLimit()) {
            throw new IllegalStateException(String.format("Renewal Failed: Maximum renewal limit (%d) reached.", member.getMaxRenewalLimit()));
        }

        if (tx.isOverdue(currentDate)) {
            throw new IllegalStateException("Renewal Failed: Cannot renew overdue book. Return book or settle fine.");
        }

        // Check if book is reserved by someone else
        boolean hasPendingReservations = transactionRepository.findReservationsByBookIsbn(tx.getBookIsbn()).stream()
                .anyMatch(r -> r.getStatus() == ReservationStatus.PENDING && !r.getMemberId().equalsIgnoreCase(member.getMemberId()));

        if (hasPendingReservations) {
            throw new IllegalStateException("Renewal Failed: Book is reserved by another member.");
        }

        tx.incrementRenewalCount();
        tx.setDueDate(tx.getDueDate().plusDays(member.getBorrowingPeriodDays()));
        tx.setStatus(TransactionStatus.RENEWED);

        transactionRepository.saveTransaction(tx);
        return true;
    }

    /**
     * Reserves a book for a member.
     */
    public synchronized Reservation reserveBook(String memberId, String isbn) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation Failed: Member not found."));

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Reservation Failed: Book not found."));

        boolean alreadyReserved = transactionRepository.findReservationsByBookIsbn(isbn).stream()
                .anyMatch(r -> r.getMemberId().equalsIgnoreCase(memberId) && r.getStatus() == ReservationStatus.PENDING);

        if (alreadyReserved) {
            throw new IllegalStateException("Reservation Failed: You already have a pending reservation for this book.");
        }

        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = new Reservation(reservationId, memberId, isbn, LocalDate.now());

        transactionRepository.saveReservation(reservation);
        book.incrementReservedCopies();
        bookRepository.save(book);

        return reservation;
    }

    /**
     * Retrieves all overdue transactions as of specified date.
     */
    public List<BorrowTransaction> getOverdueTransactions(LocalDate currentDate) {
        return transactionRepository.findAllTransactions().stream()
                .filter(tx -> tx.getStatus() != TransactionStatus.RETURNED && tx.isOverdue(currentDate))
                .collect(Collectors.toList());
    }

    public List<BorrowTransaction> getAllTransactions() {
        return transactionRepository.findAllTransactions();
    }
}
