package com.example.snapcircle.repository;

import com.example.snapcircle.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepo extends JpaRepository<Comments, Long> {
    List<Comments> findByPhotoId(Long photoId);

}
