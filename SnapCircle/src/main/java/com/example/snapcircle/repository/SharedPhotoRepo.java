package com.example.snapcircle.repository;

import com.example.snapcircle.entity.Photo;
import com.example.snapcircle.entity.SharedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedPhotoRepo extends JpaRepository<SharedPhoto, Long> {

    @Query("SELECT p FROM Photo p JOIN SharedPhoto sp ON p.photoId = sp.photoId WHERE sp.userId = :userId")
    List<Photo> findPhotosSharedWithUser(Long userId);
}
