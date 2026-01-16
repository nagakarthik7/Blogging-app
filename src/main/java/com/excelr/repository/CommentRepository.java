package com.excelr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.entity.Comment;
import com.excelr.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
	
	public List<Comment> findByPost(Post post);
	
	List<Comment> findCommentsByPostId(Integer postId);

}
