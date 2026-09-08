package com.library.service;

import com.library.model.member.Member;
import com.library.model.member.MembershipStatus;
import com.library.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing Member registration, profiles, fine balances, and membership card generation.
 */
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Registers a new member with ID and email uniqueness validation.
     */
    public boolean registerMember(Member member) {
        if (member == null || member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID is required.");
        }

        if (memberRepository.existsById(member.getMemberId())) {
            throw new IllegalArgumentException("Member with ID " + member.getMemberId() + " already registered.");
        }

        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            Optional<Member> existingByEmail = memberRepository.findByEmail(member.getEmail());
            if (existingByEmail.isPresent()) {
                throw new IllegalArgumentException("Member with email " + member.getEmail() + " already exists.");
            }
        }

        return memberRepository.save(member);
    }

    /**
     * Updates member information.
     */
    public boolean updateMember(Member updatedMember) {
        if (updatedMember == null || !memberRepository.existsById(updatedMember.getMemberId())) {
            return false;
        }
        return memberRepository.save(updatedMember);
    }

    /**
     * Finds member by ID.
     */
    public Optional<Member> getMemberById(String memberId) {
        return memberRepository.findById(memberId);
    }

    /**
     * Searches members by name or email.
     */
    public List<Member> searchMembers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return memberRepository.findAll();
        }
        String cleanQuery = query.toLowerCase().trim();
        return memberRepository.findAll().stream()
                .filter(m -> m.getName().toLowerCase().contains(cleanQuery) ||
                             m.getEmail().toLowerCase().contains(cleanQuery) ||
                             m.getMemberId().toLowerCase().contains(cleanQuery))
                .collect(Collectors.toList());
    }

    /**
     * Updates member status.
     */
    public boolean updateMemberStatus(String memberId, MembershipStatus status) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setStatus(status);
            return memberRepository.save(member);
        }
        return false;
    }

    /**
     * Processes fine payment for a member.
     */
    public boolean processFinePayment(String memberId, double amount) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            boolean success = member.payFine(amount);
            if (success) {
                memberRepository.save(member);
            }
            return success;
        }
        return false;
    }

    /**
     * Returns all registered members.
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Generates digital membership card layout string.
     */
    public String generateMembershipCard(String memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return "Member ID not found.";
        }
        Member m = memberOpt.get();

        return String.format("""
            +-------------------------------------------------------------+
            |                  LIBRARY MEMBERSHIP CARD                    |
            +-------------------------------------------------------------+
            |  Member ID  : %-42s |
            |  Name       : %-42s |
            |  Type       : %-42s |
            |  Email      : %-42s |
            |  Phone      : %-42s |
            |  Status     : %-42s |
            |  Max Limit  : %-42s |
            |  Issued On  : %-42s |
            +-------------------------------------------------------------+
            """,
                m.getMemberId(),
                m.getName(),
                m.getMemberType(),
                m.getEmail(),
                m.getPhone(),
                m.getStatus().getLabel(),
                m.getMaxBorrowingLimit() + " Books",
                m.getMembershipDate());
    }
}
