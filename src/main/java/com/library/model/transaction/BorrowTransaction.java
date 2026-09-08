package com.library.model.transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Model class representing a book borrowing transaction.
 */
public class BorrowTransaction {
    private String transactionId;
    private String memberId;
    private String bookIsbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private TransactionStatus status;
    private int renewalCount;
    private double fineAmount;
    private boolean finePaid;

    public BorrowTransaction(String transactionId, String memberId, String bookIsbn,
                             LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.bookIsbn = Objects.requireNonNull(bookIsbn, "Book ISBN cannot be null");
        this.issueDate = (issueDate != null) ? issueDate : LocalDate.now();
        this.dueDate = (dueDate != null) ? dueDate : this.issueDate.plusDays(14);
        this.returnDate = null;
        this.status = TransactionStatus.ISSUED;
        this.renewalCount = 0;
        this.fineAmount = 0.0;
        this.finePaid = false;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public void incrementRenewalCount() {
        this.renewalCount++;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = Math.max(0.0, fineAmount);
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    public boolean isOverdue(LocalDate currentDate) {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return currentDate.isAfter(dueDate);
    }

    public long getOverdueDays(LocalDate currentDate) {
        LocalDate endPoint = (returnDate != null) ? returnDate : currentDate;
        if (endPoint.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, endPoint);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowTransaction that = (BorrowTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return String.format("Tx: %s | Member: %s | ISBN: %s | Issued: %s | Due: %s | Status: %s | Overdue: %d days | Fine: $%.2f",
                transactionId, memberId, bookIsbn, issueDate, dueDate, status.getLabel(), getOverdueDays(LocalDate.now()), fineAmount);
    }
}
