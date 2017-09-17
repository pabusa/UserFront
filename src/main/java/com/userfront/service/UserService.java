package com.userfront.service;

import com.userfront.domain.User;

public interface UserService {
	
	void save(User user);
	
	User findByUserName(String username);
	
	User findByEmail(String email);

	boolean checkUserExists(String username, String email);
	
	boolean checkUsernameExists(String username);
	
	boolean checkEmailExists(String email);
	
}
