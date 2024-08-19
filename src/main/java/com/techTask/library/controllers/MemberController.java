package com.techTask.library.controllers;

import com.techTask.library.data.request.member.MemberRequest;
import com.techTask.library.data.request.member.UpdateMemberRequest;
import com.techTask.library.data.response.ErrorResponse;
import com.techTask.library.data.response.MemberResponse;
import com.techTask.library.exception.DuplicateNameException;
import com.techTask.library.exception.MemberDeleteException;
import com.techTask.library.services.interfaces.MemberService;
import com.techTask.library.utils.MemberMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "member_methods")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;
    @Autowired
    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberMapper.toMemberResponses(memberService.getMembers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(memberMapper.toMemberResponse(memberService.getMemberById(id)));
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createMember(@ModelAttribute MemberRequest memberRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(memberMapper.toMemberResponse(
                            memberService.createMember(
                                    memberMapper.toMemberDTO(memberRequest))));
        } catch (DuplicateNameException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody MemberRequest memberRequest) {
        try {
            UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest(memberRequest.getName());
            return ResponseEntity.status(HttpStatus.OK).body(
                    memberMapper.toMemberResponse(
                            memberService.updateMember(id, memberMapper.toMemberDTO(updateMemberRequest))));
        } catch (DuplicateNameException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (MemberDeleteException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
