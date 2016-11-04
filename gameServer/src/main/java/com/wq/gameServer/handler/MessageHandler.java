package com.wq.gameServer.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.gameServer.GameStart;
import com.wq.gameServer.service.Service;

@Sharable
public class MessageHandler extends ChannelInboundMessageHandlerAdapter<protocol>{
	
	public List<String> serviceNames;
	public Map<String, Service> services = new HashMap<>();
	private Map<Integer, Channel> channels = new HashMap<>();
	private List<protocol> messages = new CopyOnWriteArrayList<>();
	Logger logger = LoggerFactory.getLogger("Logger");
	
	public MessageHandler(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(handleTask, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	private Runnable handleTask = new Runnable() {
		
		@Override
		public void run() {
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
					} catch (Exception e) {
						logger.error("handleTask : "+e.getMessage());
						e.printStackTrace();
						continue;
					}
				}
			}
		}
	};
	
	public void write(protocol msg){
		int toId = msg.getToId();
		if(!channels.containsKey(toId)){
			throw new NullPointerException();
		}
		Channel channel = channels.get(toId);
		if(!channel.isActive()){
			throw new IllegalStateException();
		}
		channel.write(msg);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, protocol msg)
			throws Exception {
		int fromId = msg.getFromId();
		channels.put(fromId, ctx.channel());
		messages.add(msg);
		
		// 同步问题，效率而准确的处理，CopyOnWriteArrayList和原子容器
	}
	
	public void loadServices(){
		for(String name : serviceNames){
			Service service = (Service)GameStart.context.getBean(name);
			services.put(name, service);
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(services == null || services.size() == 0){
			loadServices();
		}
		for(Service service : services.values()){
			service.activeService(ctx);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		for(Service service : services.values()){
			service.inactiveService(ctx);
		}
	}

	public List<String> getServiceNames() {
		return serviceNames;
	}

	public void setServiceNames(List<String> serviceNames) {
		this.serviceNames = serviceNames;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

}
