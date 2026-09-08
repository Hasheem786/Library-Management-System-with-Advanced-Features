# Architecture & Digital UML Class Diagrams

> **Library Management System with Advanced Features**  
> High-level architectural layout, design patterns, and UML diagrams.

---

## 🏛 System Architecture Layers

The application is structured into 5 clean, decoupled layers adhering to SOLID design principles:

```
+-------------------------------------------------------------------+
|                        Console UI Layer                           |
|                       (ConsoleUI.java)                            |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                        Service Layer                              |
| (BookService, MemberService, BorrowingService, SearchService,     |
|  RecommendationService, AnalyticsService, DataPersistenceService) |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                      Repository Layer                             |
| (BookRepository, MemberRepository, TransactionRepository)         |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                        Domain Models                              |
| (Book [Physical/EBook/AudioBook], Member [Student/Faculty/Gen],   |
|  BorrowTransaction, Reservation, FineRecord, Enums)              |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                     Persistence Layer                             |
|                 (JSON files in data/ directory)                   |
+-------------------------------------------------------------------+
```

---

## 📐 Digital UML Class Diagrams (Mermaid)

### 1. Book Inheritance Hierarchy

```mermaid
classDiagram
    class Book {
        <<abstract>>
        -String isbn
        -String title
        -List~String~ authors
        -String publisher
        -int publicationYear
        -int pages
        -BookCategory category
        -int totalCopies
        -int availableCopies
        -int reservedCopies
        -int borrowCount
        +getBookType()* String
        +getSpecificDetails()* String
        +decrementAvailableCopies() boolean
        +incrementAvailableCopies() void
    }

    class PhysicalBook {
        -String shelfLocation
        -double weightKg
        -BookCondition condition
        +getBookType() String
        +getSpecificDetails() String
    }

    class EBook {
        -double fileSizeMB
        -String format
        -String downloadUrl
        +getBookType() String
        +getSpecificDetails() String
    }

    class AudioBook {
        -int durationMinutes
        -String narrator
        -String audioFormat
        +getBookType() String
        +getSpecificDetails() String
    }

    class BookCategory {
        <<enumeration>>
        FICTION
        NON_FICTION
        SCIENCE
        TECHNOLOGY
        HISTORY
        BIOGRAPHY
        FANTASY
        MYSTERY
    }

    class BookCondition {
        <<enumeration>>
        NEW
        GOOD
        FAIR
        POOR
        DAMAGED
    }

    Book <|-- PhysicalBook
    Book <|-- EBook
    Book <|-- AudioBook
    Book --> BookCategory
    PhysicalBook --> BookCondition
```

### 2. Member Inheritance & Privileges Hierarchy

```mermaid
classDiagram
    class Member {
        <<abstract>>
        -String memberId
        -String name
        -String email
        -String phone
        -String address
        -LocalDate membershipDate
        -MembershipStatus status
        -double unpaidFinesBalance
        -List~String~ currentBorrowedIsbns
        -List~String~ borrowingHistoryIsbns
        +getMemberType()* String
        +getMaxBorrowingLimit()* int
        +getBorrowingPeriodDays()* int
        +getDailyFineRate()* double
        +getGracePeriodDays()* int
        +getMaxFineLimit()* double
        +getMaxRenewalLimit()* int
        +addUnpaidFine(amount) void
        +payFine(amount) boolean
    }

    class StudentMember {
        -String department
        -String studentIdNumber
        +getMaxBorrowingLimit() int (5)
        +getBorrowingPeriodDays() int (14)
        +getDailyFineRate() double ($0.50)
        +getGracePeriodDays() int (2)
    }

    class FacultyMember {
        -String department
        -String designation
        +getMaxBorrowingLimit() int (10)
        +getBorrowingPeriodDays() int (30)
        +getDailyFineRate() double ($0.25)
        +getGracePeriodDays() int (5)
    }

    class GeneralMember {
        -String occupation
        +getMaxBorrowingLimit() int (3)
        +getBorrowingPeriodDays() int (14)
        +getDailyFineRate() double ($1.00)
        +getGracePeriodDays() int (1)
    }

    class MembershipStatus {
        <<enumeration>>
        ACTIVE
        SUSPENDED
        EXPIRED
    }

    Member <|-- StudentMember
    Member <|-- FacultyMember
    Member <|-- GeneralMember
    Member --> MembershipStatus
```

### 3. Transaction & Fine Relationships

```mermaid
classDiagram
    class BorrowTransaction {
        -String transactionId
        -String memberId
        -String bookIsbn
        -LocalDate issueDate
        -LocalDate dueDate
        -LocalDate returnDate
        -TransactionStatus status
        -int renewalCount
        -double fineAmount
        -boolean finePaid
        +isOverdue(currentDate) boolean
        +getOverdueDays(currentDate) long
    }

    class Reservation {
        -String reservationId
        -String memberId
        -String bookIsbn
        -LocalDate reservationDate
        -ReservationStatus status
    }

    class FineRecord {
        -String fineId
        -String memberId
        -String transactionId
        -double amount
        -String reason
        -LocalDate dateAssessed
        -boolean paid
        +markAsPaid(paymentDate) void
    }

    BorrowTransaction "1" -- "0..1" FineRecord : triggers
```

---

## 🎨 Applied Design Patterns

1. **Strategy / Template Method Pattern**: Member privilege profiles (`StudentMember`, `FacultyMember`, `GeneralMember`) encapsulate customized policies for borrowing limits, fine rates, and grace periods.
2. **Repository Pattern**: `BookRepository`, `MemberRepository`, and `TransactionRepository` abstract in-memory data storage and retrieval.
3. **Service Layer Pattern**: Business logic decoupled from presentation layer (`BookService`, `MemberService`, `BorrowingService`, `SearchService`, `RecommendationService`, `AnalyticsService`).
4. **Collaborative Filtering Recommendation Pattern**: Recommends items using Jaccard Similarity index over member borrowing histories.
