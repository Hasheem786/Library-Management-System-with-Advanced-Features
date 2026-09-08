package com.library.model.member;

import java.time.LocalDate;

/**
 * Concrete class representing a Faculty Member.
 * Privilege profile: Limit=10 books, Period=30 days, DailyFine=$0.25, Grace=5 days, MaxFine=$50.0, MaxRenewals=3.
 */
public class FacultyMember extends Member {
    private String department;
    private String designation;

    public FacultyMember(String memberId, String name, String email, String phone, String address,
                         LocalDate membershipDate, String department, String designation) {
        super(memberId, name, email, phone, address, membershipDate);
        this.department = (department != null) ? department : "Academic Faculty";
        this.designation = (designation != null) ? designation : "Professor";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getMemberType() {
        return "Faculty";
    }

    @Override
    public int getMaxBorrowingLimit() {
        return 10;
    }

    @Override
    public int getBorrowingPeriodDays() {
        return 30;
    }

    @Override
    public double getDailyFineRate() {
        return 0.25;
    }

    @Override
    public int getGracePeriodDays() {
        return 5;
    }

    @Override
    public double getMaxFineLimit() {
        return 50.00;
    }

    @Override
    public int getMaxRenewalLimit() {
        return 3;
    }
}
