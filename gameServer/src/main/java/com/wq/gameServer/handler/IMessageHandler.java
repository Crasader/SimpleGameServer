package com.wq.gameServer.handler;

import com.wq.gameServer.service.IGameService;

public interface IMessageHandler {

	public void regist(IGameService service);
	public void notify(Object obj);
	public void write(Object obj);
}
