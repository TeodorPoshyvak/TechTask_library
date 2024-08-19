package com.techTask.library.data.repositories;

import com.techTask.library.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByName(String name);

    Optional<Member> findByName(String name);
}
