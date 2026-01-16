package com.excelr.service;

import com.excelr.entity.User;

public interface UserService {
	
	
    public User registerUser(User user);
	
    public User loginUser(String email, String pwd);
    
    public boolean isEmailAlreadyRegistered(String email);  // New method
    
}
