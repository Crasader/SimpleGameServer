package com.wq.gameServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class GameStart {

	public static ApplicationContext context = new FileSystemXmlApplicationContext("src/main/resources/springGS.xml");
	static Logger logger = LoggerFactory.getLogger("Logger");
	
	public void start(RunnableServer server) throws InterruptedException{
		while(true){
			try{
				server.run();
				logger.info(server.getName() + " start succeed");
				break;
			}catch(Exception e){
				logger.error(server.getName() + " start failed : "+e.getMessage()+", try again...");
				Thread.sleep(3000);
				continue;
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		GameStart starter = new GameStart();
		RunnableServer gameServer = (RunnableServer)context.getBean("Server");
		starter.start(gameServer);
		RunnableServer gameClient = (RunnableServer)context.getBean("Client");
		starter.start(gameClient);
	}
	
	interface RunnableServer{
		public void run() throws Exception;
		public String getName();
	}
}
