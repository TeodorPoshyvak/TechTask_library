package com.techTask.library.utils;

import com.techTask.library.data.dto.BookMemberDTO;
import com.techTask.library.data.dto.MemberDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.request.member.MemberRequest;
import com.techTask.library.data.response.MemberResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MemberMapper {

    public Member toMember(MemberDTO memberDTO) {
        Member member = new Member();
        member.setId(member.getId());
        member.setName(memberDTO.getName());
        Set<Book> books = memberDTO.getBooks().stream()
                .map(bookMemberDTO -> {
                            Book book = new Book();
                            book.setId(bookMemberDTO.getID());
                            book.setTitle(bookMemberDTO.getTitle());
                            book.setAuthor(bookMemberDTO.getAuthor());
                            return book;
                        }
                ).collect(Collectors.toSet());
        member.setBooks(books);
        return member;
    }

    public Member toMember(MemberResponse memberResponse) {
        Member member = new Member();
        member.setId(memberResponse.getId());
        member.setName(memberResponse.getName());
        member.setBooks(memberResponse.getBooks().stream()
                .map(bookMemberDTO -> {
                    Book book = new Book();
                    book.setId(bookMemberDTO.getID());
                    book.setTitle(bookMemberDTO.getTitle());
                    book.setAuthor(bookMemberDTO.getAuthor());
                    return book;
                }).collect(Collectors.toSet()));
        return member;

    }

    public MemberDTO toMemberDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setName(member.getName());
        memberDTO.setMembership_date(member.getMembership_date());
        Set<BookMemberDTO> bookDTOs = member.getBooks().stream()
                .map(book -> {
                    BookMemberDTO bookDTO = new BookMemberDTO();
                    bookDTO.setID(book.getId());
                    bookDTO.setTitle(book.getTitle());
                    bookDTO.setAuthor(book.getAuthor());
                    return bookDTO;
                })
                .collect(Collectors.toSet());
        memberDTO.setBooks(bookDTOs);
        return memberDTO;
    }

    public MemberDTO toMemberDTO(MemberResponse memberResponse) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberResponse.getId());
        memberDTO.setName(memberResponse.getName());
        memberDTO.setMembership_date(memberResponse.getMembership_date());
        memberDTO.setBooks(memberResponse.getBooks());
        return memberDTO;
    }

    public MemberDTO toMemberDTO(MemberRequest memberRequest) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName(memberRequest.getName());
        return memberDTO;
    }

    public MemberResponse toMemberResponse(MemberDTO memberDTO) {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setId(memberDTO.getId());
        memberResponse.setName(memberDTO.getName());
        memberResponse.setMembership_date(memberDTO.getMembership_date());
        Set<BookMemberDTO> bookDTOs = memberDTO.getBooks().stream()
                .map(book -> {
                    BookMemberDTO bookDTO = new BookMemberDTO();
                    bookDTO.setID(book.getID());
                    bookDTO.setTitle(book.getTitle());
                    bookDTO.setAuthor(book.getAuthor());
                    return bookDTO;
                })
                .collect(Collectors.toSet());
        memberResponse.setBooks(bookDTOs);
        return memberResponse;
    }

    public MemberResponse toMemberResponse(Member member) {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setId(member.getId());
        memberResponse.setName(member.getName());
        memberResponse.setMembership_date(member.getMembership_date());
        memberResponse.setBooks(member.getBooks().stream()
                .map(book -> {
                    BookMemberDTO bookDTO = new BookMemberDTO();
                    bookDTO.setID(book.getId());
                    bookDTO.setTitle(book.getTitle());
                    bookDTO.setAuthor(book.getAuthor());
                    return bookDTO;
                }).collect(Collectors.toSet()));
        return memberResponse;
    }

    public List<MemberDTO> toMemberDTOs(Collection<Member> members) {
        return members.stream().map(this::toMemberDTO).collect(Collectors.toList());
    }

    public List<Member> toMembers(Collection<MemberDTO> memberDTOs) {
        return memberDTOs.stream().map(this::toMember).collect(Collectors.toList());
    }

    public List<MemberResponse> toMemberResponses(Collection<MemberDTO> memberDTOs) {
        return memberDTOs.stream().map(this::toMemberResponse).collect(Collectors.toList());
    }
}
