package com.library.ui;

import com.library.model.book.*;
import com.library.model.member.*;
import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.Reservation;
import com.library.service.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Interactive Console Interface matching the specified 6-level hierarchical Menu Structure.
 */
public class ConsoleUI {
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowingService borrowingService;
    private final SearchService searchService;
    private final RecommendationService recommendationService;
    private final AnalyticsService analyticsService;
    private final DataPersistenceService persistenceService;
    private final Scanner scanner;

    public ConsoleUI(BookService bookService,
                     MemberService memberService,
                     BorrowingService borrowingService,
                     SearchService searchService,
                     RecommendationService recommendationService,
                     AnalyticsService analyticsService,
                     DataPersistenceService persistenceService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.borrowingService = borrowingService;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
        this.analyticsService = analyticsService;
        this.persistenceService = persistenceService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleBookOperations();
                case "2" -> handleMemberOperations();
                case "3" -> handleBorrowingOperations();
                case "4" -> handleAdvancedFeatures();
                case "5" -> handleReportsAndAnalytics();
                case "6" -> handleSystemOperations();
                case "0" -> {
                    System.out.println("\nPersisting data and exiting system. Goodbye!");
                    persistenceService.exportAllData();
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please enter a valid menu number (0-6).");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("""

            ================================================================================
            ===                       LIBRARY MANAGEMENT SYSTEM                          ===
            ================================================================================
            1. Book Operations
            2. Member Operations
            3. Borrowing Operations
            4. Advanced Features
            5. Reports & Analytics
            6. System Operations
            0. Exit System
            --------------------------------------------------------------------------------
            Select Choice [0-6]:\s""");
    }

    // 1. Book Operations
    private void handleBookOperations() {
        System.out.println("""
            --- 1. BOOK OPERATIONS ---
            1.1 Add New Book
            1.2 Update Book Info
            1.3 Search Books
            1.4 View Book Details
            1.5 Check Availability
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1.1", "1" -> addNewBook();
            case "1.2", "2" -> updateBookInfo();
            case "1.3", "3" -> searchBooks();
            case "1.4", "4" -> viewBookDetails();
            case "1.5", "5" -> checkAvailability();
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private void addNewBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Select Book Type (1: Physical, 2: E-Book, 3: AudioBook): ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author(s) (comma-separated): ");
        List<String> authors = Arrays.stream(scanner.nextLine().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        System.out.print("Enter Publisher: ");
        String publisher = scanner.nextLine().trim();
        System.out.print("Enter Publication Year: ");
        int year = parseIntegerInput(scanner.nextLine(), 2024);
        System.out.print("Enter Pages: ");
        int pages = parseIntegerInput(scanner.nextLine(), 300);

        System.out.println("Categories: FICTION, NON_FICTION, SCIENCE, TECHNOLOGY, HISTORY, BIOGRAPHY, FANTASY, MYSTERY");
        System.out.print("Enter Category: ");
        BookCategory category = BookCategory.fromString(scanner.nextLine().trim());
        System.out.print("Enter Total Copies: ");
        int copies = parseIntegerInput(scanner.nextLine(), 1);

        Book book;
        if ("2".equals(typeChoice)) {
            System.out.print("Enter File Size (MB): ");
            double size = parseDoubleInput(scanner.nextLine(), 10.0);
            System.out.print("Enter Format (PDF/EPUB/MOBI): ");
            String format = scanner.nextLine().trim();
            System.out.print("Enter Download URL: ");
            String url = scanner.nextLine().trim();
            book = new EBook(isbn, title, authors, publisher, year, pages, category, copies, size, format, url);
        } else if ("3".equals(typeChoice)) {
            System.out.print("Enter Duration (minutes): ");
            int duration = parseIntegerInput(scanner.nextLine(), 180);
            System.out.print("Enter Narrator: ");
            String narrator = scanner.nextLine().trim();
            System.out.print("Enter Audio Format (MP3/AAC): ");
            String audioFormat = scanner.nextLine().trim();
            book = new AudioBook(isbn, title, authors, publisher, year, pages, category, copies, duration, narrator, audioFormat);
        } else {
            System.out.print("Enter Shelf Location (e.g. Sec A-101): ");
            String shelf = scanner.nextLine().trim();
            System.out.print("Enter Weight (kg): ");
            double weight = parseDoubleInput(scanner.nextLine(), 0.5);
            book = new PhysicalBook(isbn, title, authors, publisher, year, pages, category, copies, shelf, weight, BookCondition.NEW);
        }

        try {
            boolean added = bookService.addBook(book);
            if (added) {
                System.out.println("✔ Book added successfully! " + book);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void updateBookInfo() {
        System.out.print("Enter ISBN of book to update: ");
        String isbn = scanner.nextLine().trim();
        Optional<Book> bookOpt = bookService.getBookByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            System.out.println("❌ Book not found.");
            return;
        }

        Book book = bookOpt.get();
        System.out.println("Current details: " + book);
        System.out.print("Enter New Title (leave blank to keep current): ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) book.setTitle(title);

        System.out.print("Enter New Total Copies (or 0 to keep current): ");
        int copies = parseIntegerInput(scanner.nextLine(), 0);
        if (copies > 0) book.setTotalCopies(copies);

        if (book instanceof PhysicalBook pb) {
            System.out.print("Update Condition (NEW, GOOD, FAIR, POOR, DAMAGED) or blank: ");
            String condStr = scanner.nextLine().trim();
            if (!condStr.isEmpty()) pb.setCondition(BookCondition.fromString(condStr));
        }

        bookService.updateBook(book);
        System.out.println("✔ Book updated successfully!");
    }

    private void searchBooks() {
        System.out.print("Enter Search Keyword (Title/Author/ISBN/Publisher): ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = searchService.multiCriteriaSearch(keyword, null, null, false);

        System.out.printf("\n--- Search Results (%d found) ---\n", results.size());
        results.forEach(b -> System.out.println(" • " + b + " | " + b.getSpecificDetails()));
    }

    private void viewBookDetails() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        Optional<Book> bookOpt = bookService.getBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book b = bookOpt.get();
            System.out.println("\n=== BOOK DETAILS ===");
            System.out.println("Type        : " + b.getBookType());
            System.out.println("ISBN        : " + b.getIsbn());
            System.out.println("Title       : " + b.getTitle());
            System.out.println("Author(s)   : " + b.getAuthorsAsString());
            System.out.println("Publisher   : " + b.getPublisher() + " (" + b.getPublicationYear() + ")");
            System.out.println("Category    : " + b.getCategory().getDisplayName());
            System.out.println("Copies      : Total=" + b.getTotalCopies() + ", Available=" + b.getAvailableCopies() + ", Reserved=" + b.getReservedCopies());
            System.out.println("Borrow Count: " + b.getBorrowCount());
            System.out.println("Specifics   : " + b.getSpecificDetails());
        } else {
            System.out.println("❌ Book not found.");
        }
    }

    private void checkAvailability() {
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine().trim();
        Optional<Book> bookOpt = bookService.getBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book b = bookOpt.get();
            System.out.printf("'%s' Availability: %d / %d copies available (Reserved: %d)\n",
                    b.getTitle(), b.getAvailableCopies(), b.getTotalCopies(), b.getReservedCopies());
        } else {
            System.out.println("❌ Book not found.");
        }
    }

    // 2. Member Operations
    private void handleMemberOperations() {
        System.out.println("""
            --- 2. MEMBER OPERATIONS ---
            2.1 Register Member
            2.2 Update Member Info
            2.3 Search Members
            2.4 View Member History
            2.5 Fine Management
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "2.1", "1" -> registerMember();
            case "2.2", "2" -> updateMemberInfo();
            case "2.3", "3" -> searchMembers();
            case "2.4", "4" -> viewMemberHistory();
            case "2.5", "5" -> handleFineManagement();
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private void registerMember() {
        System.out.println("\n--- Register Member ---");
        System.out.print("Select Type (1: Student, 2: Faculty, 3: General Public): ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Enter Member ID (e.g. STU-1005): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        Member member;
        if ("2".equals(typeChoice)) {
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine().trim();
            System.out.print("Enter Designation: ");
            String desig = scanner.nextLine().trim();
            member = new FacultyMember(id, name, email, phone, address, LocalDate.now(), dept, desig);
        } else if ("3".equals(typeChoice)) {
            System.out.print("Enter Occupation: ");
            String occ = scanner.nextLine().trim();
            member = new GeneralMember(id, name, email, phone, address, LocalDate.now(), occ);
        } else {
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine().trim();
            System.out.print("Enter Student ID Number: ");
            String stuId = scanner.nextLine().trim();
            member = new StudentMember(id, name, email, phone, address, LocalDate.now(), dept, stuId);
        }

        try {
            boolean reg = memberService.registerMember(member);
            if (reg) {
                System.out.println("✔ Member registered successfully!");
                System.out.println(memberService.generateMembershipCard(id));
            }
        } catch (Exception e) {
            System.out.println("❌ Registration Failed: " + e.getMessage());
        }
    }

    private void updateMemberInfo() {
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine().trim();
        Optional<Member> memberOpt = memberService.getMemberById(id);
        if (memberOpt.isEmpty()) {
            System.out.println("❌ Member not found.");
            return;
        }

        Member m = memberOpt.get();
        System.out.print("Enter New Phone (leave blank to skip): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) m.setPhone(phone);

        System.out.print("Enter New Address (leave blank to skip): ");
        String addr = scanner.nextLine().trim();
        if (!addr.isEmpty()) m.setAddress(addr);

        System.out.print("Status update (ACTIVE, SUSPENDED, EXPIRED) or blank: ");
        String statusStr = scanner.nextLine().trim();
        if (!statusStr.isEmpty()) m.setStatus(MembershipStatus.fromString(statusStr));

        memberService.updateMember(m);
        System.out.println("✔ Member info updated successfully!");
    }

    private void searchMembers() {
        System.out.print("Enter Member Name / Email / ID: ");
        String query = scanner.nextLine().trim();
        List<Member> members = memberService.searchMembers(query);
        System.out.printf("\n--- Members Found (%d) ---\n", members.size());
        members.forEach(m -> System.out.println(" • " + m));
    }

    private void viewMemberHistory() {
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine().trim();
        Optional<Member> mOpt = memberService.getMemberById(id);
        if (mOpt.isPresent()) {
            Member m = mOpt.get();
            System.out.println("\n=== MEMBER PROFILE & BORROWING HISTORY ===");
            System.out.println(m);
            System.out.println("Currently Borrowed ISBNs: " + m.getCurrentBorrowedIsbns());
            System.out.println("Total Borrow History Count: " + m.getBorrowingHistoryIsbns().size());
            System.out.println("Borrowing History ISBNs: " + m.getBorrowingHistoryIsbns());
        } else {
            System.out.println("❌ Member not found.");
        }
    }

    private void handleFineManagement() {
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine().trim();
        Optional<Member> mOpt = memberService.getMemberById(id);
        if (mOpt.isEmpty()) {
            System.out.println("❌ Member not found.");
            return;
        }

        Member m = mOpt.get();
        System.out.printf("Member: %s | Current Unpaid Fine Balance: $%.2f\n", m.getName(), m.getUnpaidFinesBalance());
        if (m.getUnpaidFinesBalance() > 0) {
            System.out.print("Enter Payment Amount ($): ");
            double amt = parseDoubleInput(scanner.nextLine(), 0.0);
            if (amt > 0) {
                boolean paid = memberService.processFinePayment(id, amt);
                if (paid) {
                    System.out.printf("✔ Fine payment of $%.2f processed. Remaining balance: $%.2f\n", amt, m.getUnpaidFinesBalance());
                }
            }
        }
    }

    // 3. Borrowing Operations
    private void handleBorrowingOperations() {
        System.out.println("""
            --- 3. BORROWING OPERATIONS ---
            3.1 Issue Book
            3.2 Return Book
            3.3 Renew Book
            3.4 Reserve Book
            3.5 View Overdue Books
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "3.1", "1" -> issueBook();
            case "3.2", "2" -> returnBook();
            case "3.3", "3" -> renewBook();
            case "3.4", "4" -> reserveBook();
            case "3.5", "5" -> viewOverdueBooks();
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private void issueBook() {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine().trim();

        try {
            BorrowTransaction tx = borrowingService.issueBook(memberId, isbn);
            System.out.println("✔ Book Issued Successfully!");
            System.out.println("  Transaction ID : " + tx.getTransactionId());
            System.out.println("  Due Date       : " + tx.getDueDate());
        } catch (Exception e) {
            System.out.println("❌ Issue Error: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.print("Enter Transaction ID: ");
        String txId = scanner.nextLine().trim();
        try {
            double fine = borrowingService.returnBook(txId);
            System.out.println("✔ Book Returned Successfully!");
            if (fine > 0) {
                System.out.printf("⚠️ Overdue fine assessed: $%.2f (Added to member fine balance)\n", fine);
            } else {
                System.out.println("  No overdue fine assessed.");
            }
        } catch (Exception e) {
            System.out.println("❌ Return Error: " + e.getMessage());
        }
    }

    private void renewBook() {
        System.out.print("Enter Transaction ID to Renew: ");
        String txId = scanner.nextLine().trim();
        try {
            boolean renewed = borrowingService.renewBook(txId);
            if (renewed) {
                System.out.println("✔ Book renewed successfully!");
            }
        } catch (Exception e) {
            System.out.println("❌ Renewal Error: " + e.getMessage());
        }
    }

    private void reserveBook() {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine().trim();

        try {
            Reservation res = borrowingService.reserveBook(memberId, isbn);
            System.out.println("✔ Book Reserved Successfully! Reservation ID: " + res.getReservationId());
        } catch (Exception e) {
            System.out.println("❌ Reservation Error: " + e.getMessage());
        }
    }

    private void viewOverdueBooks() {
        List<BorrowTransaction> overdue = borrowingService.getOverdueTransactions(LocalDate.now());
        System.out.printf("\n--- OVERDUE TRANSACTIONS (%d) ---\n", overdue.size());
        overdue.forEach(tx -> System.out.println(" • " + tx));
    }

    // 4. Advanced Features
    private void handleAdvancedFeatures() {
        System.out.println("""
            --- 4. ADVANCED FEATURES ---
            4.1 Book Recommendations
            4.2 Advanced Search (Multi-criteria Stream Filtering)
            4.3 Popular Books Report
            4.4 Member Analytics
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "4.1", "1" -> getBookRecommendations();
            case "4.2", "2" -> performAdvancedStreamSearch();
            case "4.3", "3" -> viewPopularBooksReport();
            case "4.4", "4" -> viewMemberAnalytics();
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private void getBookRecommendations() {
        System.out.print("Enter Member ID for personalized recommendations: ");
        String memberId = scanner.nextLine().trim();

        System.out.println("\n--- Personalized History-Based Recommendations ---");
        List<Book> historyRecs = recommendationService.suggestBooksByMemberHistory(memberId, 5);
        historyRecs.forEach(b -> System.out.println(" • " + b));

        System.out.println("\n--- Collaborative Filtering Recommendations (Similar Members) ---");
        List<Book> cfRecs = recommendationService.suggestCollaborativeFiltering(memberId, 5);
        cfRecs.forEach(b -> System.out.println(" • " + b));
    }

    private void performAdvancedStreamSearch() {
        System.out.println("\n--- Stream Multi-Criteria Filter ---");
        System.out.print("Enter Category (or press Enter to skip): ");
        String catStr = scanner.nextLine().trim();
        BookCategory cat = catStr.isEmpty() ? null : BookCategory.fromString(catStr);

        System.out.print("Enter Start Year (or 0 to skip): ");
        int startYear = parseIntegerInput(scanner.nextLine(), 0);

        System.out.print("Enter End Year (or 0 to skip): ");
        int endYear = parseIntegerInput(scanner.nextLine(), 0);

        List<Book> books = searchService.filterAvailableBooks(
                cat,
                startYear > 0 ? startYear : null,
                endYear > 0 ? endYear : null
        );

        System.out.printf("\nFound %d matching available books:\n", books.size());
        books.forEach(b -> System.out.println(" • " + b));
    }

    private void viewPopularBooksReport() {
        System.out.println("\n--- Most Popular Books Overall ---");
        List<Book> popular = recommendationService.suggestPopularBooksOverall(10);
        popular.forEach(b -> System.out.printf(" • Borrowed %d times | %s\n", b.getBorrowCount(), b));
    }

    private void viewMemberAnalytics() {
        System.out.println(analyticsService.generateMemberEngagementReport());
    }

    // 5. Reports & Analytics
    private void handleReportsAndAnalytics() {
        System.out.println("""
            --- 5. REPORTS & ANALYTICS ---
            5.1 Borrowing Reports
            5.2 Fine Collection Reports
            5.3 Book Popularity Analysis
            5.4 Member Engagement Reports
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "5.1", "1" -> generateBorrowingReports();
            case "5.2", "2" -> generateFineReports();
            case "5.3", "3" -> generatePopularityAnalysis();
            case "5.4", "4" -> viewMemberAnalytics();
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private void generateBorrowingReports() {
        System.out.println("\n--- Monthly Borrowing Trends by Category ---");
        analyticsService.getMonthlyBorrowingTrendsByCategory()
                .forEach((ym, catMap) -> {
                    System.out.println("Month: " + ym);
                    catMap.forEach((category, count) ->
                            System.out.println("  • Category: " + category.getDisplayName() + " -> " + count + " borrows"));
                });
    }

    private void generateFineReports() {
        System.out.println("\n--- Fine Revenue & Collection Report ---");
        System.out.printf("Total Collected Fine Revenue : $%.2f\n", analyticsService.getTotalFineCollectionRevenue());
        System.out.printf("Total Outstanding Fines      : $%.2f\n", analyticsService.getTotalPendingFinesAmount());
    }

    private void generatePopularityAnalysis() {
        System.out.println("\n--- Book Turnover Rates ---");
        analyticsService.getBookTurnoverRates().forEach((title, rate) ->
                System.out.printf(" • %-40s | Turnover Rate: %.2f\n", title, rate));
    }

    // 6. System Operations
    private void handleSystemOperations() {
        System.out.println("""
            --- 6. SYSTEM OPERATIONS ---
            6.1 Data Backup (Create Snapshot)
            6.2 Data Import/Export (Persist Current State)
            6.3 System Configuration
            0. Back to Main Menu
            Enter Sub-Option:""");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "6.1", "1" -> {
                String path = persistenceService.createBackupSnapshot();
                if (path != null) System.out.println("✔ Backup snapshot created at: " + path);
            }
            case "6.2", "2" -> {
                boolean ok = persistenceService.exportAllData();
                if (ok) System.out.println("✔ Data persisted to JSON files in data/ directory.");
            }
            case "6.3", "3" -> {
                System.out.println("=== SYSTEM CONFIGURATION ===");
                System.out.println(" Java Version     : 21 OpenJDK");
                System.out.println(" Storage Location : data/");
                System.out.println(" Default Currency : USD ($)");
            }
            case "0" -> {}
            default -> System.out.println("Invalid sub-option.");
        }
    }

    private int parseIntegerInput(String input, int defaultVal) {
        try {
            return Integer.parseInt(input.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private double parseDoubleInput(String input, double defaultVal) {
        try {
            return Double.parseDouble(input.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
