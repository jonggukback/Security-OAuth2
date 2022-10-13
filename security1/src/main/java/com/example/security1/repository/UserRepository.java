package com.example.security1.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.example.security1.model.User;

@Mapper()
public interface UserRepository {
	
	//@Insert("INSERT INTO users (ID, USERNAME, PASSWORD, EMAIL) values (USERS_SEQ.nextval, #{username}, #{password}, #{email})")
	public void save(User user);

	public User findByUsername(String username);
}
