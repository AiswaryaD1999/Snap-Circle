package com.example.snapcircle.daoservice;

import com.example.snapcircle.exception.PhotoUploadException;
import com.example.snapcircle.entity.Comments;
import com.example.snapcircle.entity.FriendCircle;
import com.example.snapcircle.entity.Photo;
import com.example.snapcircle.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PhotoUploadService {
    User save(User user) throws PhotoUploadException;
    Optional<User> findByUsername(String username) throws PhotoUploadException;

    Photo uploadPhoto(String file, String description, Long userId) throws PhotoUploadException;

    List<Photo> getPhotosByUser(Long userId) throws PhotoUploadException;

    void deletePhoto(Long photoId) throws PhotoUploadException;

    Comments addComment(Long photoId, Long userId, String text) throws PhotoUploadException;


    List<Comments> getCommentsByPhoto(Long photoId) throws PhotoUploadException;

    void deleteComment(Long commentId) throws PhotoUploadException;

    FriendCircle sendFriendRequest(Long userId, Long friendId) throws PhotoUploadException;

    FriendCircle acceptFriendRequest(Long friendCircleId) throws PhotoUploadException;

    FriendCircle rejectFriendRequest(Long friendCircleId) throws PhotoUploadException;

    List<FriendCircle> getFriends(Long userId) throws PhotoUploadException;

    void sharePhotoWithFriends(Long photoId, Long userId, List<Long> friendIds) throws PhotoUploadException;

    List<Photo> getPhotosSharedWithUser(Long userId) throws PhotoUploadException;
}
