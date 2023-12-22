package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

import jakarta.persistence.Id;

public interface userRepository  extends JpaRepository<User,Id>{

	@Query("select u from User u where u.email= :email")
	public User getUserByUserName(@Param("email") String email);

}
