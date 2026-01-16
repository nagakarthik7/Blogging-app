package com.excelr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
	
	public List<Post> findByUserId(Integer userId);
	
	List<Post> findByTitleContainingOrContentContaining(String title, String content);


}
