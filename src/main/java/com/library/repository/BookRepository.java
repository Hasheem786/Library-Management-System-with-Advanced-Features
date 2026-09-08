package com.library.repository;

import com.library.model.book.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe repository managing in-memory Book data storage.
 */
public class BookRepository {
    private final Map<String, Book> bookMap = new ConcurrentHashMap<>();

    public boolean save(Book book) {
        if (book == null || book.getIsbn() == null) return false;
        bookMap.put(book.getIsbn().toUpperCase(), book);
        return true;
    }

    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null) return Optional.empty();
        return Optional.ofNullable(bookMap.get(isbn.toUpperCase().trim()));
    }

    public List<Book> findAll() {
        return new ArrayList<>(bookMap.values());
    }

    public boolean deleteByIsbn(String isbn) {
        if (isbn == null) return false;
        return bookMap.remove(isbn.toUpperCase().trim()) != null;
    }

    public boolean existsByIsbn(String isbn) {
        if (isbn == null) return false;
        return bookMap.containsKey(isbn.toUpperCase().trim());
    }

    public void clear() {
        bookMap.clear();
    }

    public int count() {
        return bookMap.size();
    }
}
