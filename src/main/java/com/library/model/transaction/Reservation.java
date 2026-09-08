package com.library.model.transaction;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing a book reservation request.
 */
public class Reservation {
    private String reservationId;
    private String memberId;
    private String bookIsbn;
    private LocalDate reservationDate;
    private ReservationStatus status;

    public Reservation(String reservationId, String memberId, String bookIsbn, LocalDate reservationDate) {
        this.reservationId = Objects.requireNonNull(reservationId, "Reservation ID cannot be null");
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.bookIsbn = Objects.requireNonNull(bookIsbn, "Book ISBN cannot be null");
        this.reservationDate = (reservationDate != null) ? reservationDate : LocalDate.now();
        this.status = ReservationStatus.PENDING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return String.format("Res ID: %s | Member: %s | ISBN: %s | Reserved: %s | Status: %s",
                reservationId, memberId, bookIsbn, reservationDate, status.getLabel());
    }
}
