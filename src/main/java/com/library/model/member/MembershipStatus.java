package com.library.model.member;

/**
 * Membership status enum.
 */
public enum MembershipStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired");

    private final String label;

    MembershipStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static MembershipStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return ACTIVE;
        }
        for (MembershipStatus status : values()) {
            if (status.name().equalsIgnoreCase(statusStr.trim()) || status.label.equalsIgnoreCase(statusStr.trim())) {
                return status;
            }
        }
        return ACTIVE;
    }

    @Override
    public String toString() {
        return label;
    }
}
