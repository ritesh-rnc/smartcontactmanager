package com.smart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByuser(@Param("userId")int userId,Pageable pageble);

}
