package com.userfront.service.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.dao.UserDao;
import com.userfront.domain.User;
import com.userfront.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired()
	private UserDao userDao;
	
	@Override
	public void save(User user){
		userDao.save(user);
	}

	@Override
	public User findByUserName(String username) {
		return userDao.findByUserName(username);
	}

	@Override
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	@Override
	public boolean checkUserExists(String username, String email) {
		if (checkUsernameExists(username) ||  checkEmailExists(email)){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkUsernameExists(String username) {
		if (null != findByUserName(username)){
			return true;
		} else {
			return false;	
		}
	}

	@Override
	public boolean checkEmailExists(String email) {
		if (null != findByEmail(email)){
			return true;
		} else {
			return false;	
		}
	}
	
	
}
