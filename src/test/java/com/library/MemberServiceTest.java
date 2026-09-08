package com.library;

import com.library.model.member.*;
import com.library.repository.MemberRepository;
import com.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @Test
    void testRegisterMemberSuccess() {
        Member student = new StudentMember("STU-001", "John Doe", "john@edu.com", "555-1234",
                "Campus", LocalDate.now(), "CS", "CS-101");

        assertTrue(memberService.registerMember(student));
        assertTrue(memberService.getMemberById("STU-001").isPresent());
        assertEquals("Student", student.getMemberType());
        assertEquals(5, student.getMaxBorrowingLimit());
    }

    @Test
    void testRegisterMemberDuplicateIdOrEmail() {
        Member student1 = new StudentMember("STU-001", "John Doe", "john@edu.com", "555-1234",
                "Campus", LocalDate.now(), "CS", "CS-101");
        Member student2 = new StudentMember("STU-001", "Jane Doe", "jane@edu.com", "555-5678",
                "Campus", LocalDate.now(), "CS", "CS-102");
        Member student3 = new FacultyMember("FAC-001", "Dr. Smith", "john@edu.com", "555-9999",
                "Faculty", LocalDate.now(), "Math", "Professor");

        memberService.registerMember(student1);
        assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(student2));
        assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(student3));
    }

    @Test
    void testMembershipCardGeneration() {
        Member faculty = new FacultyMember("FAC-100", "Dr. Alan Turing", "turing@edu.com", "555-0000",
                "Bletchley", LocalDate.now(), "CS", "Professor");
        memberService.registerMember(faculty);

        String card = memberService.generateMembershipCard("FAC-100");
        assertNotNull(card);
        assertTrue(card.contains("Alan Turing"));
        assertTrue(card.contains("Faculty"));
    }
}
