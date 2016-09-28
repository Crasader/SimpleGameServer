package com.wq.gameServer.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.gameServer.P1;
import com.wq.entity.protobuf.gameServer.P1.p1;
import com.wq.gameServer.service.Service;

public class MessageHandler extends ChannelInboundMessageHandlerAdapter<protocol>{
	
	private Map<Integer, Channel> channels = new HashMap<>();
	private List<protocol> messages = new CopyOnWriteArrayList<>();
	private Map<String, Service> services;
	
	public MessageHandler(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(handleTask, 0, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, protocol msg)
			throws Exception {
		System.out.println("get a message");
		int userId = msg.getFromId();
		channels.put(userId, ctx.channel());
		messages.add(msg);
		
		// 同步问题，效率而准确的处理
	}

	public void write(protocol msg){
		int userId = msg.getToId();
		if(!channels.containsKey(userId)){
			throw new NullPointerException();
		}
		Channel channel = channels.get(userId);
		if(!channel.isActive()){
			throw new IllegalStateException();
		}
		channel.write(msg);
	}
	
	private Runnable handleTask = new Runnable() {
		
		@Override
		public void run() {
			if(messages.size() <= 0){
				return;
			}
			List<protocol> tempMessages = new ArrayList<>();
			tempMessages.addAll(messages); // 需要合适的同步处理
			messages.clear();
			for(protocol msg : tempMessages){
				String serviceName = msg.getName().split("_")[0];
				if(services.containsKey(serviceName)){
					try {
						services.get(serviceName).service(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		p1.Builder p = p1.newBuilder();
		p.setName("111");
		protocol.Builder proto = protocol.newBuilder();
		proto.setName("LoginServiceImpl_login");
		proto.setFromId(1);
		proto.setExtension(P1.user, p.build());
		ctx.write(proto.build());
		super.channelActive(ctx);
	}

	public Map<String, Service> getServices() {
		return services;
	}

	public void setServices(Map<String, Service> services) {
		this.services = services;
	}


}