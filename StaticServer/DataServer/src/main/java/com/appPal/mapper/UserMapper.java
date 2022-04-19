package com.appPal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.appPal.dto.UserDto;
import com.appPal.dto.search.UserSearchDto;

public interface UserMapper {
	public List<UserDto> selectUserList(@Param("search") UserSearchDto search);
}
