package com.wq.database.redis.gameServer;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.wq.entity.mould.User;

public class RedisUserDao {

	@Resource
	StringRedisTemplate redis;
	
	public void insert(User user){
		redis.opsForHash().put("user:"+user.getId(), "name", user.getName());
	}
	
	public String query(User user){
		return (String)redis.opsForHash().get("user:"+user.getId(), "name");
	}
}
