package com.wq.loginServer;

import com.wq.gameServer.Acceptor;
import com.wq.gameServer.Connector;
import com.wq.gameServer.GameStart;


public class LoginStart extends GameStart{
	
	public LoginStart(String springName){
		super(springName);
	}

	public static void main(String[] args){
		LoginStart ls = new LoginStart("src/main/resources/springGS.xml");
		Acceptor server = (Acceptor)ls.context.getBean("Server");
		server.run();
		Connector client = (Connector)ls.context.getBean("Client");
		client.run();
	}
	
}
