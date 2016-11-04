package com.wq.worldServer.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.server.ServerLogin;
import com.wq.entity.protobuf.server.ServerLogin.WorldLoginReq;
import com.wq.entity.protobuf.server.ServerLogin.WorldLoginRes;
import com.wq.entity.server.ServerInfo;
import com.wq.gameServer.service.Service;

public class ServerLoginService extends Service{

	private ServerInfo currentServer;
	private Map<Integer, ServerInfo> servers = new HashMap<>();
	
	@Override
	public void activeService(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String address = channel.remoteAddress().toString();
		boolean hasConnected = false;
		for(ServerInfo server : servers.values()){
			if(address.equals(server.getIp()+":"+server.getPort())){
				hasConnected = true;
				break;
			}
		}
		if(!hasConnected){
			WorldLoginReq.Builder request = WorldLoginReq.newBuilder();
			protocol.Builder message = protocol.newBuilder();
			message.setName("ServerLoginService_login");
			message.setFromId(currentServer.getId());
			message.setExtension(ServerLogin.worldLoginReq, request.build());
			channel.write(message.build());
		}
	}
	
	public void login(protocol msg){
		WorldLoginRes response = msg.getExtension(ServerLogin.worldLoginRes);
		if(response == null){
			throw new NullPointerException("Null when need a worldLoginReq");
		}
		Integer serverId = response.getServerId();
		if(servers.containsKey(serverId)){
			ServerInfo server = servers.get(serverId);
			server.setUpdateTime(new Date());
			server.setOnlineCount(response.getOnlineCount());
		}else{
			ServerInfo server = new ServerInfo();
			server.setId(serverId);
			server.setName(response.getName());
			server.setType(response.getType());
			server.setOnlineCount(response.getOnlineCount());
			server.setUpdateTime(new Date());
			servers.put(serverId, server);
		}
	}
	
	@Override
	public void inactiveService(ChannelHandlerContext ctx) {
		Iterator<Entry<Integer, ServerInfo>> it = servers.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ServerInfo> entry = it.next();
			if(!entry.getValue().getChannel().isOpen()){
				it.remove();
				// log
			}
		}
	}

	public ServerInfo getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(ServerInfo currentServer) {
		this.currentServer = currentServer;
	}

}
