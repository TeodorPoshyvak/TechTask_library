package com.techTask.library.services;

import com.techTask.library.data.dto.BookMemberDTO;
import com.techTask.library.data.dto.MemberDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.repositories.BookRepository;
import com.techTask.library.data.repositories.MemberRepository;
import com.techTask.library.exception.BookBorrowEmptyException;
import com.techTask.library.exception.MemberBorrowException;
import com.techTask.library.exception.MemberReturnEmptyException;
import com.techTask.library.services.interfaces.BorrowService;
import com.techTask.library.utils.MemberMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowService {
    @Value("${member.borrow.limit:10}")
    private int borrowLimit;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Autowired
    public BorrowServiceImpl(BookRepository bookRepository, MemberRepository memberRepository, MemberMapper memberMapper) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public MemberDTO borrowBook(Long bookId, long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));

        if (member.getBooks().size() == borrowLimit) {
            throw new MemberBorrowException("Cannot borrow more than " + borrowLimit + " books.");
        }

        if (book.getAmount() == 0) {
            throw new BookBorrowEmptyException();
        }
        //fix.........
        if (!member.getBooks().contains(book)) {
            bookRepository.addBookToMember(bookId, memberId);
            member.getBooks().add(book);
        }
        member.getBooks().add(book);
        book.setAmount(book.getAmount() - 1);
        memberRepository.save(member);
        bookRepository.save(book);
        return memberMapper.toMemberDTO(member);
    }


    @Override
    @Transactional
    public MemberDTO returnBook(Long bookId, long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotFoundException("Member not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + bookId + " not found"));

        if (!member.getBooks().contains(book)) {
            throw new MemberReturnEmptyException("This book is not borrowed by the member");
        }

        Integer amount = Math.toIntExact(member.getBooks().stream().filter(a -> a.equals(book)).count());
        book.setAmount(book.getAmount() + amount);
        member.getBooks().remove(book);
        bookRepository.save(book);
        memberRepository.save(member);
        bookRepository.deleteBookFromMember(bookId, memberId);
        return memberMapper.toMemberDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookMemberDTO> getBooksBorrowedByMemberName(String name) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with name: " + name));
        return member.getBooks().stream()
                .map(book -> new BookMemberDTO(book.getId(), book.getTitle(), book.getAuthor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllBorrowedDistinctBook() {
        return bookRepository.findAll().stream()
                .filter(book -> !book.getMembers().isEmpty())
                .map(Book::getTitle)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getAllBorrowDistinctBookNamesWithCount() {
        return bookRepository.findAll().stream()
                .filter(book -> !book.getMembers().isEmpty())
                .collect(Collectors.groupingBy(Book::getTitle, Collectors.counting()));
    }

}
