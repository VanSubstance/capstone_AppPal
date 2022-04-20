package com.appPal.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appPal.main.dto.UserDto;
import com.appPal.main.dto.UserSearchDto;
import com.appPal.main.mapper.UserMapper;
import com.appPal.main.service.UserService;

@Service
public class UserService {

	@Autowired
	private UserMapper mapper;
	
	public List<UserDto> selectUserList(UserSearchDto search) {
		// TODO Auto-generated method stub
		return mapper.selectUserList(search);
	}

}
