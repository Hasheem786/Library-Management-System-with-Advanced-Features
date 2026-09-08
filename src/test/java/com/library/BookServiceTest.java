package com.library;

import com.library.model.book.*;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        bookService = new BookService(bookRepository);
    }

    @Test
    void testAddBookSuccess() {
        Book book = new PhysicalBook("978-1111111111", "Java Design Patterns", List.of("Author A"),
                "Tech Press", 2022, 350, BookCategory.TECHNOLOGY, 3, "Shelf 1", 0.5, BookCondition.NEW);

        assertTrue(bookService.addBook(book));
        assertTrue(bookService.getBookByIsbn("978-1111111111").isPresent());
    }

    @Test
    void testAddBookDuplicateIsbnThrowsException() {
        Book book1 = new PhysicalBook("978-1111111111", "Java Design Patterns", List.of("Author A"),
                "Tech Press", 2022, 350, BookCategory.TECHNOLOGY, 3, "Shelf 1", 0.5, BookCondition.NEW);
        Book book2 = new EBook("978-1111111111", "Duplicate Book", List.of("Author B"),
                "Press B", 2023, 200, BookCategory.FICTION, 1, 5.0, "PDF", "http://dl.com");

        bookService.addBook(book1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book2));
    }

    @Test
    void testUpdateBookStockAndCondition() {
        Book book = new PhysicalBook("978-2222222222", "Clean Architecture", List.of("Robert Martin"),
                "Prentice Hall", 2017, 400, BookCategory.TECHNOLOGY, 2, "Shelf 2", 0.8, BookCondition.GOOD);
        bookService.addBook(book);

        assertTrue(bookService.updateBookStock("978-2222222222", 5));
        assertEquals(5, bookService.getBookByIsbn("978-2222222222").get().getTotalCopies());

        assertTrue(bookService.updateBookCondition("978-2222222222", BookCondition.DAMAGED));
        PhysicalBook pb = (PhysicalBook) bookService.getBookByIsbn("978-2222222222").get();
        assertEquals(BookCondition.DAMAGED, pb.getCondition());
    }
}
