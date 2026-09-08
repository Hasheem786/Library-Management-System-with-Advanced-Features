package com.library;

import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import com.library.service.*;
import com.library.ui.ConsoleUI;
import com.library.util.SampleDataGenerator;

/**
 * Main application entry point for the Library Management System with Advanced Features.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     INITIALIZING LIBRARY MANAGEMENT SYSTEM WITH ADVANCED FEATURES             ");
        System.out.println("================================================================================");

        // 1. Initialize Repositories
        BookRepository bookRepository = new BookRepository();
        MemberRepository memberRepository = new MemberRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        // 2. Initialize Services
        FineCalculatorService fineCalculatorService = new FineCalculatorService();
        BookService bookService = new BookService(bookRepository);
        MemberService memberService = new MemberService(memberRepository);
        BorrowingService borrowingService = new BorrowingService(bookRepository, memberRepository, transactionRepository, fineCalculatorService);
        SearchService searchService = new SearchService(bookRepository, memberRepository, transactionRepository);
        RecommendationService recommendationService = new RecommendationService(bookRepository, memberRepository, transactionRepository);
        AnalyticsService analyticsService = new AnalyticsService(bookRepository, memberRepository, transactionRepository);
        DataPersistenceService persistenceService = new DataPersistenceService(bookRepository, memberRepository, transactionRepository);

        // 3. Seed Sample Data
        System.out.println("[INFO] Seeding initial sample data (Books, Members, Transactions, Fines, Reservations)...");
        SampleDataGenerator.populateSampleData(bookRepository, memberRepository, transactionRepository);
        persistenceService.exportAllData();
        System.out.printf("[INFO] System initialized successfully. Loaded %d Books, %d Members, %d Transactions.\n",
                bookRepository.count(), memberRepository.count(), transactionRepository.findAllTransactions().size());

        // 4. Launch Interactive CLI Menu
        ConsoleUI consoleUI = new ConsoleUI(
                bookService,
                memberService,
                borrowingService,
                searchService,
                recommendationService,
                analyticsService,
                persistenceService
        );

        consoleUI.start();
    }
}
