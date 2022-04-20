package com.appPal.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "어서와 ㅡ 모두 너를 기다려";
	}
	
	@GetMapping("/error")
	public String error() {
		return "에러페이지다맨이야";
	}
}
