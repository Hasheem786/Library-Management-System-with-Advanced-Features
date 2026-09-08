package com.library.service;

import com.library.model.book.*;
import com.library.model.member.*;
import com.library.model.transaction.*;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service managing Data Import/Export, JSON file persistence, and snapshot backups.
 */
public class DataPersistenceService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    private static final String DATA_DIR = "data";

    public DataPersistenceService(BookRepository bookRepository,
                                  MemberRepository memberRepository,
                                  TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(DATA_DIR, "backups"));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    /**
     * Exports current system state to JSON files in data/ directory.
     */
    public boolean exportAllData() {
        try {
            saveBooksToFile(Paths.get(DATA_DIR, "books.json"));
            saveMembersToFile(Paths.get(DATA_DIR, "members.json"));
            saveTransactionsToFile(Paths.get(DATA_DIR, "transactions.json"));
            saveReservationsToFile(Paths.get(DATA_DIR, "reservations.json"));
            saveFinesToFile(Paths.get(DATA_DIR, "fines.json"));
            return true;
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a timestamped backup snapshot in data/backups/
     */
    public String createBackupSnapshot() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "_" + System.currentTimeMillis();
        Path backupDir = Paths.get(DATA_DIR, "backups", "backup_" + timestamp);
        try {
            Files.createDirectories(backupDir);
            saveBooksToFile(backupDir.resolve("books.json"));
            saveMembersToFile(backupDir.resolve("members.json"));
            saveTransactionsToFile(backupDir.resolve("transactions.json"));
            saveReservationsToFile(backupDir.resolve("reservations.json"));
            saveFinesToFile(backupDir.resolve("fines.json"));
            return backupDir.toString();
        } catch (Exception e) {
            System.err.println("Backup failed: " + e.getMessage());
            return null;
        }
    }

    private void saveBooksToFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        List<Book> books = bookRepository.findAll();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            sb.append("  {\n");
            sb.append(String.format("    \"type\": \"%s\",\n", b.getBookType()));
            sb.append(String.format("    \"isbn\": \"%s\",\n", b.getIsbn()));
            sb.append(String.format("    \"title\": \"%s\",\n", escapeJson(b.getTitle())));
            sb.append(String.format("    \"authors\": %s,\n", toJsonArray(b.getAuthors())));
            sb.append(String.format("    \"publisher\": \"%s\",\n", escapeJson(b.getPublisher())));
            sb.append(String.format("    \"publicationYear\": %d,\n", b.getPublicationYear()));
            sb.append(String.format("    \"pages\": %d,\n", b.getPages()));
            sb.append(String.format("    \"category\": \"%s\",\n", b.getCategory().name()));
            sb.append(String.format("    \"totalCopies\": %d,\n", b.getTotalCopies()));
            sb.append(String.format("    \"availableCopies\": %d,\n", b.getAvailableCopies()));
            sb.append(String.format("    \"reservedCopies\": %d,\n", b.getReservedCopies()));
            sb.append(String.format("    \"borrowCount\": %d", b.getBorrowCount()));

            if (b instanceof PhysicalBook pb) {
                sb.append(",\n");
                sb.append(String.format("    \"shelfLocation\": \"%s\",\n", pb.getShelfLocation()));
                sb.append(String.format("    \"weightKg\": %.2f,\n", pb.getWeightKg()));
                sb.append(String.format("    \"condition\": \"%s\"\n", pb.getCondition().name()));
            } else if (b instanceof EBook eb) {
                sb.append(",\n");
                sb.append(String.format("    \"fileSizeMB\": %.2f,\n", eb.getFileSizeMB()));
                sb.append(String.format("    \"format\": \"%s\",\n", eb.getFormat()));
                sb.append(String.format("    \"downloadUrl\": \"%s\"\n", eb.getDownloadUrl()));
            } else if (b instanceof AudioBook ab) {
                sb.append(",\n");
                sb.append(String.format("    \"durationMinutes\": %d,\n", ab.getDurationMinutes()));
                sb.append(String.format("    \"narrator\": \"%s\",\n", ab.getNarrator()));
                sb.append(String.format("    \"audioFormat\": \"%s\"\n", ab.getAudioFormat()));
            } else {
                sb.append("\n");
            }
            sb.append("  }").append(i < books.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]");
        Files.writeString(filePath, sb.toString());
    }

    private void saveMembersToFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        List<Member> members = memberRepository.findAll();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            sb.append("  {\n");
            sb.append(String.format("    \"type\": \"%s\",\n", m.getMemberType()));
            sb.append(String.format("    \"memberId\": \"%s\",\n", m.getMemberId()));
            sb.append(String.format("    \"name\": \"%s\",\n", escapeJson(m.getName())));
            sb.append(String.format("    \"email\": \"%s\",\n", m.getEmail()));
            sb.append(String.format("    \"phone\": \"%s\",\n", m.getPhone()));
            sb.append(String.format("    \"address\": \"%s\",\n", escapeJson(m.getAddress())));
            sb.append(String.format("    \"membershipDate\": \"%s\",\n", m.getMembershipDate()));
            sb.append(String.format("    \"status\": \"%s\",\n", m.getStatus().name()));
            sb.append(String.format("    \"unpaidFinesBalance\": %.2f,\n", m.getUnpaidFinesBalance()));
            sb.append(String.format("    \"currentBorrowedIsbns\": %s,\n", toJsonArray(m.getCurrentBorrowedIsbns())));
            sb.append(String.format("    \"borrowingHistoryIsbns\": %s", toJsonArray(m.getBorrowingHistoryIsbns())));

            if (m instanceof StudentMember sm) {
                sb.append(",\n");
                sb.append(String.format("    \"department\": \"%s\",\n", sm.getDepartment()));
                sb.append(String.format("    \"studentIdNumber\": \"%s\"\n", sm.getStudentIdNumber()));
            } else if (m instanceof FacultyMember fm) {
                sb.append(",\n");
                sb.append(String.format("    \"department\": \"%s\",\n", fm.getDepartment()));
                sb.append(String.format("    \"designation\": \"%s\"\n", fm.getDesignation()));
            } else if (m instanceof GeneralMember gm) {
                sb.append(",\n");
                sb.append(String.format("    \"occupation\": \"%s\"\n", gm.getOccupation()));
            } else {
                sb.append("\n");
            }
            sb.append("  }").append(i < members.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]");
        Files.writeString(filePath, sb.toString());
    }

    private void saveTransactionsToFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        List<BorrowTransaction> txs = transactionRepository.findAllTransactions();
        for (int i = 0; i < txs.size(); i++) {
            BorrowTransaction tx = txs.get(i);
            sb.append("  {\n");
            sb.append(String.format("    \"transactionId\": \"%s\",\n", tx.getTransactionId()));
            sb.append(String.format("    \"memberId\": \"%s\",\n", tx.getMemberId()));
            sb.append(String.format("    \"bookIsbn\": \"%s\",\n", tx.getBookIsbn()));
            sb.append(String.format("    \"issueDate\": \"%s\",\n", tx.getIssueDate()));
            sb.append(String.format("    \"dueDate\": \"%s\",\n", tx.getDueDate()));
            sb.append(String.format("    \"returnDate\": %s,\n", tx.getReturnDate() != null ? "\"" + tx.getReturnDate() + "\"" : "null"));
            sb.append(String.format("    \"status\": \"%s\",\n", tx.getStatus().name()));
            sb.append(String.format("    \"renewalCount\": %d,\n", tx.getRenewalCount()));
            sb.append(String.format("    \"fineAmount\": %.2f,\n", tx.getFineAmount()));
            sb.append(String.format("    \"finePaid\": %b\n", tx.isFinePaid()));
            sb.append("  }").append(i < txs.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]");
        Files.writeString(filePath, sb.toString());
    }

    private void saveReservationsToFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        List<Reservation> reservations = transactionRepository.findAllReservations();
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            sb.append("  {\n");
            sb.append(String.format("    \"reservationId\": \"%s\",\n", r.getReservationId()));
            sb.append(String.format("    \"memberId\": \"%s\",\n", r.getMemberId()));
            sb.append(String.format("    \"bookIsbn\": \"%s\",\n", r.getBookIsbn()));
            sb.append(String.format("    \"reservationDate\": \"%s\",\n", r.getReservationDate()));
            sb.append(String.format("    \"status\": \"%s\"\n", r.getStatus().name()));
            sb.append("  }").append(i < reservations.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]");
        Files.writeString(filePath, sb.toString());
    }

    private void saveFinesToFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        List<FineRecord> fines = transactionRepository.findAllFineRecords();
        for (int i = 0; i < fines.size(); i++) {
            FineRecord f = fines.get(i);
            sb.append("  {\n");
            sb.append(String.format("    \"fineId\": \"%s\",\n", f.getFineId()));
            sb.append(String.format("    \"memberId\": \"%s\",\n", f.getMemberId()));
            sb.append(String.format("    \"transactionId\": \"%s\",\n", f.getTransactionId()));
            sb.append(String.format("    \"amount\": %.2f,\n", f.getAmount()));
            sb.append(String.format("    \"reason\": \"%s\",\n", escapeJson(f.getReason())));
            sb.append(String.format("    \"dateAssessed\": \"%s\",\n", f.getDateAssessed()));
            sb.append(String.format("    \"paid\": %b,\n", f.isPaid()));
            sb.append(String.format("    \"datePaid\": %s\n", f.getDatePaid() != null ? "\"" + f.getDatePaid() + "\"" : "null"));
            sb.append("  }").append(i < fines.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]");
        Files.writeString(filePath, sb.toString());
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
