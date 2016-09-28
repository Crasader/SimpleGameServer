package com.wq.gameServer.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LogService extends AbstractService{

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Pointcut("execution(* com.wq.gameServer.service.*.*(..))")
	public void serviceAspect(){
		
	}
	
	@Before("serviceAspect()")
	public void before(JoinPoint joinPoint){
		log.debug("joinPoint : "+joinPoint);
		log.debug("args : "+joinPoint.getArgs()[0]);
		log.debug("location : "+joinPoint.getSourceLocation());
		log.debug("target : "+joinPoint.getTarget());
		log.debug("kind : "+joinPoint.getKind());
	}
	
	@AfterReturning(pointcut="serviceAspect()",returning="result")
	public void afterReturning(JoinPoint joinPoint, Object result){
		if(result == null){
			log.debug("AfterReturning ");
		}else{
			log.debug("return : "+result);
		}
	}
	
	@After("serviceAspect()")
	public void after(JoinPoint joinPoint){
		log.debug("After"+joinPoint.getTarget());
	}
	
	@AfterThrowing(pointcut="serviceAspect()", throwing="e")
	public void afterThrowing(JoinPoint joinPoint, Exception e){
		log.error("Exception : "+e);
		log.error("trace : "+e.fillInStackTrace());
	}
	
	@Around("serviceAspect()")
	public void around(JoinPoint joinPoint){
//		try{
//			((ProceedingJoinPoint)joinPoint).proceed();
//		}catch(Throwable e){
//			
//		} 
	}
}
