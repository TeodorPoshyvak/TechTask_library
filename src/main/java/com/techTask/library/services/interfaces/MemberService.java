package com.techTask.library.services.interfaces;

import com.techTask.library.data.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    List<MemberDTO> getMembers();

    MemberDTO getMemberById(Long id);

    MemberDTO createMember(MemberDTO memberDTO);

    MemberDTO updateMember(Long id, MemberDTO memberDTO);

    void deleteMember(Long id);
}
