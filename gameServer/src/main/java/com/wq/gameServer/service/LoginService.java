package com.wq.gameServer.service;

import java.util.List;

import javax.annotation.Resource;

import com.wq.database.mybatis.UserDao;
import com.wq.entity.mould.User;
import com.wq.entity.protobuf.Protocol.protocol;

public class LoginService extends Service{
	
	@Resource
	UserDao userDao;

	public String login(protocol msg) {
		List<User> userList = userDao.query();
		System.out.println(userList.get(0).getName());
		return "OK";
	}

	
}
