package com.excelr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.entity.Post;
import com.excelr.entity.SavedPost;
import com.excelr.entity.User;

public interface SavedPostRepository extends JpaRepository<SavedPost, Integer> {
    
    // Find all saved posts by user
    List<SavedPost> findByUser(User user);
    
    // Find all saved posts by user ID
    List<SavedPost> findByUserId(Integer userId);
    
    // Check if a post is saved by a user
    Optional<SavedPost> findByUserAndPost(User user, Post post);
    
    // Check if a post is saved by user ID and post ID
    Optional<SavedPost> findByUserIdAndPostId(Integer userId, Integer postId);
    
    // Delete saved post by user and post
    void deleteByUserAndPost(User user, Post post);
    
    // Count saved posts by user
    long countByUserId(Integer userId);
}
