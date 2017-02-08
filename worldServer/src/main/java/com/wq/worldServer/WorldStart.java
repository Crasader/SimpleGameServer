package com.wq.worldServer;

import com.wq.gameServer.Acceptor;
import com.wq.gameServer.GameStart;


public class WorldStart extends GameStart{
	
	public WorldStart(String springName){
		super(springName);
	}
	
	public static void main(String[] args){
		WorldStart ws = new WorldStart("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)ws.context.getBean("Server");
		server.run();
	}
	
}
