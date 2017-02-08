package com.wq.loginServer.service;

import javax.annotation.Resource;

import com.wq.database.mybatis.UserDao;
import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.server.ServerLogin.WorldLoginRes;
import com.wq.gameServer.service.GameService;

public class ServerLoginService extends GameService{
	
	@Resource
	UserDao userDao;

	public void login(protocol msg) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		userDao.query();
		WorldLoginRes.Builder response = WorldLoginRes.newBuilder();
		response.setServerId(localServer.getId());
		response.setName(localServer.getName());
		response.setType(localServer.getType());
		write(msg.getFromId(), response.build());
	}

	@Override
	public void connect(String channelId) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		
	}

	@Override
	public void disConnect(String channelId) {
		
	}
	
}
