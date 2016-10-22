package com.wq.worldServer.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.WorldLogin;
import com.wq.entity.protobuf.WorldLogin.WorldLoginReq;
import com.wq.entity.server.Server;
import com.wq.gameServer.service.Service;

public class WorldLoginService extends Service{

	private Map<Integer, Server> servers = new HashMap<>();
	
	public void login(Channel channel, protocol msg){
		WorldLoginReq request = msg.getExtension(WorldLogin.worldLoginReq);
		if(request == null){
			throw new NullPointerException("Null when need a worldLoginReq");
		}
		Integer serverId = request.getId();
		if(servers.containsKey(serverId)){
			Server server = servers.get(serverId);
			server.setUpdateTime(new Date());
			server.setOnlineCount(request.getOnlineCount());
		}else{
			Server server = new Server();
			server.setId(serverId);
			server.setName(request.getName());
			server.setType(request.getType());
			server.setOnlineCount(request.getOnlineCount());
			server.setUpdateTime(new Date());
			channel.closeFuture().addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					channelCloseHandler();
				}
			});
		}
	}
	
	public void channelCloseHandler(){
		Iterator<Entry<Integer, Server>> it = servers.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Server> entry = it.next();
			if(!entry.getValue().getChannel().isOpen()){
				it.remove();
				// log
			}
		}
	}
}
