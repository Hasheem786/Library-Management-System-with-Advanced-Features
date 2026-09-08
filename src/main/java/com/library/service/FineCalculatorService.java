package com.library.service;

import com.library.model.member.Member;
import com.library.model.transaction.BorrowTransaction;

import java.time.LocalDate;

/**
 * Fine calculation service implementing customizable rate policies,
 * grace periods, maximum fine caps, and compound multipliers for extended overdue periods.
 */
public class FineCalculatorService {

    /**
     * Calculates the calculated fine amount for a given transaction and member type.
     *
     * Rules:
     * 1. Grace period: If overdue days <= grace period, fine is $0.0.
     * 2. Effective overdue days = overdue days - grace period.
     * 3. Base daily rate depends on Member type.
     * 4. Compound fine: For overdue days exceeding 14 days, a 1.5x multiplier applies to the extra days.
     * 5. Maximum fine limit: Fine is capped at member.getMaxFineLimit().
     */
    public double calculateFine(BorrowTransaction transaction, Member member, LocalDate currentDate) {
        if (transaction == null || member == null) {
            return 0.0;
        }

        long overdueDays = transaction.getOverdueDays(currentDate);
        int gracePeriod = member.getGracePeriodDays();

        if (overdueDays <= gracePeriod) {
            return 0.0;
        }

        long effectiveOverdueDays = overdueDays - gracePeriod;
        double dailyRate = member.getDailyFineRate();
        double totalFine = 0.0;

        if (effectiveOverdueDays <= 14) {
            totalFine = effectiveOverdueDays * dailyRate;
        } else {
            long standardDays = 14;
            long compoundDays = effectiveOverdueDays - 14;
            totalFine = (standardDays * dailyRate) + (compoundDays * dailyRate * 1.5);
        }

        // Cap fine at member's max fine limit
        return Math.min(totalFine, member.getMaxFineLimit());
    }
}
