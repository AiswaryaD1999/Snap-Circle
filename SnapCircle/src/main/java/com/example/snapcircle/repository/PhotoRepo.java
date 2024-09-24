package com.example.snapcircle.repository;

import com.example.snapcircle.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long> {
    List<Photo> findByUserId(Long userId);
}
