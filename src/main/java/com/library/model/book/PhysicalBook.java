package com.library.model.book;

import java.util.List;

/**
 * Concrete implementation representing a physical printed book in the library.
 */
public class PhysicalBook extends Book {
    private String shelfLocation;
    private double weightKg;
    private BookCondition condition;

    public PhysicalBook(String isbn, String title, List<String> authors, String publisher,
                        int publicationYear, int pages, BookCategory category, int totalCopies,
                        String shelfLocation, double weightKg, BookCondition condition) {
        super(isbn, title, authors, publisher, publicationYear, pages, category, totalCopies);
        this.shelfLocation = (shelfLocation != null) ? shelfLocation : "Unassigned Shelf";
        this.weightKg = weightKg;
        this.condition = (condition != null) ? condition : BookCondition.GOOD;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public BookCondition getCondition() {
        return condition;
    }

    public void setCondition(BookCondition condition) {
        this.condition = condition;
    }

    @Override
    public String getBookType() {
        return "Physical Book";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Shelf: %s | Weight: %.2f kg | Condition: %s",
                shelfLocation, weightKg, condition.getLabel());
    }
}
