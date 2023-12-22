package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.contact;

public interface contactRepository extends JpaRepository<contact, Integer> {

	// pagination
	// crrent page -page
	// contact per page-
	
	@Query("from contact as c where c.user.id=:userId")
    public Page<contact> findContactsByUser(@Param("userId") int user_id,Pageable pageable);
}
