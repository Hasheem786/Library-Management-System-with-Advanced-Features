package com.library.model.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a book in the library management system.
 * Demonstrates encapsulation, abstraction, and inheritance hierarchy.
 */
public abstract class Book {
    private String isbn;
    private String title;
    private List<String> authors;
    private String publisher;
    private int publicationYear;
    private int pages;
    private BookCategory category;
    private int totalCopies;
    private int availableCopies;
    private int reservedCopies;
    private int borrowCount;

    /**
     * Constructs a Book instance.
     */
    public Book(String isbn, String title, List<String> authors, String publisher,
                int publicationYear, int pages, BookCategory category, int totalCopies) {
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null").trim();
        this.title = Objects.requireNonNull(title, "Title cannot be null").trim();
        this.authors = (authors != null) ? new ArrayList<>(authors) : new ArrayList<>();
        this.publisher = (publisher != null) ? publisher.trim() : "Unknown Publisher";
        this.publicationYear = publicationYear;
        this.pages = pages;
        this.category = (category != null) ? category : BookCategory.FICTION;
        this.totalCopies = Math.max(1, totalCopies);
        this.availableCopies = this.totalCopies;
        this.reservedCopies = 0;
        this.borrowCount = 0;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public String getAuthorsAsString() {
        return String.join(", ", authors);
    }

    public void setAuthors(List<String> authors) {
        this.authors = (authors != null) ? new ArrayList<>(authors) : new ArrayList<>();
    }

    public void addAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.authors.add(author.trim());
        }
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public BookCategory getCategory() {
        return category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        int copyDiff = totalCopies - this.totalCopies;
        this.totalCopies = totalCopies;
        this.availableCopies = Math.max(0, this.availableCopies + copyDiff);
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getReservedCopies() {
        return reservedCopies;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = Math.max(0, borrowCount);
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public synchronized boolean decrementAvailableCopies() {
        if (availableCopies > 0) {
            availableCopies--;
            borrowCount++;
            return true;
        }
        return false;
    }

    public synchronized void incrementAvailableCopies() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public synchronized void incrementReservedCopies() {
        reservedCopies++;
    }

    public synchronized void decrementReservedCopies() {
        if (reservedCopies > 0) {
            reservedCopies--;
        }
    }

    /**
     * Abstract method returning the specific type of book (Physical, E-Book, Audio).
     */
    public abstract String getBookType();

    /**
     * Abstract method returning type-specific metadata representation.
     */
    public abstract String getSpecificDetails();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("[%s] ISBN: %s | Title: '%s' | Author(s): %s | Category: %s | Avail: %d/%d",
                getBookType(), isbn, title, getAuthorsAsString(), category.getDisplayName(), availableCopies, totalCopies);
    }
}
