package com.appPal.main.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.appPal.main.dto.UserDto;
import com.appPal.main.dto.UserSearchDto;
import com.appPal.main.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<UserDto> selectUserList(HttpServletRequest req) {
		UserSearchDto search = new UserSearchDto();
		search.setEmail(req.getParameter("email"));
		search.setOrderBy(req.getParameter("orderBy"));
		return service.selectUserList(search);
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public UserSearchDto test(HttpServletRequest req) {
		UserSearchDto search = new UserSearchDto();
		search.setEmail(req.getParameter("email"));
		search.setOrderBy(req.getParameter("orderBy"));
		return search;
	}
	
}
