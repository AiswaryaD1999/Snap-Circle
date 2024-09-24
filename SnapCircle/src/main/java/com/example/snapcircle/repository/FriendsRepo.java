package com.example.snapcircle.repository;

import com.example.snapcircle.entity.FriendCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepo extends JpaRepository<FriendCircle, Long> {
    List<FriendCircle> findByUserIdAndStatus(Long userId, String accepted);
}
