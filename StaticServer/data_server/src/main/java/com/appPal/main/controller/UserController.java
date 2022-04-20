package com.appPal.main.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appPal.main.dto.ResultDto;
import com.appPal.main.dto.UserDto;
import com.appPal.main.dto.UserSearchDto;
import com.appPal.main.service.UserService;
import com.appPal.main.util.UtilFunction;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService service;

	@GetMapping("")
	public ResultDto selectUserList(HttpServletRequest req) {
		ResultDto res = new ResultDto();
		UserSearchDto search = new UserSearchDto();
		search.setEmail(req.getParameter("email"));
		search.setOrderBy(req.getParameter("orderBy"));
		res.addData("userList", service.selectUserList(search));
		return res;
	}

	@PostMapping("")
	public ResultDto signup(@RequestBody UserDto newUser) {
		ResultDto res = new ResultDto();
		boolean checkDuplicated = service.selectUserList(new UserSearchDto(newUser.getEmail())).size() > 0;
		if (checkDuplicated) {
			res.setStatus(1);
			res.setMessage("This email is already signed up.");
		} else {
			newUser.setMemberKey(UtilFunction.generateUuidV4());
			service.insertUser(newUser);
		}
		return res;
	}

	@PatchMapping("/login")
	public ResultDto login(@RequestBody UserDto loginUser) {
		ResultDto res = new ResultDto();

		List<String> searchResult = service.selectMemberKey(loginUser);
		if (searchResult.size() > 0) {
			String memberKey = searchResult.get(0);
			res.addData("memberKey", memberKey);
			loginUser.setMemberKey(memberKey);
			service.refreshUser(loginUser);
		} else {
			res.setStatus(1);
			res.setMessage("Wrong email or password.");
		}
		return res;
	}

}
