package com.techTask.library.services;

import com.techTask.library.data.dto.MemberDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.repositories.MemberRepository;
import com.techTask.library.exception.DuplicateNameException;
import com.techTask.library.exception.MemberDeleteException;
import com.techTask.library.utils.MemberMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    private final static long ID = 1L;
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper memberMapper;

    @Test
    void getMembersTest() {
        List<Member> members = Arrays.asList(
                new Member("Stan"),
                new Member("Olga"));

        Mockito.when(memberRepository.findAll()).thenReturn(members);
        Mockito.when(memberMapper.toMemberDTOs(members)).thenReturn(
                Arrays.asList(
                        new MemberDTO("Stan"),
                        new MemberDTO("Olga"))
        );

        List<MemberDTO> memberDTOS = memberService.getMembers();
        assertNotNull(memberDTOS);
        assertEquals(2, memberDTOS.size());
        assertEquals("Stan", memberDTOS.get(0).getName());
        assertEquals("Olga", memberDTOS.get(1).getName());
    }

    @Test
    void getMemberByIdTest() {
        Member member = new Member("Stan");

        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.of(member));
        Mockito.when(memberMapper.toMemberDTO(member)).thenReturn(new MemberDTO(1, "Stan"));

        MemberDTO memberDTO = memberService.getMemberById(ID);

        assertNotNull(memberDTO);
        assertEquals("Stan", memberDTO.getName());
        assertEquals(ID, memberDTO.getId());
    }

    @Test
    void getMemberByIdExceptionTest() {
        final long memberID = 2;
        Member member = new Member("Stan");

        Mockito.when(memberRepository.findById(memberID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> memberService.getMemberById(memberID));

        assertEquals("Member with id " + memberID + " not found", exception.getMessage());
    }

    @Test
    void createMemberTest() {
        MemberDTO memberDTO = new MemberDTO(1, "Stan", LocalDateTime.now());
        Member expectedMember = new Member(1, "Stan", LocalDateTime.now());

        Mockito.when(memberMapper.toMember(memberDTO)).thenReturn(expectedMember);
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(expectedMember);
        Mockito.when(memberMapper.toMemberDTO(expectedMember)).thenReturn(memberDTO);

        MemberDTO createdMember = memberService.createMember(memberDTO);

        assertNotNull(createdMember);
        assertEquals("Stan", createdMember.getName());
        assertEquals(1, createdMember.getId());
    }

    @Test
    void createMemberExceptionTest() {
        Member member = new Member("Stan");
        Mockito.when(memberRepository.existsByName(member.getName())).thenReturn(true);
        DuplicateNameException exception = assertThrows(
                DuplicateNameException.class, () -> memberService.createMember(new MemberDTO(1, "Stan")));

        assertEquals("Member with name " + member.getName() + " already exists.", exception.getMessage());
    }

    @Test
    void updateMemberTest() {
        Member existingMember = new Member(ID, "Stan", LocalDateTime.now());
        Member updatedMember = new Member(ID, "Stan Li", LocalDateTime.now());
        MemberDTO memberDTO = new MemberDTO(ID, "Stan Li", LocalDateTime.now());

        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.of(existingMember));
        Mockito.when(memberRepository.existsByName(memberDTO.getName())).thenReturn(false);
        Mockito.when(memberMapper.toMember(memberDTO)).thenReturn(updatedMember);
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(updatedMember);
        Mockito.when(memberMapper.toMemberDTO(updatedMember)).thenReturn(memberDTO);

        MemberDTO result = memberService.updateMember(ID, memberDTO);

        assertNotNull(result);
        assertEquals("Stan Li", result.getName());
        assertEquals(ID, result.getId());
    }

    @Test
    void updateMemberDuplicateExceptionTest() {
        MemberDTO memberDTO = new MemberDTO(ID, "Stan Li", LocalDateTime.now());
        Member existingMember = new Member(ID, "Stan Li", LocalDateTime.now());
        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.of(existingMember));
        Mockito.when(memberRepository.existsByName(memberDTO.getName())).thenReturn(true);
        DuplicateNameException exception = assertThrows(
                DuplicateNameException.class, () -> memberService.updateMember(ID, memberDTO)
        );

        assertEquals("Member with name " + memberDTO.getName() + " already exists.", exception.getMessage());
    }

    @Test
    void updateMemberNotFoundExceptionTest() {
        MemberDTO memberDTO = new MemberDTO(ID, "Stan Li", LocalDateTime.now());
        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> memberService.updateMember(ID, memberDTO));
        assertEquals("Member not found with id: " + ID, exception.getMessage());

    }

    @Test
    void deleteMemberTest() {
        Member existingMember = new Member(ID, "Stan", LocalDateTime.now());

        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.of(existingMember));
        memberService.deleteMember(ID);

        Mockito.verify(memberRepository, Mockito.times(1)).deleteById(existingMember.getId());
    }

    @Test
    void deleteMemberNotFoundExceptionTest() {
        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> memberService.deleteMember(ID));

        assertEquals("Member not found", exception.getMessage());
    }

    @Test
    void deleteMemberExceptionTest() {
        Member member = new Member(ID, "Stan", LocalDateTime.now());
        member.setBooks(new HashSet<>(List.of(new Book())));

        Mockito.when(memberRepository.findById(ID)).thenReturn(Optional.of(member));
        MemberDeleteException exception = assertThrows(MemberDeleteException.class, () -> memberService.deleteMember(ID));

        assertEquals("Member cannot be deleted because they have borrowed books", exception.getMessage());
        Mockito.verify(memberRepository).findById(ID);
        Mockito.verify(memberRepository, Mockito.never()).deleteById(ID);
    }
}