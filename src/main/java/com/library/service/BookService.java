package com.library.service;

import com.library.model.book.Book;
import com.library.model.book.BookCategory;
import com.library.model.book.BookCondition;
import com.library.model.book.PhysicalBook;
import com.library.repository.BookRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business logic service managing Book lifecycle, catalog reporting, stock updates, and condition tracking.
 */
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a new book with duplicate ISBN validation.
     */
    public boolean addBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid book details: ISBN is required.");
        }

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists in the system.");
        }

        return bookRepository.save(book);
    }

    /**
     * Updates existing book details.
     */
    public boolean updateBook(Book updatedBook) {
        if (updatedBook == null || !bookRepository.existsByIsbn(updatedBook.getIsbn())) {
            return false;
        }
        return bookRepository.save(updatedBook);
    }

    /**
     * Retrieves book by ISBN.
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Returns all books in the catalog.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Updates physical book condition.
     */
    public boolean updateBookCondition(String isbn, BookCondition newCondition) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isPresent() && bookOpt.get() instanceof PhysicalBook physicalBook) {
            physicalBook.setCondition(newCondition);
            return bookRepository.save(physicalBook);
        }
        return false;
    }

    /**
     * Updates total copies and available copies.
     */
    public boolean updateBookStock(String isbn, int totalCopies) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setTotalCopies(totalCopies);
            return bookRepository.save(book);
        }
        return false;
    }

    /**
     * Generates book catalog report grouped by category using Java Streams.
     */
    public String generateCatalogReport() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            return "No books registered in the catalog.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== LIBRARY BOOK CATALOG REPORT ===\n");
        sb.append(String.format("Total Titles: %d | Total Physical Copies: %d\n",
                books.size(), books.stream().mapToInt(Book::getTotalCopies).sum()));
        sb.append("--------------------------------------------------------------------------------\n");

        books.stream()
                .collect(Collectors.groupingBy(Book::getCategory))
                .forEach((category, categoryBooks) -> {
                    sb.append(String.format("\n[Category: %s (%d titles)]\n", category.getDisplayName(), categoryBooks.size()));
                    categoryBooks.stream()
                            .sorted(Comparator.comparing(Book::getTitle))
                            .forEach(b -> sb.append(String.format("  • %s | %s\n    %s\n",
                                    b.toString(), b.getSpecificDetails(), b.getAuthorsAsString())));
                });

        return sb.toString();
    }
}
