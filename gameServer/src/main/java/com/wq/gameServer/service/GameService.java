package com.wq.gameServer.service;

import java.lang.reflect.Method;

import org.springframework.aop.framework.AopContext;

import com.google.protobuf.ExtensionLite;
import com.google.protobuf.Message;
import com.wq.entity.protobuf.Protocol;
import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.entity.protobuf.server.ServerLogin;
import com.wq.entity.protobuf.server.ServerLogin.WorldLoginReq;
import com.wq.entity.server.ServerInfo;
import com.wq.gameServer.handler.IMessageHandler;

public abstract class GameService implements IGameService{
	
	public ServerInfo localServer;
	IMessageHandler handler;
	
	@Override
	public void service(protocol msg) throws NoSuchMethodException, SecurityException {
		String msgName = msg.getName();
		if(msgName == null || "".equals(msgName)){
			throw new NullPointerException();
		}
		String[] names = msgName.split("_");
		if(names.length < 2){
			throw new IllegalArgumentException();
		}
		String serviceName = this.getClass().getSimpleName();
		if(!names[0].equals(serviceName)){
			throw new IllegalArgumentException();
		}
		Method method = this.getClass().getMethod(names[1], protocol.class);
		
		// 反射调用方法，这里第一个参数是实例对象，由于使用了AOP代理，这里不用this，而是获取代理的对象
		try {
			method.invoke(AopContext.currentProxy(), msg);
//			method.invoke(this, msg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void write(String targetId, Message msg) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException{
		String[] names = msg.getClass().getName().split("\\.");
		String className = names[names.length-1].split("\\$")[0];
		className = msg.getClass().getPackage().getName() + "." + className;
		String fieldName = names[names.length-1].split("\\$")[1];
		protocol.Builder message = protocol.newBuilder();
		message.setName(fieldName);
		message.setFromId(localServer.getId()+"");
		message.setToId(targetId);
		fieldName = fieldName.substring(0,1).toLowerCase() + fieldName.substring(1);
		message.setExtension((ExtensionLite<Protocol.protocol,Message>)Class.forName(className).getField(fieldName).get(msg), msg);
		handler.write(message.build());
	}

	public IMessageHandler getHandler() {
		return handler;
	}

	public void setHandler(IMessageHandler handler) {
		handler.regist(this);
		this.handler = handler;
	}

	public ServerInfo getLocalServer() {
		return localServer;
	}

	public void setLocalServer(ServerInfo localServer) {
		this.localServer = localServer;
	}

}
