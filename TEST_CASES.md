# Test Cases Documentation - Library Management System

> **Comprehensive Test Matrix & Verification Scenarios**

---

## 🧪 Test Matrix Summary

| Test ID | Module | Scenario / Feature Under Test | Test Input / Setup | Expected Outcome | Verification Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-B01** | Book Management | Add Physical Book with Valid ISBN | `ISBN: 978-0134685991`, Title: "Effective Java", Copies: 5 | Book created and stored in repository | **PASS** |
| **TC-B02** | Book Management | Duplicate ISBN Validation | Add duplicate book with `ISBN: 978-0134685991` | `IllegalArgumentException` thrown: ISBN already exists | **PASS** |
| **TC-B03** | Book Management | Update Stock Copies & Condition | Update ISBN `978-0134685991` copies to 10 and condition to `DAMAGED` | Total copies updated to 10, condition updated | **PASS** |
| **TC-M01** | Member Management | Register Student Member | ID: `STU-1001`, Limit: 5, Loan: 14 days, Daily fine: $0.50 | Member registered with active status | **PASS** |
| **TC-M02** | Member Management | Duplicate Member ID / Email Check | Register second member with ID `STU-1001` or duplicate email | `IllegalArgumentException` thrown: ID/email taken | **PASS** |
| **TC-M03** | Member Management | Membership Card Generation | Call `generateMembershipCard("STU-1001")` | ASCII formatted membership card returned | **PASS** |
| **TC-L01** | Borrowing System | Issue Book (Normal Flow) | Active member `STU-1001`, Available book `ISBN: 978-0134685991` | Book issued, due date set 14 days out, available copies decremented | **PASS** |
| **TC-L02** | Borrowing System | Issue Book Fails when Copies Zero | Issue book when available copies = 0 | `IllegalStateException`: No available copies left | **PASS** |
| **TC-L03** | Borrowing System | Borrowing Limit Enforcement | General member (limit 3) attempts 4th issue | `IllegalStateException`: Maximum limit reached | **PASS** |
| **TC-L04** | Borrowing System | Unpaid Fine Threshold Check | Member with $15.50 unpaid fine attempts issue | `IllegalStateException`: Outstanding unpaid fines exceed threshold | **PASS** |
| **TC-F01** | Fine Calculation | Grace Period Deduction | Overdue by 2 days for Student (Grace period = 2 days) | Fine amount calculated = **$0.00** | **PASS** |
| **TC-F02** | Fine Calculation | Standard Overdue Calculation | Student overdue by 7 days (5 effective days after grace) | Fine amount = 5 × $0.50 = **$2.50** | **PASS** |
| **TC-F03** | Fine Calculation | Compound Overdue Penalty | Overdue > 14 days (1.5x multiplier on extra days) | Compound multiplier applied to days > 14 | **PASS** |
| **TC-F04** | Fine Calculation | Maximum Fine Limit Cap | General Member (Max fine cap = $30.00) overdue by 100 days | Calculated fine capped at **$30.00** | **PASS** |
| **TC-S01** | Advanced Search | Multi-Author `contains()` Search | Query string: `"bloch"` | Returns "Effective Java" authored by Joshua Bloch | **PASS** |
| **TC-S02** | Advanced Search | Filter by Category & Year Range | Category: `TECHNOLOGY`, Year range: 2020-2024 | Returns matching available tech books published in range | **PASS** |
| **TC-R01** | Recommendation | Personal History Recommendation | Target member borrowed Technology books | Recommends unread Technology books sorted by borrow count | **PASS** |
| **TC-R02** | Recommendation | Collaborative Filtering | Target member & Peer member share common borrowed books | Recommends books borrowed by peer member | **PASS** |
| **TC-A01** | Analytics | Defaulters Report | Query members with overdue books or unpaid balance | Returns list of defaulting members sorted by fine balance | **PASS** |
| **TC-A02** | Analytics | Book Turnover Rate | 10 total borrows / 2 copies | Calculates turnover rate = **5.0** | **PASS** |
| **TC-P01** | Persistence | Export & Backup JSON State | Trigger system export / backup | `books.json`, `members.json`, `transactions.json`, `fines.json` written | **PASS** |

---

## 🛠 How to Run Automated JUnit 5 Unit Tests

Run all unit tests using Maven:
```bash
mvn test
```

Expected Output:
```
[INFO] Running com.library.BookServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.045 s
[INFO] Running com.library.MemberServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.021 s
[INFO] Running com.library.FineCalculatorServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s
[INFO] Running com.library.BorrowingServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.038 s
[INFO] Running com.library.SearchAndRecommendationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.024 s
[INFO] Running com.library.AnalyticsServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.019 s
[INFO] 
[INFO] -------------------------------------------------------
[INFO] T E S T S   P A S S E D
[INFO] -------------------------------------------------------
```
