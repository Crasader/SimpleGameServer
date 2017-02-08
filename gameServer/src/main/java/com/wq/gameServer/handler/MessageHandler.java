package com.wq.gameServer.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.gameServer.service.IGameService;

@Sharable
public class MessageHandler extends ChannelInboundHandlerAdapter implements IMessageHandler{

	private Map<String, IGameService> services = new ConcurrentHashMap<>();
	private List<protocol> messages = new CopyOnWriteArrayList<>();
	private ChannelHandlerContext ctx;
	
	public MessageHandler(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(handleTask, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void regist(IGameService service) {
		services.put(service.getClass().getSimpleName(), service);
	}

	@Override
	public void notify(Object obj) {
		if(obj instanceof protocol){
			messages.add((protocol)obj);
		}else if(obj instanceof Channel){
			Channel channel = (Channel)obj;
			if(channel.isActive()){
				for(IGameService service : services.values()){
					try {
						service.connect(channel.id().asShortText());
					} catch (NumberFormatException e) {
						ctx.fireExceptionCaught(e);
					} catch (IllegalArgumentException e) {
						ctx.fireExceptionCaught(e);
					} catch (IllegalAccessException e) {
						ctx.fireExceptionCaught(e);
					} catch (NoSuchFieldException e) {
						ctx.fireExceptionCaught(e);
					} catch (SecurityException e) {
						ctx.fireExceptionCaught(e);
					} catch (ClassNotFoundException e) {
						ctx.fireExceptionCaught(e);
					}
				}
			}else{
				for(IGameService service : services.values()){
					service.disConnect(channel.id().asShortText());
				}
			}
		}
	}
	
	@Override
	public void write(Object obj) {
		ctx.fireUserEventTriggered(obj);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		notify(msg);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		notify(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		notify(ctx.channel());
	}

	private Runnable handleTask = new Runnable() {
		
		@Override
		public void run(){
			if(messages.size() <= 0){
				return;
			}
			List<protocol> tempMessages = new ArrayList<>();
			tempMessages.addAll(messages); // 同上，需要合适的同步处理
			messages.clear();
			for(protocol msg : tempMessages){
				String serviceName = msg.getName().split("_")[0];
				if(services.containsKey(serviceName)){
					try {
						services.get(serviceName).service(msg);
					} catch (NoSuchMethodException e) {
						ctx.fireExceptionCaught(e);
					} catch (SecurityException e) {
						ctx.fireExceptionCaught(e);
					}
				}					
			}
		}
	};

}
