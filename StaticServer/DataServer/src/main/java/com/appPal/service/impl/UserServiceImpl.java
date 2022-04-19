package com.appPal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appPal.dto.UserDto;
import com.appPal.dto.search.UserSearchDto;
import com.appPal.mapper.UserMapper;
import com.appPal.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper mapper;
	
	@Override
	public List<UserDto> selectUserList(UserSearchDto search) {
		// TODO Auto-generated method stub
		return mapper.selectUserList(search);
	}

}
