package com.techTask.library.services.interfaces;

import com.techTask.library.data.dto.BookMemberDTO;
import com.techTask.library.data.dto.MemberDTO;

import java.util.List;
import java.util.Map;

public interface BorrowService {
    MemberDTO borrowBook(Long bookId, long memberId);

    MemberDTO returnBook(Long bookId, long memberId);

    List<BookMemberDTO> getBooksBorrowedByMemberName(String name);

    List<String> getAllBorrowedDistinctBook();

    Map<String, Long> getAllBorrowDistinctBookNamesWithCount();
}
