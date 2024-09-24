package com.example.snapcircle.controller;

import com.example.snapcircle.entity.Comments;
import com.example.snapcircle.entity.FriendCircle;
import com.example.snapcircle.entity.Photo;
import com.example.snapcircle.entity.User;
import com.example.snapcircle.exception.PhotoUploadException;
import com.example.snapcircle.model.CommentModel;
import com.example.snapcircle.model.FriendRequestModel;
import com.example.snapcircle.model.LoginRequest;
import com.example.snapcircle.model.SharePhotoRequest;
import com.example.snapcircle.model.UploadPictureModel;
import com.example.snapcircle.repository.UserRepo;
import com.example.snapcircle.service.SnapUploadService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PhotoUploadController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    SnapUploadService snapUploadService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(PhotoUploadController.class);


    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response,HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            String sessionId = session.getId();
            Cookie cookie = new Cookie("JSESSIONID", sessionId);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setSecure(false);
            response.addCookie(cookie);

            return ResponseEntity.ok("Login successful!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred!");
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout successful!");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> user = snapUploadService.findUserByUsername(username);

            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

        } catch (PhotoUploadException e) {
            logger.error("Error finding user by username: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while finding the user. Please try again.");
        }
    }


    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User newUser = snapUploadService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (PhotoUploadException e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user. Please try again.");
        }
    }

    @PostMapping("/photo/upload")
    public ResponseEntity<?> uploadPhoto(@RequestBody UploadPictureModel uploadPictureModel) {
        try {
            Photo photo = snapUploadService.uploadPhoto(uploadPictureModel.getBase64Image(), uploadPictureModel.getDescription(), uploadPictureModel.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(photo);
        } catch (PhotoUploadException e) {
            logger.error("Error uploading photo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while uploading the photo. Please try again.");
        }
    }

    @GetMapping("/photo/getPhoto")
    public ResponseEntity<?> getPhotosByUser(@RequestParam(name = "userId") Long userId) {
        try {
            List<Photo> photos = snapUploadService.getPhotosByUser(userId);
            return ResponseEntity.ok(photos);
        } catch (PhotoUploadException e) {
            logger.error("Error retrieving photos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving photos. Please try again.");
        }
    }

    @DeleteMapping("/photo/deletePhoto")
    public ResponseEntity<?> deletePhoto(@RequestParam(name = "photoId") Long photoId) {
        try {
            snapUploadService.deletePhoto(photoId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PhotoUploadException e) {
            logger.error("Error deleting photo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the photo. Please try again.");
        }
    }

    @PostMapping("/friend/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestModel friendRequestModel) {
        try {
            FriendCircle friendRequest = snapUploadService.sendFriendRequest(friendRequestModel.getUserId(), friendRequestModel.getFriendId());
            return ResponseEntity.status(HttpStatus.CREATED).body(friendRequest);
        } catch (PhotoUploadException e) {
            logger.error("Error sending friend request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while sending the friend request. Please try again.");
        }
    }

    @PutMapping("/friend/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam(name = "friendCircleId") Long friendCircleId) {
        try {
            FriendCircle friendCircle = snapUploadService.acceptFriendRequest(friendCircleId);
            return ResponseEntity.ok(friendCircle);
        } catch (PhotoUploadException e) {
            logger.error("Error accepting friend request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while accepting the friend request. Please try again.");
        }
    }

    @PutMapping("/friend/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestParam(name = "friendCircleId") Long friendCircleId) {
        try {
            FriendCircle friendCircle = snapUploadService.rejectFriendRequest(friendCircleId);
            return ResponseEntity.ok(friendCircle);
        } catch (PhotoUploadException e) {
            logger.error("Error rejecting friend request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while rejecting the friend request. Please try again.");
        }
    }

    @GetMapping("/friend/getFriendCircle")
    public ResponseEntity<?> getFriendCircle(@RequestParam(name = "userId") Long userId) {
        try {
            List<FriendCircle> friends = snapUploadService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (PhotoUploadException e) {
            logger.error("Error retrieving friend circle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the friend circle. Please try again.");
        }
    }

    @PostMapping("/comment/add")
    public ResponseEntity<?> addComment(@RequestBody CommentModel commentModel) {
        try {
            Comments comment = snapUploadService.addComment(commentModel.getPhotoId(), commentModel.getUserId(), commentModel.getText());
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (PhotoUploadException e) {
            logger.error("Error adding comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the comment. Please try again.");
        }
    }

    @GetMapping("/comment/getComments")
    public ResponseEntity<?> getCommentsByPhoto(@RequestParam(name = "photoId") Long photoId) {
        try {
            List<Comments> comments = snapUploadService.getCommentsByPhoto(photoId);
            return ResponseEntity.ok(comments);
        } catch (PhotoUploadException e) {
            logger.error("Error retrieving comments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the comments. Please try again.");
        }
    }

    @DeleteMapping("/comment/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestParam(name = "commentId") Long commentId) {
        try {
            snapUploadService.deleteComment(commentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PhotoUploadException e) {
            logger.error("Error deleting comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the comment. Please try again.");
        }
    }

    @PostMapping("/photo/share")
    public ResponseEntity<?> sharePhoto(@RequestBody SharePhotoRequest request) {
        try {
            snapUploadService.sharePhotoWithFriends(request.getPhotoId(), request.getUserId(), request.getFriendIds());
            return ResponseEntity.status(HttpStatus.OK).body("Photo shared successfully.");
        } catch (PhotoUploadException e) {
            logger.error("Error sharing photo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while sharing the photo. Please try again.");
        }
    }

    @GetMapping("/photo/shared")
    public ResponseEntity<?> getSharedPhotos(@RequestParam("userId") Long userId) {
        try {
            List<Photo> sharedPhotos = snapUploadService.getPhotosSharedWithUser(userId);
            return ResponseEntity.ok(sharedPhotos);
        } catch (PhotoUploadException e) {
            logger.error("Error retrieving shared photos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the shared photos. Please try again.");
        }
    }

}
