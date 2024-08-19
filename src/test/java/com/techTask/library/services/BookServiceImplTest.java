package com.techTask.library.services;

import com.techTask.library.data.dto.BookDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.repositories.BookRepository;
import com.techTask.library.exception.BookDeleteBorrowedException;
import com.techTask.library.utils.BookMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private final long ID = 1L;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;


    @Test
    void getAllBooksTest() {
        List<Book> testBooks = Arrays.asList(
                new Book("Title1", "Author First", 1),
                new Book("Title2", "Author Second", 1)
        );

        Mockito.when(bookRepository.findAll()).thenReturn(testBooks);
        Mockito.when(bookMapper.toBookDTOs(testBooks)).thenReturn(
                Arrays.asList(
                        new BookDTO("Title1", "Author First", 1),
                        new BookDTO("Title2", "Author Second", 1)
                )
        );

        List<BookDTO> books = bookService.getAllBooks();

        assertFalse(books.isEmpty());
        assertEquals(2, books.size());
        assertEquals("Title1", books.get(0).getTitle());
        assertEquals("Author Second", books.get(1).getAuthor());
    }

    @Test
    void getBookByIdTest() {
        Book testBook = new Book(ID, "Title1", "Author First", 1);

        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.of(testBook));
        Mockito.when(bookMapper.toBookDTO(testBook))
                .thenReturn(new BookDTO(ID, "Title1", "Author First", 1));

        BookDTO result = bookService.getBookById(ID);

        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        assertEquals(ID, result.getId());
    }

    @Test
    void getBookById_NotFoundTest() {
        final Long bookID = 2L;
        Mockito.when(bookRepository.findById(bookID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.getBookById(bookID);
        });

        assertEquals("Book with id " + bookID + " not found", exception.getMessage());
    }

    @Test
    void createBook_WhenBookExistsTest() {
        Book existingBook = new Book(ID, "Title1", "Author First", 1);

        Mockito.when(bookRepository.findByTitleAndAuthor("Title1", "Author First"))
                .thenReturn(Optional.of(existingBook));
        Mockito.when(bookRepository.save(existingBook))
                .thenReturn(existingBook);
        Mockito.when(bookMapper.toBookDTO(existingBook))
                .thenReturn(new BookDTO(ID, "Title1", "Author First", 2));

        BookDTO result = bookService.createBook(new BookDTO(ID, "Title1", "Author First", 1));

        assertNotNull(result);
        assertEquals(2, result.getAmount());
    }

    @Test
    void createBook_WhenBookDoesNotExistTest() {
        BookDTO testBookDTO = new BookDTO(ID, "Title1", "Author First", 1);

        Mockito.when(bookRepository.findByTitleAndAuthor("Title1", "Author First"))
                .thenReturn(Optional.empty());
        Mockito.when(bookMapper.toBook(testBookDTO))
                .thenReturn(
                        new Book(ID, "Title1", "Author First", 1));
        Mockito.when(bookRepository.save(Mockito.any(Book.class)))
                .thenReturn(new Book(ID, "Title1", "Author First", 1));
        Mockito.when(bookMapper.toBookDTO(Mockito.any(Book.class)))
                .thenReturn(new BookDTO(ID, "Title1", "Author First", 1));

        BookDTO result = bookService.createBook(testBookDTO);

        assertNotNull(result);
        assertEquals("Title1", result.getTitle());
        assertEquals("Author First", result.getAuthor());
    }

    @Test
    void updateExistBookTest() {
        Book existingBook = new Book(ID, "Old Title", "Old Author", 5);
        Book updatedBook = new Book(ID, "Updated Title", "Updated Author", 10);
        BookDTO bookDTO = new BookDTO(ID, "Updated Title", "Updated Author", 10);

        Mockito.when(bookRepository.findById(ID))
                .thenReturn(Optional.of(existingBook));
        Mockito.when(bookRepository.save(existingBook))
                .thenReturn(updatedBook);
        Mockito.when(bookMapper.toBookDTO(updatedBook))
                .thenReturn(bookDTO);

        BookDTO result = bookService.updateBook(ID, bookDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        assertEquals(10, result.getAmount());
    }

    @Test
    void updateNotFoundBookTest() {
        BookDTO bookDTO = new BookDTO(ID, "Updated Title", "Updated Author", 10);

        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook(ID, bookDTO);
        });

        assertEquals("Book not found with id: " + ID, thrown.getMessage());
    }

    @Test
    void deleteBookTest() {
        Book book = new Book(ID, "Title", "Author", 1);
        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        bookService.deleteBook(ID);

        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(book.getId());
    }

    @Test
    void deleteBookBorrowedTest() {
        Book book = new Book(ID, "Title", "Author", 1);
        Set<Member> members = new HashSet<>(Arrays.asList(new Member(), new Member()));
        book.setMembers(members);

        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.of(book));

        BookDeleteBorrowedException thrown = assertThrows(BookDeleteBorrowedException.class, () -> {
            bookService.deleteBook(ID);
        });
        assertEquals("You can't delete a book. It is currently borrowed by users", thrown.getMessage());
    }

    @Test
    void deleteNotFoundBookTest() {
        Mockito.when(bookRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.deleteBook(ID);
        });
        assertEquals("Book not found with id: " + ID, thrown.getMessage());
    }
}