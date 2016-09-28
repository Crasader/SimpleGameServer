package com.wq.database.gameManager;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.wq.entity.mould.User;

@Repository
public interface UserDao {

	public List<User> query();
}
