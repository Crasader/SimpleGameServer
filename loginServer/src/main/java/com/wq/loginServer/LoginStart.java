package com.wq.loginServer;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.wq.gameServer.Acceptor;
import com.wq.gameServer.Connector;
import com.wq.gameServer.GameStart;


public class LoginStart {

	public static void main(String[] args){
		GameStart.context = new FileSystemXmlApplicationContext("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)GameStart.context.getBean("Server");
		server.run();
		Connector client = (Connector)GameStart.context.getBean("Client");
		client.run();
	}
	
}
