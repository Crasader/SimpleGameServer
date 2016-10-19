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

import com.wq.gameServer.GameStart.RunnableServer;

public class Server implements RunnableServer{

	private String name;
	private int port;
	private List<String> handlers;
	Logger logger = LoggerFactory.getLogger("Logger");
	
    public void run() throws Exception {
    	
    	// 线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        // 服务器参数设置
        ServerBootstrap b = new ServerBootstrap();
        b.localAddress(port)
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
        ChannelFuture f =  b.bind().sync();
        
        // 连接关闭处理
        f.channel().closeFuture().addListener(new ChannelFutureListener() {
        	
        	@Override
        	public void operationComplete(ChannelFuture future) throws Exception {
        		if(!future.isSuccess()){
        			logger.error("Close " + getName() + "failed!");
        		}else{
        			logger.info("Close " + getName() + "succeed");
        		}
        		future.channel().eventLoop().shutdown();
        	}
        });
    }
    
    public String getName() {
    	if(name != null && !"".equals(name)){
    		return name;
    	}
    	return "Localhost : " + port;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

	public List<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
