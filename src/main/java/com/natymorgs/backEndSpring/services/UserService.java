package com.natymorgs.backEndSpring.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.natymorgs.backEndSpring.security.UserSS;

public class UserService {

	public static UserSS authenticaded() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}
}
