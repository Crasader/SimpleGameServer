package com.wq.loginServer.service;

import io.netty.channel.ChannelHandlerContext;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.server.ServerLogin;
import com.wq.entity.protobuf.server.ServerLogin.WorldLoginRes;
import com.wq.entity.server.ServerInfo;
import com.wq.gameServer.service.Service;

public class ServerLoginService extends Service{
	
	ServerInfo currentServer;

	public void login(protocol msg) {
		WorldLoginRes.Builder response = WorldLoginRes.newBuilder();
		response.setServerId(currentServer.getId());
		response.setName(currentServer.getName());
		response.setType(currentServer.getType());
		protocol.Builder message = protocol.newBuilder();
		message.setName("ServerLoginService_login");
		message.setFromId(currentServer.getId());
		message.setToId(msg.getFromId());
		message.setExtension(ServerLogin.worldLoginRes, response.build());
		write(message.build());
	}
	
	@Override
	public void activeService(ChannelHandlerContext ctx) {
		
	}
	
	@Override
	public void inactiveService(ChannelHandlerContext ctx) {
		
	}

	public ServerInfo getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(ServerInfo currentServer) {
		this.currentServer = currentServer;
	}

}
