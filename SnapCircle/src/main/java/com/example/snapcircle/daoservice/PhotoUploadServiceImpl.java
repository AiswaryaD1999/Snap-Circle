package com.example.snapcircle.daoservice;

import com.example.snapcircle.exception.PhotoUploadException;
import com.example.snapcircle.entity.Comments;
import com.example.snapcircle.entity.FriendCircle;
import com.example.snapcircle.entity.Photo;
import com.example.snapcircle.entity.SharedPhoto;
import com.example.snapcircle.entity.User;
import com.example.snapcircle.repository.CommentsRepo;
import com.example.snapcircle.repository.FriendsRepo;
import com.example.snapcircle.repository.PhotoRepo;
import com.example.snapcircle.repository.SharedPhotoRepo;
import com.example.snapcircle.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoUploadServiceImpl implements PhotoUploadService, UserDetailsService {

    @Autowired
    UserRepo userRepository;

    @Autowired
    FriendsRepo friendCircleRepository;

    @Autowired
    PhotoRepo photoRepository;

    @Autowired
    CommentsRepo commentRepository;

    @Autowired
    SharedPhotoRepo sharedPhotoRepo;

    @Override
    public User save(User user) throws PhotoUploadException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw new PhotoUploadException("Failed to save user", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    @Override
    public Optional<User> findByUsername(String username) throws PhotoUploadException {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            throw new PhotoUploadException("Failed to find user by username", e);
        }
    }

    @Override
    public Photo uploadPhoto(String file, String description, Long userId) throws PhotoUploadException {
        try {
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new PhotoUploadException("User not found with id: " + userId));

            Photo photo = new Photo();
            photo.setBase64Data(file);
            photo.setDescription(description);
            photo.setUserId(user.getUserId());
            photo.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            photo.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            return photoRepository.save(photo);
        } catch (Exception e) {
            System.err.println("Error uploading photo: " + e.getMessage());
            throw new PhotoUploadException("Failed to upload photo", e);
        }
    }


    @Override
    public List<Photo> getPhotosByUser(Long userId) throws PhotoUploadException {
        try {
            return photoRepository.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error retrieving photos for user: " + e.getMessage());
            throw new PhotoUploadException("Failed to retrieve photos for user with id: " + userId, e);
        }
    }

    @Override
    public void deletePhoto(Long photoId) throws PhotoUploadException {
        try {
            photoRepository.deleteById(photoId);
        } catch (Exception e) {
            System.err.println("Error deleting photo with id: " + photoId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to delete photo with id: " + photoId, e);
        }
    }


    @Override
    public Comments addComment(Long photoId, Long userId, String text) throws PhotoUploadException {
        try {
            Comments comment = new Comments();
            comment.setPhotoId(photoId);
            comment.setUserId(userId);
            comment.setCommentText(text);
            comment.setCommentedDate(new Timestamp(System.currentTimeMillis()));
            return commentRepository.save(comment);
        } catch (Exception e) {
            System.err.println("Error adding comment to photo with id: " + photoId + " by user id: " + userId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to add comment to photo with id: " + photoId + " by user id: " + userId, e);
        }
    }

    @Override
    public List<Comments> getCommentsByPhoto(Long photoId) throws PhotoUploadException {
        try {
            return commentRepository.findByPhotoId(photoId);
        } catch (Exception e) {
            System.err.println("Error retrieving comments for photo with id: " + photoId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to retrieve comments for photo with id: " + photoId, e);
        }
    }


    @Override
    public void deleteComment(Long commentId) throws PhotoUploadException {
        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            System.err.println("Error deleting comment with id: " + commentId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to delete comment with id: " + commentId, e);
        }
    }


    @Override
    public FriendCircle sendFriendRequest(Long userId, Long friendId) throws PhotoUploadException {
        try {
            FriendCircle friendRequest = new FriendCircle();
            friendRequest.setUserId(userId);
            friendRequest.setFriendId(friendId);
            friendRequest.setStatus("PENDING");
            friendRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            friendRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            return friendCircleRepository.save(friendRequest);
        } catch (Exception e) {
            System.err.println("Error sending friend request from user ID: " + userId + " to friend ID: " + friendId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to send friend request from user ID: " + userId + " to friend ID: " + friendId, e);
        }
    }


    @Override
    public FriendCircle acceptFriendRequest(Long friendCircleId) throws PhotoUploadException {
        try {
            FriendCircle friendCircle = friendCircleRepository.findById(friendCircleId)
                    .orElseThrow(() -> new PhotoUploadException("FriendCircle not found with id: " + friendCircleId));
            friendCircle.setStatus("ACCEPTED");
            friendCircle.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return friendCircleRepository.save(friendCircle);
        } catch (Exception e) {
            System.err.println("Error accepting friend request with id: " + friendCircleId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to accept friend request with id: " + friendCircleId, e);
        }
    }

    @Override
    public FriendCircle rejectFriendRequest(Long friendCircleId) throws PhotoUploadException {
        try {
            FriendCircle friendCircle = friendCircleRepository.findById(friendCircleId)
                    .orElseThrow(() -> new PhotoUploadException("FriendCircle not found with id: " + friendCircleId));
            friendCircle.setStatus("REJECTED");
            friendCircle.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return friendCircleRepository.save(friendCircle);
        } catch (Exception e) {
            System.err.println("Error rejecting friend request with id: " + friendCircleId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to reject friend request with id: " + friendCircleId, e);
        }
    }

    @Override
    public List<FriendCircle> getFriends(Long userId) throws PhotoUploadException {
        try {
            return friendCircleRepository.findByUserIdAndStatus(userId, "ACCEPTED");
        } catch (Exception e) {
            System.err.println("Error retrieving friends for user id: " + userId + ". " + e.getMessage());
            throw new PhotoUploadException("Failed to retrieve friends for user id: " + userId, e);
        }
    }


    @Override
    public void sharePhotoWithFriends(Long photoId, Long userId, List<Long> friendIds) throws PhotoUploadException {
        try {
            List<SharedPhoto> sharedPhotos = new ArrayList<>();
            for (Long friendId : friendIds) {
                SharedPhoto sharedPhoto = new SharedPhoto();
                sharedPhoto.setPhotoId(photoId);
                sharedPhoto.setUserId(friendId);
                sharedPhoto.setSharedBy(userId);
                sharedPhoto.setSharedAt(new Timestamp(System.currentTimeMillis()));
                sharedPhotos.add(sharedPhoto);
            }
            sharedPhotoRepo.saveAll(sharedPhotos);
        } catch (Exception exception) {
            System.err.println("Error sharing photo with friends: " + exception.getMessage());
            throw new PhotoUploadException("Failed to share photo with friends for photo ID: " + photoId, exception);
        }
    }

    @Override
    public List<Photo> getPhotosSharedWithUser(Long userId) throws PhotoUploadException {
        try {
            return sharedPhotoRepo.findPhotosSharedWithUser(userId);
        } catch (Exception exception) {
            System.err.println("Error retrieving photos shared with user id: " + userId + ". " + exception.getMessage());
            throw new PhotoUploadException("Failed to retrieve photos shared with user id: " + userId, exception);
        }
    }



}
