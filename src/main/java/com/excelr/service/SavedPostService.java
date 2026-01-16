package com.excelr.service;

import java.util.List;

import com.excelr.entity.Post;
import com.excelr.entity.SavedPost;
import com.excelr.entity.User;

public interface SavedPostService {
    
    // Save a post for a user
    SavedPost savePost(User user, Post post);
    
    // Unsave a post for a user
    void unsavePost(User user, Post post);
    
    // Check if post is saved by user
    boolean isPostSaved(User user, Post post);
    
    // Get all saved posts for a user
    List<SavedPost> getSavedPostsByUser(User user);
    
    // Get all saved posts for a user ID
    List<SavedPost> getSavedPostsByUserId(Integer userId);
    
    // Get all posts that are saved by a user (just the Post objects)
    List<Post> getSavedPostsAsPosts(Integer userId);
    
    // Count saved posts for a user
    long countSavedPosts(Integer userId);
}
