package com.library.model.transaction;

/**
 * Status enumeration for book reservations.
 */
public enum ReservationStatus {
    PENDING("Pending"),
    FULFILLED("Fulfilled"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ReservationStatus fromString(String str) {
        if (str == null || str.trim().isEmpty()) return PENDING;
        for (ReservationStatus s : values()) {
            if (s.name().equalsIgnoreCase(str.trim()) || s.label.equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return PENDING;
    }

    @Override
    public String toString() {
        return label;
    }
}
