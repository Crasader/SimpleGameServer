package com.wq.gameServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class GameStart {
	
	public ApplicationContext context;
	
	public GameStart(String springName){
		context = new FileSystemXmlApplicationContext(springName);
	}

	public static void main(String[] args){
		GameStart gs = new GameStart("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)gs.context.getBean("Server");
		server.run();
		Connector client = (Connector)gs.context.getBean("Client");
		client.run();
	}
	
}
