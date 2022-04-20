package com.appPal.main.dto;

public class UserSearchDto {
	private String email;
	private String orderBy;// 'regDate' | 'lastLoginDate'
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
