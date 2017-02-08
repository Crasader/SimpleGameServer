package com.wq.gameServer.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import com.wq.entity.protobuf.Protocol.protocol;

@Sharable
public class IdleHandler extends ChannelInboundHandlerAdapter{
	
	public String keepAliveName = "keepAlive";

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent state = (IdleStateEvent)evt;
			if(state.state() == IdleState.READER_IDLE){
				ctx.channel().close();
			}else{
				protocol.Builder message = protocol.newBuilder();
				message.setName(keepAliveName);
				ctx.channel().write(message.build());
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}

	public String getKeepAliveName() {
		return keepAliveName;
	}

	public void setKeepAliveName(String keepAliveName) {
		this.keepAliveName = keepAliveName;
	}

	
}
