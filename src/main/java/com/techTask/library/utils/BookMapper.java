package com.techTask.library.utils;

import com.techTask.library.data.dto.BookDTO;
import com.techTask.library.data.dto.MemberDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.request.book.BookRequest;
import com.techTask.library.data.response.BookResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class BookMapper {

    public Book toBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setAmount(bookDTO.getAmount());
        book.setMembers(bookDTO.getMembers().stream().map(
                memberDTO -> {
                    Member member = new Member();
                    member.setId(memberDTO.getId());
                    member.setName(memberDTO.getName());
                    return member;
                }
        ).collect(Collectors.toSet()));
        return book;
    }

    public Book toBook(BookResponse bookResponse) {
        Book book = new Book();
        book.setId(bookResponse.getId());
        book.setTitle(bookResponse.getTitle());
        book.setAuthor(bookResponse.getAuthor());
        book.setAmount(bookResponse.getAmount());
        book.setMembers(bookResponse.getMembers());
        return book;
    }

    public BookDTO toBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setAmount(book.getAmount());
        Set<MemberDTO> memberDTOS = book.getMembers().stream().map(
                        member -> {
                            MemberDTO memberDTO = new MemberDTO();
                            memberDTO.setId(member.getId());
                            memberDTO.setName(member.getName());
                            return memberDTO;
                        })
                .collect(Collectors.toSet());
        return bookDTO;
    }

    public BookDTO toBookDTO(BookResponse bookResponse) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(bookResponse.getId());
        bookDTO.setTitle(bookResponse.getTitle());
        bookDTO.setAuthor(bookResponse.getAuthor());
        bookDTO.setAmount(bookResponse.getAmount());
        bookDTO.setMembers(bookResponse.getMembers().stream().map(
                member -> {
                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setId(member.getId());
                    memberDTO.setName(member.getName());
                    return memberDTO;
                }
        ).collect(Collectors.toSet()));
        return bookDTO;
    }

    public BookDTO toBookDTO(BookRequest bookRequest) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(bookRequest.getTitle());
        bookDTO.setAuthor(bookRequest.getAuthor());
        return bookDTO;
    }

    public BookResponse toBookResponse(BookDTO bookDTO) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(bookDTO.getId());
        bookResponse.setTitle(bookDTO.getTitle());
        bookResponse.setAuthor(bookDTO.getAuthor());
        bookResponse.setAmount(bookDTO.getAmount());
        bookResponse.setMembers(bookDTO.getMembers().stream().map(
                memberDTO -> {
                    Member member = new Member();
                    member.setId(memberDTO.getId());
                    member.setName(memberDTO.getName());
                    return member;
                }
        ).collect(Collectors.toSet()));
        return bookResponse;
    }

    public BookResponse toBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setAmount(book.getAmount());
        bookResponse.setMembers(book.getMembers());
        return bookResponse;
    }

    public List<BookDTO> toBookDTOs(Collection<Book> books) {
        return books.stream().map(this::toBookDTO).collect(Collectors.toList());
    }

    public List<Book> toBooks(Collection<BookDTO> bookDTOs) {
        return bookDTOs.stream().map(this::toBook).collect(Collectors.toList());
    }

    public List<BookResponse> toBookResponses(Collection<BookDTO> bookDTOs) {
        return bookDTOs.stream().map(this::toBookResponse).collect(Collectors.toList());
    }

}
