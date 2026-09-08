package com.library.model.book;

/**
 * Physical condition status of a physical book item.
 */
public enum BookCondition {
    NEW("New"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    DAMAGED("Damaged");

    private final String label;

    BookCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BookCondition fromString(String conditionStr) {
        if (conditionStr == null || conditionStr.trim().isEmpty()) {
            return GOOD;
        }
        for (BookCondition c : values()) {
            if (c.name().equalsIgnoreCase(conditionStr.trim()) || c.label.equalsIgnoreCase(conditionStr.trim())) {
                return c;
            }
        }
        return GOOD;
    }

    @Override
    public String toString() {
        return label;
    }
}
