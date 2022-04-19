package com.appPal.service;

import java.util.List;

import com.appPal.dto.UserDto;
import com.appPal.dto.search.UserSearchDto;

public interface UserService {
	public List<UserDto> selectUserList(UserSearchDto search);
}
