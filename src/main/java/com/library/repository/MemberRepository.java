package com.library.repository;

import com.library.model.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe repository managing in-memory Member data storage.
 */
public class MemberRepository {
    private final Map<String, Member> memberMap = new ConcurrentHashMap<>();

    public boolean save(Member member) {
        if (member == null || member.getMemberId() == null) return false;
        memberMap.put(member.getMemberId().toUpperCase(), member);
        return true;
    }

    public Optional<Member> findById(String memberId) {
        if (memberId == null) return Optional.empty();
        return Optional.ofNullable(memberMap.get(memberId.toUpperCase().trim()));
    }

    public Optional<Member> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return memberMap.values().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(memberMap.values());
    }

    public boolean deleteById(String memberId) {
        if (memberId == null) return false;
        return memberMap.remove(memberId.toUpperCase().trim()) != null;
    }

    public boolean existsById(String memberId) {
        if (memberId == null) return false;
        return memberMap.containsKey(memberId.toUpperCase().trim());
    }

    public void clear() {
        memberMap.clear();
    }

    public int count() {
        return memberMap.size();
    }
}
