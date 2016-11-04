package com.wq.worldServer;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.wq.gameServer.Acceptor;
import com.wq.gameServer.GameStart;


public class WorldStart {

	public static void main(String[] args){
		GameStart.context = new FileSystemXmlApplicationContext("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)GameStart.context.getBean("Server");
		server.run();
	}
	
}
