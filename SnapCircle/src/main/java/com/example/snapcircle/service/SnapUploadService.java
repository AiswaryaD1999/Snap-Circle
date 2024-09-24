package com.example.snapcircle.service;

import com.example.snapcircle.exception.PhotoUploadException;
import com.example.snapcircle.daoservice.PhotoUploadService;
import com.example.snapcircle.entity.Comments;
import com.example.snapcircle.entity.FriendCircle;
import com.example.snapcircle.entity.Photo;
import com.example.snapcircle.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class SnapUploadService {

    @Autowired
    PhotoUploadService photoUploadService;

    private static final Logger logger = LoggerFactory.getLogger(SnapUploadService.class);

    public User createUser(User user) throws PhotoUploadException {
        try {
            return photoUploadService.save(user);
        } catch (PhotoUploadException e) {
            // Log the exception and rethrow it
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<User> findUserByUsername(String username) throws PhotoUploadException {
        try {
            return photoUploadService.findByUsername(username);
        } catch (PhotoUploadException e) {
            logger.error("Error finding user by username: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Photo uploadPhoto(String file, String description, Long userId) throws PhotoUploadException {
        try {
            return photoUploadService.uploadPhoto(file, description, userId);
        } catch (PhotoUploadException e) {
            logger.error("Error uploading photo: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Photo> getPhotosByUser(Long userId) throws PhotoUploadException {
        try {
            return photoUploadService.getPhotosByUser(userId);
        } catch (PhotoUploadException e) {
            logger.error("Error getting photos by user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deletePhoto(Long photoId) throws PhotoUploadException {
        try {
            photoUploadService.deletePhoto(photoId);
        } catch (PhotoUploadException e) {
            logger.error("Error deleting photo: {}", e.getMessage(), e);
            throw e;
        }
    }

    public FriendCircle sendFriendRequest(Long userId, Long friendId) throws PhotoUploadException {
        try {
            return photoUploadService.sendFriendRequest(userId, friendId);
        } catch (PhotoUploadException e) {
            logger.error("Error sending friend request: {}", e.getMessage(), e);
            throw e;
        }
    }

    public FriendCircle acceptFriendRequest(Long friendCircleId) throws PhotoUploadException {
        try {
            return photoUploadService.acceptFriendRequest(friendCircleId);
        } catch (PhotoUploadException e) {
            logger.error("Error accepting friend request: {}", e.getMessage(), e);
            throw e;
        }
    }

    public FriendCircle rejectFriendRequest(Long friendCircleId) throws PhotoUploadException {
        try {
            return photoUploadService.rejectFriendRequest(friendCircleId);
        } catch (PhotoUploadException e) {
            logger.error("Error rejecting friend request: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FriendCircle> getFriends(Long userId) throws PhotoUploadException {
        try {
            return photoUploadService.getFriends(userId);
        } catch (PhotoUploadException e) {
            logger.error("Error getting friends: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Comments addComment(Long photoId, Long userId, String text) throws PhotoUploadException {
        try {
            return photoUploadService.addComment(photoId, userId, text);
        } catch (PhotoUploadException e) {
            logger.error("Error adding comment: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Comments> getCommentsByPhoto(Long photoId) throws PhotoUploadException {
        try {
            return photoUploadService.getCommentsByPhoto(photoId);
        } catch (PhotoUploadException e) {
            logger.error("Error getting comments by photo: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteComment(Long commentId) throws PhotoUploadException {
        try {
            photoUploadService.deleteComment(commentId);
        } catch (PhotoUploadException e) {
            logger.error("Error deleting comment: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sharePhotoWithFriends(Long photoId, Long userId, List<Long> friendIds) throws PhotoUploadException {
        try {
            photoUploadService.sharePhotoWithFriends(photoId, userId, friendIds);
        } catch (PhotoUploadException e) {
            logger.error("Error sharing photo with friends: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Photo> getPhotosSharedWithUser(Long userId) throws PhotoUploadException {
        try {
            return photoUploadService.getPhotosSharedWithUser(userId);
        } catch (PhotoUploadException e) {
            logger.error("Error getting photos shared with user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
