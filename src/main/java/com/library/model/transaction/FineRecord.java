package com.library.model.transaction;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing a fine assessment/payment record.
 */
public class FineRecord {
    private String fineId;
    private String memberId;
    private String transactionId;
    private double amount;
    private String reason;
    private LocalDate dateAssessed;
    private boolean paid;
    private LocalDate datePaid;

    public FineRecord(String fineId, String memberId, String transactionId, double amount, String reason, LocalDate dateAssessed) {
        this.fineId = Objects.requireNonNull(fineId, "Fine ID cannot be null");
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.transactionId = transactionId;
        this.amount = amount;
        this.reason = (reason != null) ? reason : "Overdue book return";
        this.dateAssessed = (dateAssessed != null) ? dateAssessed : LocalDate.now();
        this.paid = false;
        this.datePaid = null;
    }

    public String getFineId() {
        return fineId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDateAssessed() {
        return dateAssessed;
    }

    public boolean isPaid() {
        return paid;
    }

    public LocalDate getDatePaid() {
        return datePaid;
    }

    public void markAsPaid(LocalDate paymentDate) {
        this.paid = true;
        this.datePaid = (paymentDate != null) ? paymentDate : LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FineRecord that = (FineRecord) o;
        return Objects.equals(fineId, that.fineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fineId);
    }

    @Override
    public String toString() {
        return String.format("Fine ID: %s | Member: %s | Tx: %s | Amount: $%.2f | Paid: %s | Assessed: %s",
                fineId, memberId, transactionId, amount, paid ? "YES (" + datePaid + ")" : "NO", dateAssessed);
    }
}
