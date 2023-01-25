package com.example.lifechallenge.repository;

import com.example.lifechallenge.domain.MemberLikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberLIkePostRepository extends JpaRepository<MemberLikePost, Long> {
}
