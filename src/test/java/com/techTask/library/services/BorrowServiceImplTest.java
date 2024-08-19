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
import com.techTask.library.utils.MemberMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
class BorrowServiceImplTest {
    private final Long memberID = 1L;
    private final Long bookID = 1L;

    @InjectMocks
    private BorrowServiceImpl borrowService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper memberMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(borrowService, "borrowLimit", 10);
    }


    @Test
    void borrowBookTest() {
        Member member = new Member();
        member.setId(memberID);
        member.setBooks(new HashSet<>());

        Book book = new Book();
        book.setId(bookID);
        book.setAmount(1);
        MemberDTO memberDTO = new MemberDTO();
        when(memberMapper.toMemberDTO(member)).thenReturn(memberDTO);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(book));

        MemberDTO result = borrowService.borrowBook(bookID, bookID);

        assertNotNull(result);
        assertTrue(member.getBooks().contains(book));
        assertEquals(0, book.getAmount());
        verify(memberRepository).save(member);
        verify(bookRepository).save(book);
    }

    @Test
    public void testBorrowBook_BorrowLimitExceeded() {
        Member member = new Member();
        member.setId(memberID);

        Set<Book> borrowedBooks = new HashSet<>();
        for (long i = 1; i <= 10; i++) {
            borrowedBooks.add(new Book(i, "Book" + i, "Author" + i, 1));
        }
        member.setBooks(borrowedBooks);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        Book bookToBorrow = new Book(11L, "Sony3", "Ynos Son3", 1);
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(bookToBorrow));

        MemberBorrowException exception = assertThrows(MemberBorrowException.class, () ->
                borrowService.borrowBook(bookID, memberID));
        assertEquals("Cannot borrow more than " + 10 + " books.", exception.getMessage());
    }

    @Test
    public void testBorrowBook_BookNotFound() {
        Member member = new Member();
        member.setId(memberID);
        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        when(bookRepository.findById(memberID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> borrowService.borrowBook(bookID, bookID));
        assertEquals("Book with id " + 1 + " not found", exception.getMessage());
    }


    @Test
    public void testBorrowBook_MemberNotFound() {
        when(memberRepository.findById(memberID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> borrowService.borrowBook(bookID, bookID));
        assertEquals("Member not found", exception.getMessage());
    }

    @Test
    public void testBorrowBook_BookNotAvailable() {
        Member member = new Member();
        member.setId(memberID);
        Set<Book> borrowedBooks = new HashSet<>();
        borrowedBooks.add(new Book(1L, "Existing Book", "Existing Author", 1));
        member.setBooks(borrowedBooks);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));

        Book bookToBorrow = new Book(11L, "Sony3", "Ynos Son3", 0);
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(bookToBorrow));

        BookBorrowEmptyException exception = assertThrows(BookBorrowEmptyException.class, () -> borrowService.borrowBook(bookID, memberID));
        assertEquals("Book is empty", exception.getMessage());
    }


    @Test
    void returnBookTest() {
        Member member = new Member();
        member.setId(memberID);
        Book book = new Book();
        book.setId(bookID);
        book.setAmount(1);

        Set<Book> borrowedBooks = new HashSet<>();
        borrowedBooks.add(book);
        member.setBooks(borrowedBooks);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(book));

        MemberDTO memberDTO = new MemberDTO();
        when(memberMapper.toMemberDTO(member)).thenReturn(memberDTO);

        MemberDTO result = borrowService.returnBook(bookID, memberID);


        assertEquals(memberDTO, result);
        assertTrue(member.getBooks().isEmpty());
        assertEquals(2, book.getAmount());
        verify(bookRepository).save(book);
        verify(memberRepository).save(member);
        verify(bookRepository).deleteBookFromMember(bookID, memberID);
    }

    @Test
    public void testReturnBook_BookNotBorrowedByMember() {
        Member member = new Member();
        member.setId(memberID);

        Book book = new Book();
        book.setId(bookID);
        book.setAmount(1);

        Set<Book> borrowedBooks = new HashSet<>();
        member.setBooks(borrowedBooks);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(book));

        MemberReturnEmptyException exception = assertThrows(MemberReturnEmptyException.class, () ->
                borrowService.returnBook(bookID, memberID));
        assertEquals("This book is not borrowed by the member", exception.getMessage());
    }

    @Test
    public void testReturnBook_BookNotFound() {
        Member member = new Member();
        member.setId(memberID);
        Book book = new Book();
        book.setId(bookID);
        book.setAmount(1);

        Set<Book> borrowedBooks = new HashSet<>();
        borrowedBooks.add(book);
        member.setBooks(borrowedBooks);

        when(memberRepository.findById(memberID)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                borrowService.returnBook(bookID, memberID));
        assertEquals("Book with id " + bookID + " not found", exception.getMessage());
    }

    @Test
    public void testReturnBook_MemberNotFound() {
        when(memberRepository.findById(memberID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                borrowService.returnBook(bookID, memberID));
        assertEquals("Member not found", exception.getMessage());
    }

    @Test
    void getBooksBorrowedByMemberName() {
        Book book1 = new Book(1L, "Book Title 1", "Author 1", 1);
        Book book2 = new Book(2L, "Book Title 2", "Author 2", 2);

        Member member = new Member();
        member.setId(1L);
        Set<Book> borrowedBooks = new HashSet<>();
        borrowedBooks.add(book1);
        borrowedBooks.add(book2);
        member.setBooks(borrowedBooks);
        final String memberName = "Gibson";

        when(memberRepository.findByName(memberName)).thenReturn(Optional.of(member));

        List<BookMemberDTO> expectedDTOs = Arrays.asList(
                new BookMemberDTO(1L, "Book Title 1", "Author 1"),
                new BookMemberDTO(2L, "Book Title 2", "Author 2")
        );

        List<BookMemberDTO> result = borrowService.getBooksBorrowedByMemberName(memberName);

        expectedDTOs = expectedDTOs.stream().sorted((b1, b2) -> b1.getID().compareTo(b2.getID())).collect(Collectors.toList());
        result = result.stream().sorted((b1, b2) -> b1.getID().compareTo(b2.getID())).collect(Collectors.toList());

        assertEquals(expectedDTOs, result);
    }

    @Test
    public void testGetBooksBorrowedByMemberName_MemberNotFound() {
        final String memberName = "Cort";
        when(memberRepository.findByName(memberName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                borrowService.getBooksBorrowedByMemberName(memberName));
        assertEquals("Member not found with name: " + memberName, exception.getMessage());
    }

    @Test
    void getAllBorrowedDistinctBook() {
        Book book1 = new Book(1L, "Unique Title", "Author 1", 1);
        Book book2 = new Book(2L, "Another Title", "Author 2", 2);
        Book book3 = new Book(3L, "Unique Title", "Author 3", 0);

        book1.setMembers(Collections.singleton(new Member()));
        book2.setMembers(Collections.singleton(new Member()));
        book3.setMembers(Collections.emptySet());

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<String> expectedTitles = Arrays.asList("Unique Title", "Another Title");


        List<String> result = borrowService.getAllBorrowedDistinctBook();

        assertEquals(expectedTitles, result);
    }


    @Test
    void getAllBorrowDistinctBookNamesWithCount() {
        Book book1 = new Book(1L, "Unique Title", "Author 1", 1);
        Book book2 = new Book(2L, "Another Title", "Author 2", 2);
        Book book3 = new Book(3L, "Unique Title", "Author 3", 0);

        book1.setMembers(Collections.singleton(new Member()));
        book2.setMembers(Collections.singleton(new Member()));
        book3.setMembers(Collections.emptySet());

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        Map<String, Long> expectedMap = new HashMap<>();
        expectedMap.put("Unique Title", 1L);
        expectedMap.put("Another Title", 1L);

        Map<String, Long> result = borrowService.getAllBorrowDistinctBookNamesWithCount();

        assertEquals(expectedMap, result);
    }
}