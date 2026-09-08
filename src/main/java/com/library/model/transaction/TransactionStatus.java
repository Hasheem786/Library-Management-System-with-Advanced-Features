package com.library.model.transaction;

/**
 * Status enumeration for borrow transactions.
 */
public enum TransactionStatus {
    ISSUED("Issued"),
    RETURNED("Returned"),
    RENEWED("Renewed"),
    OVERDUE("Overdue");

    private final String label;

    TransactionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TransactionStatus fromString(String str) {
        if (str == null || str.trim().isEmpty()) return ISSUED;
        for (TransactionStatus s : values()) {
            if (s.name().equalsIgnoreCase(str.trim()) || s.label.equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return ISSUED;
    }

    @Override
    public String toString() {
        return label;
    }
}
