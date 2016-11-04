package com.wq.gameServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class GameStart {

	public static ApplicationContext context;
	
	public static void main(String[] args){
		context = new FileSystemXmlApplicationContext("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)context.getBean("Server");
		server.run();
		Connector client = (Connector)context.getBean("Client");
		client.run();
	}
	
}
