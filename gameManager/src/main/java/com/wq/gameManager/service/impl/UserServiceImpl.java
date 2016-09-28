package com.wq.gameManager.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wq.database.gameManager.UserDao;
import com.wq.gameManager.service.UserService;

@Service("UserService")
public class UserServiceImpl implements UserService{
	
	@Resource
	UserDao userDao;

	@Transactional
	@Override
	public void query() {
		System.out.println("In service:"+userDao.query().get(0).getName());
	}

	
}
