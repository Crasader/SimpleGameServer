package com.wq.gameServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wq.entity.server.ServerInfo;

public class Acceptor{

	private ServerInfo currentServer;
	private List<String> handlers;
	Logger logger = LoggerFactory.getLogger("Logger");
	
    public void run(){
    	try{
    		
    		// 线程池
    		EventLoopGroup bossGroup = new NioEventLoopGroup();
    		EventLoopGroup workerGroup = new NioEventLoopGroup();
    		
    		// 服务器参数设置
    		ServerBootstrap b = new ServerBootstrap();
    		b.localAddress(currentServer.getPort())
    		.group(bossGroup, workerGroup)
    		.channel(NioServerSocketChannel.class)
    		.childHandler(new ChannelInitializer<SocketChannel>() {
    			@Override
    			public void initChannel(SocketChannel ch) throws Exception {
    				for(String handlerName : getHandlers()){
    					ch.pipeline().addLast((ChannelHandler)GameStart.context.getBean(handlerName));
    				}
    			}
    		})
    		.option(ChannelOption.SO_BACKLOG, 128)
    		.childOption(ChannelOption.SO_KEEPALIVE, true);
    		
    		// 绑定端口
    		ChannelFuture f =  b.bind();
    		
    		// 绑定结果处理
    		f.addListener(new ChannelFutureListener() {
    			
    			@Override
    			public void operationComplete(ChannelFuture future) throws Exception {
    				if(future.isSuccess()){
    					logger.info("Bind to " + currentServer.getPort() + " succeed");
    				}else{
    					logger.error("Bind to " + currentServer.getPort() + " failed");
    					Thread.sleep(3000);
    					run();
    				}
    			}
    		});
    		
    		// 绑定关闭处理
    		f.channel().closeFuture().addListener(new ChannelFutureListener() {
    			
    			@Override
    			public void operationComplete(ChannelFuture future) throws Exception {
    				if(!future.isSuccess()){
    					logger.error("Unbind from " + currentServer.getPort() + "failed!");
    				}else{
    					logger.info("Unbind from " + currentServer.getPort() + "succeed");
    				}
    				future.channel().eventLoop().shutdown();
    			}
    		});
    	}catch(Exception e){
    		logger.error("run : " + e.getMessage());
    	}
    }
	
	public List<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}

	public ServerInfo getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(ServerInfo currentServer) {
		this.currentServer = currentServer;
	}

}
