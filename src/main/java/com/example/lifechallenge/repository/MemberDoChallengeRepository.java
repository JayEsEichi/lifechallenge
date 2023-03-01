package com.example.lifechallenge.repository;

import com.example.lifechallenge.domain.MemberDoChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberDoChallengeRepository extends JpaRepository<MemberDoChallenge, Long> {
}
