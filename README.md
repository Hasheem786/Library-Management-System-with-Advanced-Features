# Library Management System with Advanced Features

> **Submit-Ready Java 21 Application**  
> Comprehensive, production-grade Library Management System built using Object-Oriented Programming (OOP) principles, Java 21 Streams & Lambdas, JUnit 5 unit testing framework, JSON data persistence, and an interactive 6-level hierarchical CLI.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Features](#-key-features)
3. [System Architecture & Package Structure](#-system-architecture--package-structure)
4. [Prerequisites & System Requirements](#-prerequisites--system-requirements)
5. [Build & Execution Instructions](#-build--execution-instructions)
   - [Method 1: Run with Maven (Recommended)](#method-1-run-with-maven-recommended)
   - [Method 2: Standalone Compilation & Direct Execution](#method-2-standalone-compilation--direct-execution)
   - [Method 3: Running Automated JUnit 5 Unit Tests](#method-3-running-automated-junit-5-unit-tests)
6. [Core Technical Implementations](#-core-technical-implementations)
   - [OOP Inheritance & Polymorphism](#1-oop-inheritance--polymorphism)
   - [Java 21 Streams & Lambdas Code Examples](#2-java-21-streams--lambdas-code-examples)
   - [Business Validation Scenarios](#3-business-validation-scenarios)
7. [CLI Menu Navigation](#-cli-menu-navigation)
8. [Data Persistence & Backup](#-data-persistence--backup)
9. [Deliverables Summary](#-deliverables-summary)

---

## 🌟 Project Overview

The **Library Management System with Advanced Features** is designed to automate modern library operations, including book inventory management across multiple physical and digital formats, member registrations with customized privilege profiles, borrowing/returning workflows with dynamic fine calculations, reservation queues, personalized recommendation engines, and real-time analytical reporting.

---

## 🚀 Key Features

### 1. Book Management
* **Polymorphic Book Types**:
  * `PhysicalBook`: Tracks shelf location, weight (kg), and physical condition (`NEW`, `GOOD`, `FAIR`, `POOR`, `DAMAGED`).
  * `EBook`: Digital books with file size (MB), format (`PDF`, `EPUB`, `MOBI`), and secure download URL.
  * `AudioBook`: Spoken audio books with duration (minutes), narrator name, and audio format (`MP3`, `AAC`).
* **Categorization**: Enum-driven (`FICTION`, `NON_FICTION`, `SCIENCE`, `TECHNOLOGY`, `HISTORY`, `BIOGRAPHY`, `FANTASY`, `MYSTERY`, `PHILOSOPHY`, `SELF_HELP`).
* **Duplicate Validation**: Guaranteed unique ISBN enforcement on book creation.
* **Copy Availability Tracking**: Total copies, available copies, reserved copies, and total borrow counts.

### 2. Member Management
* **Member Privilege Profiles**:
  * `StudentMember`: Max 5 books, 14-day loan period, $0.50/day fine, 2-day grace period, $20.00 max fine cap, 2 max renewals.
  * `FacultyMember`: Max 10 books, 30-day loan period, $0.25/day fine, 5-day grace period, $50.00 max fine cap, 3 max renewals.
  * `GeneralMember`: Max 3 books, 14-day loan period, $1.00/day fine, 1-day grace period, $30.00 max fine cap, 1 max renewal.
* **Digital Membership Card Generation**: ASCII-formatted printable membership cards.
* **Fine Tracking**: Balance tracking, fine payment handling, and status management (`ACTIVE`, `SUSPENDED`, `EXPIRED`).

### 3. Borrowing & Reservation System
* **Validation Rules**: Enforces active member status, copy availability, borrowing limit checks, unpaid fine threshold ($15 limit), and reservation priority queue.
* **Fine Calculation Engine**: Dynamic fine calculator considering grace periods, member-specific daily rates, 1.5x compound penalties for extended overdue (>14 days), and max caps.
* **Reservation System**: Priority queue per book for handling waiting lists on popular titles.

### 4. Advanced Search & Recommendation Engine
* **Multi-Criteria Stream Search**: Instant filtering by title, multi-author `contains()` matching, publication year ranges, category, and availability status.
* **Personalized Recommendation Engine**:
  * *History-Based*: Analyzes member borrowing history to recommend unread books in top categories.
  * *Category Popularity*: Top borrowed titles overall and per category.
  * *Collaborative Filtering*: Identifies reading affinity between members using Jaccard Similarity and recommends books enjoyed by peer members.

### 5. Reporting & Analytics
* Real-time metrics: Monthly borrowing trends by category, average borrowing duration by category using `Collectors.averagingDouble`, book turnover rates (borrow count / total copies), defaulter tracking, and fine collection revenue analysis.

---

## 🏗 System Architecture & Package Structure

```
d:\MH\Airtribe Projects\Library Management System with Advanced Features\
├── pom.xml                                   # Maven Build Configuration (Java 21 & JUnit 5)
├── README.md                                  # Complete Setup & Architecture Documentation
├── TEST_CASES.md                              # Documented Test Scenarios & Test Matrix
├── ARCHITECTURE_AND_UML.md                    # Digital Class & Component Architecture Diagrams
├── data/                                      # JSON Persistence & Sample Data Storage
│   ├── books.json
│   ├── members.json
│   ├── transactions.json
│   ├── reservations.json
│   └── fines.json
└── src/
    ├── main/java/com/library/
    │   ├── Main.java                          # System Launch Entry Point
    │   ├── model/
    │   │   ├── book/                          # Book Domain Model & Enums
    │   │   │   ├── Book.java (abstract)
    │   │   │   ├── PhysicalBook.java
    │   │   │   ├── EBook.java
    │   │   │   ├── AudioBook.java
    │   │   │   ├── BookCategory.java
    │   │   │   └── BookCondition.java
    │   │   ├── member/                        # Member Domain Model & Enums
    │   │   │   ├── Member.java (abstract)
    │   │   │   ├── StudentMember.java
    │   │   │   ├── FacultyMember.java
    │   │   │   ├── GeneralMember.java
    │   │   │   └── MembershipStatus.java
    │   │   └── transaction/                   # Transactions, Fines, Reservations
    │   │       ├── BorrowTransaction.java
    │   │       ├── TransactionStatus.java
    │   │       ├── Reservation.java
    │   │       ├── ReservationStatus.java
    │   │       └── FineRecord.java
    │   ├── repository/                        # In-Memory Concurrent Repositories
    │   │   ├── BookRepository.java
    │   │   ├── MemberRepository.java
    │   │   └── TransactionRepository.java
    │   ├── service/                           # Business Logic & Stream Services
    │   │   ├── BookService.java
    │   │   ├── MemberService.java
    │   │   ├── FineCalculatorService.java
    │   │   ├── BorrowingService.java
    │   │   ├── SearchService.java
    │   │   ├── RecommendationService.java
    │   │   ├── AnalyticsService.java
    │   │   └── DataPersistenceService.java
    │   ├── ui/
    │   │   └── ConsoleUI.java                 # Interactive 6-Level CLI Navigation
    │   └── util/
    │       └── SampleDataGenerator.java       # Seed Data Population Utility
    └── test/java/com/library/                 # JUnit 5 Automated Unit Test Suite
        ├── BookServiceTest.java
        ├── MemberServiceTest.java
        ├── BorrowingServiceTest.java
        ├── FineCalculatorServiceTest.java
        ├── SearchAndRecommendationTest.java
        └── AnalyticsServiceTest.java
```

---

## ⚙️ Prerequisites & System Requirements

* **Java Development Kit (JDK)**: Java 21 or later.
* **Build Tool**: Apache Maven 3.8+ (or standalone `javac`).
* **Operating System**: Windows / Linux / macOS.

---

## 🛠 Build & Execution Instructions

### Method 1: Run with Maven (Recommended)

To compile, seed data, and launch the application CLI:

```bash
# 1. Compile the project
mvn clean compile

# 2. Run the Interactive CLI Application
mvn exec:java
```

### Method 2: Standalone Compilation & Direct Execution

If running without Maven installed directly via `javac` / `java`:

```bash
# Compile all source files into bin directory
mkdir bin
javac -d bin -sourcepath src/main/java src/main/java/com/library/Main.java

# Launch Main application
java -cp bin com.library.Main
```

### Method 3: Running Automated JUnit 5 Unit Tests

To execute the complete test suite:

```bash
mvn test
```

---

## 💻 Core Technical Implementations

### 1. OOP Inheritance & Polymorphism
All book types inherit from abstract `Book`, implementing specialized logic:
```java
public abstract class Book {
    private String isbn;
    private String title;
    private List<String> authors;
    private BookCategory category;
    private int totalCopies;
    private int availableCopies;
    
    public abstract String getBookType();
    public abstract String getSpecificDetails();
}
```

Member privileges inherit from abstract `Member`, defining polymorphic constraints:
```java
public class StudentMember extends Member {
    @Override public int getMaxBorrowingLimit() { return 5; }
    @Override public int getBorrowingPeriodDays() { return 14; }
    @Override public double getDailyFineRate() { return 0.50; }
    @Override public int getGracePeriodDays() { return 2; }
}
```

### 2. Java 21 Streams & Lambdas Code Examples

#### A. Search Books by Multiple Authors using `contains()`
```java
public List<Book> searchBooksByAuthors(List<String> authorQueries) {
    List<String> cleanQueries = authorQueries.stream()
            .map(String::toLowerCase)
            .map(String::trim)
            .toList();

    return bookRepository.findAll().stream()
            .filter(book -> book.getAuthors().stream()
                    .anyMatch(author -> cleanQueries.stream()
                            .anyMatch(query -> author.toLowerCase().contains(query))))
            .distinct()
            .collect(Collectors.toList());
}
```

#### B. Filter Available Books by Category and Publication Year Range
```java
public List<Book> filterAvailableBooks(BookCategory category, Integer startYear, Integer endYear) {
    return bookRepository.findAll().stream()
            .filter(Book::isAvailable)
            .filter(b -> category == null || b.getCategory() == category)
            .filter(b -> startYear == null || b.getPublicationYear() >= startYear)
            .filter(b -> endYear == null || b.getPublicationYear() <= endYear)
            .sorted(Comparator.comparing(Book::getTitle))
            .collect(Collectors.toList());
}
```

#### C. Find Members with Overdue Books
```java
public List<Member> findMembersWithOverdueBooks(LocalDate currentDate) {
    Set<String> overdueMemberIds = transactionRepository.findAllTransactions().stream()
            .filter(tx -> tx.getStatus() != TransactionStatus.RETURNED)
            .filter(tx -> tx.isOverdue(currentDate))
            .map(BorrowTransaction::getMemberId)
            .collect(Collectors.toSet());

    return memberRepository.findAll().stream()
            .filter(m -> overdueMemberIds.contains(m.getMemberId()))
            .collect(Collectors.toList());
}
```

#### D. Calculate Average Borrowing Duration by Category
```java
public Map<BookCategory, Double> calculateAverageBorrowingDurationByCategory() {
    return transactionRepository.findAllTransactions().stream()
            .filter(tx -> tx.getReturnDate() != null)
            .collect(Collectors.groupingBy(
                    tx -> bookRepository.findByIsbn(tx.getBookIsbn())
                            .map(Book::getCategory)
                            .orElse(BookCategory.FICTION),
                    Collectors.averagingDouble(tx -> ChronoUnit.DAYS.between(tx.getIssueDate(), tx.getReturnDate()))
            ));
}
```

#### E. Popularity Rankings using `groupingBy` and `counting`
```java
public Map<String, Long> generatePopularityRankings() {
    return transactionRepository.findAllTransactions().stream()
            .collect(Collectors.groupingBy(
                    BorrowTransaction::getBookIsbn,
                    Collectors.counting()
            ));
}
```

---

## 🖥 CLI Menu Navigation

The system presents the exact 6-category structure:

```
=== LIBRARY MANAGEMENT SYSTEM ===
1. Book Operations
   1.1 Add New Book
   1.2 Update Book Info
   1.3 Search Books
   1.4 View Book Details
   1.5 Check Availability
2. Member Operations
   2.1 Register Member
   2.2 Update Member Info
   2.3 Search Members
   2.4 View Member History
   2.5 Fine Management
3. Borrowing Operations
   3.1 Issue Book
   3.2 Return Book
   3.3 Renew Book
   3.4 Reserve Book
   3.5 View Overdue Books
4. Advanced Features
   4.1 Book Recommendations
   4.2 Advanced Search
   4.3 Popular Books Report
   4.4 Member Analytics
5. Reports & Analytics
   5.1 Borrowing Reports
   5.2 Fine Collection Reports
   5.3 Book Popularity Analysis
   5.4 Member Engagement Reports
6. System Operations
   6.1 Data Backup
   6.2 Data Import/Export
   6.3 System Configuration
0. Exit System
```

---

## 📁 Persistence & Data Storage

* Data is saved in human-readable JSON format under `data/` directory (`books.json`, `members.json`, `transactions.json`, `reservations.json`, `fines.json`).
* System Operations option `6.1` creates timestamped snapshot backups in `data/backups/`.

---

## 📹 Video Demo Script & Presentation Outline

A 5-minute video demonstration can be presented following this flow:
1. **0:00 - 0:45**: Introduction & project architecture overview.
2. **0:45 - 1:45**: Book & Member Management (adding Physical/E-Book, generating membership cards).
3. **1:45 - 2:45**: Borrowing, Returning, Renewal, and Fine Calculation validation.
4. **2:45 - 3:45**: Advanced Search & Recommendation Engine (Collaborative filtering).
5. **3:45 - 4:30**: Analytics & Stream operations (averaging duration, monthly trends).
6. **4:30 - 5:00**: Data Persistence, JSON export, and automated JUnit test run (`mvn test`).

---
*Developed with Java 21 - Ready for Final Submission.*
#   P r o j e c t   u p d a t e s  
 