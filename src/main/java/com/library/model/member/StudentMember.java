package com.library.model.member;

import java.time.LocalDate;

/**
 * Concrete class representing a Student Member.
 * Privilege profile: Limit=5 books, Period=14 days, DailyFine=$0.50, Grace=2 days, MaxFine=$20.0, MaxRenewals=2.
 */
public class StudentMember extends Member {
    private String department;
    private String studentIdNumber;

    public StudentMember(String memberId, String name, String email, String phone, String address,
                         LocalDate membershipDate, String department, String studentIdNumber) {
        super(memberId, name, email, phone, address, membershipDate);
        this.department = (department != null) ? department : "General Studies";
        this.studentIdNumber = (studentIdNumber != null) ? studentIdNumber : memberId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentIdNumber() {
        return studentIdNumber;
    }

    public void setStudentIdNumber(String studentIdNumber) {
        this.studentIdNumber = studentIdNumber;
    }

    @Override
    public String getMemberType() {
        return "Student";
    }

    @Override
    public int getMaxBorrowingLimit() {
        return 5;
    }

    @Override
    public int getBorrowingPeriodDays() {
        return 14;
    }

    @Override
    public double getDailyFineRate() {
        return 0.50;
    }

    @Override
    public int getGracePeriodDays() {
        return 2;
    }

    @Override
    public double getMaxFineLimit() {
        return 20.00;
    }

    @Override
    public int getMaxRenewalLimit() {
        return 2;
    }
}
