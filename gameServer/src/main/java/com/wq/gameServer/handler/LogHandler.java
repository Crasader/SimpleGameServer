package com.wq.gameServer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;

public class LogHandler extends LoggingHandler{
	
	Logger logger = LoggerFactory.getLogger("Logger");
	PrintStream ps = null;
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(logger instanceof ch.qos.logback.classic.Logger){
			ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger)logger;
			Iterator<Appender<ILoggingEvent>> it = logbackLogger.iteratorForAppenders();
			for(;it.hasNext();){
				Appender<ILoggingEvent> appender = it.next();
				if(appender instanceof RollingFileAppender){
					RollingFileAppender<ILoggingEvent> rollingAppender = (RollingFileAppender<ILoggingEvent>)appender;
					OutputStream os = rollingAppender.getOutputStream();
					ps = new PrintStream(os);
					break;
				}
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(ps != null){
			cause.printStackTrace(ps);
		}else{
			logger.error(cause.getMessage());
		}
	}

}
