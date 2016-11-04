package com.wq.gameServer.service;

import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

import org.springframework.aop.framework.AopContext;

import com.wq.entity.protobuf.Protocol.protocol;
import com.wq.gameServer.handler.MessageHandler;

public abstract class Service{
	
	MessageHandler messageHandler;
	
	public void service(protocol msg) throws Exception {
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
		method.invoke(AopContext.currentProxy(), msg);
//		method.invoke(this, msg);
	}
	
	public abstract void activeService(ChannelHandlerContext ctx);
	
	public abstract void inactiveService(ChannelHandlerContext ctx);
	
	public void write(protocol msg){
		messageHandler.write(msg);
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

}
