package com.wq.loginServer.service;

import java.util.List;

import javax.annotation.Resource;

import com.wq.database.mybatis.UserDao;
import com.wq.entity.mould.User;
import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.gameServer.service.GameService;

public class LoginService extends GameService{
	
	@Resource
	UserDao userDao;

	public String login(protocol msg) {
		List<User> userList = userDao.query();
		System.out.println(userList.get(0).getName());
		return "OK";
	}

	@Override
	public void connect(String channelId) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		
	}

	@Override
	public void disConnect(String channelId) {
		
	}
	
}
