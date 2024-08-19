package com.techTask.library.services;

import com.techTask.library.data.dto.BookDTO;
import com.techTask.library.data.entity.Book;
import com.techTask.library.data.repositories.BookRepository;
import com.techTask.library.exception.BookDeleteBorrowedException;
import com.techTask.library.services.interfaces.BookService;
import com.techTask.library.utils.BookMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;


    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookMapper.toBookDTOs(bookRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return bookMapper.toBookDTO(book.get());
        } else {
            throw new EntityNotFoundException("Book with id " + id + " not found");
        }
    }


    @Override
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        return bookRepository.findByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor())
                .map(book -> {
                    book.setAmount(book.getAmount() + 1);
                    return bookMapper.toBookDTO(bookRepository.save(book));
                })
                .orElseGet(() -> {
                    Book book = bookMapper.toBook(bookDTO);
                    book.setAmount(book.getAmount() + 1);
                    return bookMapper.toBookDTO(bookRepository.save(book));
                });
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(book -> {
                    if (bookDTO.getTitle() != null) {
                        book.setTitle(bookDTO.getTitle());
                    }
                    if (bookDTO.getAuthor() != null) {
                        book.setAuthor(bookDTO.getAuthor());
                    }
                    if (bookDTO.getAmount() != 0) {
                        book.setAmount(bookDTO.getAmount());
                    }
                    return bookMapper.toBookDTO(bookRepository.save(book));
                })
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }


    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        if (!book.getMembers().isEmpty()) {
            throw new BookDeleteBorrowedException("You can't delete a book. It is currently borrowed by users");
        }
        bookRepository.deleteById(id);
    }

}
