package com.excelr.service;

import java.util.List;

import com.excelr.entity.Post;

public interface PostService {
	
	public Post createPost(Post post);
	
    public List<Post> getAllPosts();
    
    public List<Post> getPostsByUserId(Integer userId);
    
    public Post getPostById(Integer id);
    
    public void deletePost(Integer id);

	public List<Post> searchPosts(String query);



}
