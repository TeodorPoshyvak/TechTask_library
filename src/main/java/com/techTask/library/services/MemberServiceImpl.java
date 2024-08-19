package com.techTask.library.services;

import com.techTask.library.data.dto.MemberDTO;
import com.techTask.library.data.entity.Member;
import com.techTask.library.data.repositories.MemberRepository;
import com.techTask.library.exception.DuplicateNameException;
import com.techTask.library.exception.MemberDeleteException;
import com.techTask.library.services.interfaces.MemberService;
import com.techTask.library.utils.MemberMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> getMembers() {
        return memberMapper.toMemberDTOs(memberRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return memberMapper.toMemberDTO(member.get());
        } else {
            throw new EntityNotFoundException("Member with id " + id + " not found");
        }
    }

    @Override
    @Transactional
    public MemberDTO createMember(MemberDTO memberDTO) {
        if (memberRepository.existsByName(memberDTO.getName())) {
            throw new DuplicateNameException("Member with name " + memberDTO.getName() + " already exists.");
        }
        Member member = memberRepository.save(memberMapper.toMember(memberDTO));
        return memberMapper.toMemberDTO(member);
    }

    @Override
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        return memberRepository.findById(id)
                .map(member -> {
                    if (memberRepository.existsByName(memberDTO.getName())) {
                        throw new DuplicateNameException(
                                "Member with name " + memberDTO.getName() + " already exists.");
                    }
                    return memberMapper.toMemberDTO(memberRepository
                            .save(memberMapper.toMember(memberDTO)));
                })
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        if (!member.getBooks().isEmpty()) {
            throw new MemberDeleteException("Member cannot be deleted because they have borrowed books");
        }
        memberRepository.deleteById(id);
    }


}
