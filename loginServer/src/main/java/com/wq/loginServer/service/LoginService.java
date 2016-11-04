package com.wq.loginServer.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import javax.annotation.Resource;

import com.wq.database.mybatis.UserDao;
import com.wq.entity.mould.User;
import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.gameServer.service.Service;

public class LoginService extends Service{
	
	@Resource
	UserDao userDao;

	public String login(protocol msg) {
		List<User> userList = userDao.query();
		System.out.println(userList.get(0).getName());
		return "OK";
	}

	@Override
	public void activeService(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void inactiveService(ChannelHandlerContext ctx) {
		
	}

	
}
