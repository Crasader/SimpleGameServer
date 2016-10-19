package com.wq.gameServer.service;

import java.lang.reflect.Method;

import org.springframework.aop.framework.AopContext;

import com.wq.entity.protobuf.Protocol.protocol;

public class Service{
	
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
	}

}
