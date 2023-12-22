package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.userRepository;
import com.smart.entities.User;

public class userDeatailsServiceImpl implements UserDetailsService {

	@Autowired
	private userRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user= userRepository.getUserByUserName(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("could found user !!");
		}
		
		customUserDetails customUserDetails=new customUserDetails(user);
		
		return customUserDetails;
	}

}
