package com.wq.gameServer.service;

import io.netty.channel.ChannelHandlerContext;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LogService extends Service{

	Logger logger = LoggerFactory.getLogger("Logger");
	
	@Pointcut("execution(* com.wq.gameServer.service.*.*(..))")
	public void serviceAspect(){
		
	}
	
	@Around("serviceAspect()")
	public void around(JoinPoint joinPoint){
		try{
			logger.debug("[Before]"+joinPoint.getTarget()+" : "+joinPoint.toShortString());
			Object[] args = joinPoint.getArgs();
			for(Object arg : args){
				logger.debug("[arg]"+arg);
			}
			Object result = ((ProceedingJoinPoint)joinPoint).proceed();
			logger.debug("[After]"+joinPoint.getTarget()+" : "+joinPoint.toShortString());
			if(result != null){
				logger.debug("[Return]"+result);
			}
		}catch(Throwable e){
			logger.error(joinPoint.getTarget()+" : "+e.getMessage());
		} 
	}

	@Override
	public void activeService(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void inactiveService(ChannelHandlerContext ctx) {
		
	}
}
