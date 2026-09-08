package com.library;

import com.library.model.book.*;
import com.library.model.member.Member;
import com.library.model.member.StudentMember;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import com.library.service.RecommendationService;
import com.library.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchAndRecommendationTest {

    private BookRepository bookRepository;
    private MemberRepository memberRepository;
    private TransactionRepository transactionRepository;
    private SearchService searchService;
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        memberRepository = new MemberRepository();
        transactionRepository = new TransactionRepository();
        searchService = new SearchService(bookRepository, memberRepository, transactionRepository);
        recommendationService = new RecommendationService(bookRepository, memberRepository, transactionRepository);

        Book b1 = new PhysicalBook("ISBN-1", "Java Programming", List.of("James Gosling", "Joshua Bloch"), "Sun",
                2020, 500, BookCategory.TECHNOLOGY, 3, "Shelf A", 1.0, BookCondition.NEW);
        Book b2 = new EBook("ISBN-2", "Python Basics", List.of("Guido van Rossum"), "O'Reilly",
                2021, 300, BookCategory.TECHNOLOGY, 5, 10.0, "PDF", "http://py.com");
        Book b3 = new PhysicalBook("ISBN-3", "World History", List.of("Jared Diamond"), "Penguin",
                2015, 600, BookCategory.HISTORY, 2, "Shelf B", 0.9, BookCondition.GOOD);

        b1.setBorrowCount(15);
        b2.setBorrowCount(25);
        b3.setBorrowCount(5);

        bookRepository.save(b1);
        bookRepository.save(b2);
        bookRepository.save(b3);
    }

    @Test
    void testSearchBooksByAuthorsContains() {
        List<Book> result = searchService.searchBooksByAuthors(List.of("bloch"));
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getTitle());
    }

    @Test
    void testFilterAvailableBooksByCategoryAndYear() {
        List<Book> techBooks = searchService.filterAvailableBooks(BookCategory.TECHNOLOGY, 2020, 2022);
        assertEquals(2, techBooks.size());
    }

    @Test
    void testRecommendationPopularBooksOverall() {
        List<Book> popular = recommendationService.suggestPopularBooksOverall(2);
        assertEquals(2, popular.size());
        assertEquals("Python Basics", popular.get(0).getTitle()); // Highest borrow count 25
    }
}
