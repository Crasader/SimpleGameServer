package com.wq.gameServer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

public class LogHandler extends ChannelInboundMessageHandlerAdapter<Message>{
	
	Logger logger = LoggerFactory.getLogger("Logger");

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		logger.info("MessageReceived");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		try{
			logger.error("LogHandler.exceptionCaught : "+cause.getMessage());
		}catch(Exception e){
			
		}
	}

}
