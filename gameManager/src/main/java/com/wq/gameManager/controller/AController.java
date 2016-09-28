package com.wq.gameManager.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wq.database.gameManager.UserDao;
import com.wq.entity.mould.User;
import com.wq.gameManager.service.UserService;

@Controller
public class AController {
	
	@Resource
	UserDao userDao;
	
	@Resource
	UserService userService;
	
	@RequestMapping("hello/{name}")
	public String hello(@ModelAttribute("content") String content,@PathVariable("name") String name){
		System.out.println(name);
		content = name;
		return "succees";
	}
	
	@RequestMapping("session")
	public String getSession(@ModelAttribute("content")User content){
		content.setName(userDao.query().get(0).getName());
		System.out.println("model:"+content.getName());
		userService.query();
		return "Hello";
	}
}
