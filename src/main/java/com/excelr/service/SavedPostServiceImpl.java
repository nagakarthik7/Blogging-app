package com.excelr.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excelr.entity.Post;
import com.excelr.entity.SavedPost;
import com.excelr.entity.User;
import com.excelr.repository.SavedPostRepository;

@Service
public class SavedPostServiceImpl implements SavedPostService {
    
    @Autowired
    private SavedPostRepository savedPostRepository;
    
    @Override
    @Transactional
    public SavedPost savePost(User user, Post post) {
        // Check if already saved
        Optional<SavedPost> existing = savedPostRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            return existing.get(); // Already saved, return existing
        }
        
        // Create new saved post
        SavedPost savedPost = new SavedPost();
        savedPost.setUser(user);
        savedPost.setPost(post);
        
        return savedPostRepository.save(savedPost);
    }
    
    @Override
    @Transactional
    public void unsavePost(User user, Post post) {
        savedPostRepository.deleteByUserAndPost(user, post);
    }
    
    @Override
    public boolean isPostSaved(User user, Post post) {
        return savedPostRepository.findByUserAndPost(user, post).isPresent();
    }
    
    @Override
    public List<SavedPost> getSavedPostsByUser(User user) {
        return savedPostRepository.findByUser(user);
    }
    
    @Override
    public List<SavedPost> getSavedPostsByUserId(Integer userId) {
        return savedPostRepository.findByUserId(userId);
    }
    
    @Override
    public List<Post> getSavedPostsAsPosts(Integer userId) {
        List<SavedPost> savedPosts = savedPostRepository.findByUserId(userId);
        return savedPosts.stream()
                .map(SavedPost::getPost)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countSavedPosts(Integer userId) {
        return savedPostRepository.countByUserId(userId);
    }
}
