package com.excelr.service;

import java.util.List;

import com.excelr.entity.Comment;

public interface CommentService {
	
	public Comment addComment(Comment comment);
	
    public List<Comment> getCommentsByPostId(Integer postId);

	public List<Comment> getAllComments();

}
