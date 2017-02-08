package com.wq.worldServer.service;

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
import com.wq.gameServer.service.GameService;

public class ServerLoginService extends GameService{

	private Map<Integer, ServerInfo> servers = new HashMap<>();
	
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
	public void connect(String channelId) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		boolean hasConnected = false;
		for(ServerInfo server : servers.values()){
			if(server.getChannel().id().asShortText().equals(channelId)){
				hasConnected = true;
				break;
			}
		}
		if(!hasConnected){
			WorldLoginReq.Builder request = WorldLoginReq.newBuilder();
			write(channelId, request.build());
		}
	}

	@Override
	public void disConnect(String channelId) {
		Iterator<Entry<Integer, ServerInfo>> it = servers.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ServerInfo> entry = it.next();
			if(!entry.getValue().getChannel().isOpen()){
				it.remove();
				// log
			}
		}
	}
}