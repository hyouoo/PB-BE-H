package com.example.purebasketbe.domain.Member;

import com.example.purebasketbe.domain.Member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<User, Long> {

}
