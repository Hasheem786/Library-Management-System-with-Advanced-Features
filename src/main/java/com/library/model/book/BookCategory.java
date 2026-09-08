package com.library.model.book;

/**
 * Enumeration representing categories/genres of books in the library.
 */
public enum BookCategory {
    FICTION("Fiction"),
    NON_FICTION("Non-Fiction"),
    SCIENCE("Science"),
    TECHNOLOGY("Technology"),
    HISTORY("History"),
    BIOGRAPHY("Biography"),
    FANTASY("Fantasy"),
    MYSTERY("Mystery"),
    PHILOSOPHY("Philosophy"),
    SELF_HELP("Self-Help");

    private final String displayName;

    BookCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Case-insensitive lookup for BookCategory.
     */
    public static BookCategory fromString(String categoryStr) {
        if (categoryStr == null || categoryStr.trim().isEmpty()) {
            return FICTION;
        }
        String clean = categoryStr.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        for (BookCategory category : values()) {
            if (category.name().equalsIgnoreCase(clean) || category.displayName.equalsIgnoreCase(categoryStr.trim())) {
                return category;
            }
        }
        return FICTION;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
