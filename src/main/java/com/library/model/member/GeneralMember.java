package com.library.model.member;

import java.time.LocalDate;

/**
 * Concrete class representing a General Public Member.
 * Privilege profile: Limit=3 books, Period=14 days, DailyFine=$1.00, Grace=1 day, MaxFine=$30.0, MaxRenewals=1.
 */
public class GeneralMember extends Member {
    private String occupation;

    public GeneralMember(String memberId, String name, String email, String phone, String address,
                          LocalDate membershipDate, String occupation) {
        super(memberId, name, email, phone, address, membershipDate);
        this.occupation = (occupation != null) ? occupation : "General Public";
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public String getMemberType() {
        return "General Public";
    }

    @Override
    public int getMaxBorrowingLimit() {
        return 3;
    }

    @Override
    public int getBorrowingPeriodDays() {
        return 14;
    }

    @Override
    public double getDailyFineRate() {
        return 1.00;
    }

    @Override
    public int getGracePeriodDays() {
        return 1;
    }

    @Override
    public double getMaxFineLimit() {
        return 30.00;
    }

    @Override
    public int getMaxRenewalLimit() {
        return 1;
    }
}
