package com.wq.database.mybatis;


import java.util.List;

import com.wq.entity.mould.User;

public interface UserDao {

	public List<User> query();
}
