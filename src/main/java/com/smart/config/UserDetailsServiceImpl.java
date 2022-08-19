package com.smart.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.repository.UserRepository;
import com.smart.entity.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository  repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//fetchng user  from databse
		
	User user = repo.getUserByUserName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("could not found user!!");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
	}

}
