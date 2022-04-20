package com.appPal.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.appPal.main.dto.UserDto;
import com.appPal.main.dto.UserSearchDto;

@Mapper
public interface UserMapper {
	public List<UserDto> selectUserList(@Param("search") UserSearchDto search);
}
