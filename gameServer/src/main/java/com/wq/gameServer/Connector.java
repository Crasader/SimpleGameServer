package com.wq.gameServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wq.entity.server.ServerInfo;
import com.wq.gameServer.util.SpringUtil;

public class Connector{
	
	private ServerInfo serverToConnect;
	private List<String> handlers;
	Logger logger = LoggerFactory.getLogger("Logger");

	public void run(){
		try{
			
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			
			Bootstrap b = new Bootstrap();
			b.remoteAddress(serverToConnect.getIp(), serverToConnect.getPort());
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					for(String handlerName : getHandlers()){
						ch.pipeline().addLast((ChannelHandler)SpringUtil.context.getBean(handlerName));
					}
				}
			});
			
			// 启动连接
			ChannelFuture f = b.connect();
			
			// 连接结果处理
			f.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()){
						logger.info("Connect to " + serverToConnect.getName() + " succeed");
					}else{
						logger.error("Connect to " + serverToConnect.getName() + " failed");
						Thread.sleep(3000);
						run();
					}
				}
			});
			
			// 连接关闭处理
			f.channel().closeFuture().addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(!future.isSuccess()){
						logger.error("Disconnect from " + serverToConnect.getName() + " failed");
					}else{
						logger.info("Disconnect from " + serverToConnect.getName() + " succeed");
					}
					future.channel().eventLoop().shutdownGracefully();
				}
			});
		}catch(Exception e){
			logger.error("run : " + e.getMessage());
			run();
		}
	}
	
	public List<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}

	public ServerInfo getServerToConnect() {
		return serverToConnect;
	}

	public void setServerToConnect(ServerInfo serverToConnect) {
		this.serverToConnect = serverToConnect;
	}

}
