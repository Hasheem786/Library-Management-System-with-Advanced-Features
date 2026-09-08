package com.library.model.member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a library member.
 * Demonstrates inheritance, encapsulation, and customized member privileges.
 */
public abstract class Member {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate membershipDate;
    private MembershipStatus status;
    private double unpaidFinesBalance;
    private List<String> currentBorrowedIsbns;
    private List<String> borrowingHistoryIsbns;

    public Member(String memberId, String name, String email, String phone, String address, LocalDate membershipDate) {
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null").trim();
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
        this.email = (email != null) ? email.trim() : "";
        this.phone = (phone != null) ? phone.trim() : "";
        this.address = (address != null) ? address.trim() : "";
        this.membershipDate = (membershipDate != null) ? membershipDate : LocalDate.now();
        this.status = MembershipStatus.ACTIVE;
        this.unpaidFinesBalance = 0.0;
        this.currentBorrowedIsbns = new ArrayList<>();
        this.borrowingHistoryIsbns = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public double getUnpaidFinesBalance() {
        return unpaidFinesBalance;
    }

    public synchronized void addUnpaidFine(double amount) {
        if (amount > 0) {
            this.unpaidFinesBalance += amount;
        }
    }

    public synchronized boolean payFine(double amount) {
        if (amount > 0 && amount <= unpaidFinesBalance) {
            this.unpaidFinesBalance -= amount;
            return true;
        } else if (amount > unpaidFinesBalance) {
            this.unpaidFinesBalance = 0.0;
            return true;
        }
        return false;
    }

    public List<String> getCurrentBorrowedIsbns() {
        return Collections.unmodifiableList(currentBorrowedIsbns);
    }

    public List<String> getBorrowingHistoryIsbns() {
        return Collections.unmodifiableList(borrowingHistoryIsbns);
    }

    public synchronized boolean addBorrowedBook(String isbn) {
        if (currentBorrowedIsbns.size() < getMaxBorrowingLimit()) {
            currentBorrowedIsbns.add(isbn);
            borrowingHistoryIsbns.add(isbn);
            return true;
        }
        return false;
    }

    public synchronized boolean removeBorrowedBook(String isbn) {
        return currentBorrowedIsbns.remove(isbn);
    }

    public boolean isEligibleToBorrow() {
        return status == MembershipStatus.ACTIVE && currentBorrowedIsbns.size() < getMaxBorrowingLimit() && unpaidFinesBalance < 15.0;
    }

    // Abstract methods defining specific member privileges
    public abstract String getMemberType();
    public abstract int getMaxBorrowingLimit();
    public abstract int getBorrowingPeriodDays();
    public abstract double getDailyFineRate();
    public abstract int getGracePeriodDays();
    public abstract double getMaxFineLimit();
    public abstract int getMaxRenewalLimit();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Email: %s | Status: %s | Borrowed: %d/%d | Unpaid Fines: $%.2f",
                getMemberType(), memberId, name, email, status.getLabel(), currentBorrowedIsbns.size(), getMaxBorrowingLimit(), unpaidFinesBalance);
    }
}
