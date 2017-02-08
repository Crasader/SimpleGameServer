package com.wq.gameServer.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wq.entity.protobuf.Protocol.protocol;

@Sharable
public class ChannelHandler extends ChannelInboundHandlerAdapter{
	
	private List<Channel> unknownChannels = new ArrayList<>();
	private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj)
			throws Exception {
		if(obj instanceof protocol){
			protocol msg = (protocol)obj;
			String fromId = msg.getFromId();
			channels.put(fromId, ctx.channel());
		}else{
			unknownChannels.add(ctx.channel());
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof protocol){
			protocol msg = (protocol)evt;
			String toId = msg.getToId();
			if(channels.containsKey(toId)){
				channels.get(toId).write(msg);
			}
		}
	}
}
