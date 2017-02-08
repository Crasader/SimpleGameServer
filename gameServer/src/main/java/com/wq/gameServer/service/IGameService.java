package com.wq.gameServer.service;

import com.wq.entity.protobuf.Protocol.protocol;


public interface IGameService {

	public void connect(String channelId) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException;
	public void disConnect(String channelId);
	public void service(protocol msg) throws NoSuchMethodException, SecurityException;
	
}
