package com.library.repository;

import com.library.model.transaction.BorrowTransaction;
import com.library.model.transaction.FineRecord;
import com.library.model.transaction.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Thread-safe repository managing transactions, reservations, and fine records.
 */
public class TransactionRepository {
    private final Map<String, BorrowTransaction> transactionMap = new ConcurrentHashMap<>();
    private final List<Reservation> reservationList = new CopyOnWriteArrayList<>();
    private final Map<String, FineRecord> fineRecordMap = new ConcurrentHashMap<>();

    // Transaction methods
    public boolean saveTransaction(BorrowTransaction tx) {
        if (tx == null || tx.getTransactionId() == null) return false;
        transactionMap.put(tx.getTransactionId(), tx);
        return true;
    }

    public Optional<BorrowTransaction> findTransactionById(String transactionId) {
        if (transactionId == null) return Optional.empty();
        return Optional.ofNullable(transactionMap.get(transactionId));
    }

    public List<BorrowTransaction> findAllTransactions() {
        return new ArrayList<>(transactionMap.values());
    }

    public List<BorrowTransaction> findTransactionsByMemberId(String memberId) {
        if (memberId == null) return List.of();
        return transactionMap.values().stream()
                .filter(tx -> tx.getMemberId().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    public List<BorrowTransaction> findTransactionsByBookIsbn(String isbn) {
        if (isbn == null) return List.of();
        return transactionMap.values().stream()
                .filter(tx -> tx.getBookIsbn().equalsIgnoreCase(isbn.trim()))
                .collect(Collectors.toList());
    }

    // Reservation methods
    public boolean saveReservation(Reservation reservation) {
        if (reservation == null) return false;
        reservationList.add(reservation);
        return true;
    }

    public List<Reservation> findAllReservations() {
        return new ArrayList<>(reservationList);
    }

    public List<Reservation> findReservationsByBookIsbn(String isbn) {
        if (isbn == null) return List.of();
        return reservationList.stream()
                .filter(r -> r.getBookIsbn().equalsIgnoreCase(isbn.trim()))
                .collect(Collectors.toList());
    }

    public List<Reservation> findReservationsByMemberId(String memberId) {
        if (memberId == null) return List.of();
        return reservationList.stream()
                .filter(r -> r.getMemberId().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    // FineRecord methods
    public boolean saveFineRecord(FineRecord fineRecord) {
        if (fineRecord == null || fineRecord.getFineId() == null) return false;
        fineRecordMap.put(fineRecord.getFineId(), fineRecord);
        return true;
    }

    public Optional<FineRecord> findFineRecordById(String fineId) {
        if (fineId == null) return Optional.empty();
        return Optional.ofNullable(fineRecordMap.get(fineId));
    }

    public List<FineRecord> findAllFineRecords() {
        return new ArrayList<>(fineRecordMap.values());
    }

    public List<FineRecord> findFineRecordsByMemberId(String memberId) {
        if (memberId == null) return List.of();
        return fineRecordMap.values().stream()
                .filter(f -> f.getMemberId().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    public void clearAll() {
        transactionMap.clear();
        reservationList.clear();
        fineRecordMap.clear();
    }
}
