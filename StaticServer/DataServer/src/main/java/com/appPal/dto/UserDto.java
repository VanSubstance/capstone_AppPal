package com.appPal.dto;

public class UserDto {
	private String memberkey;
	private String password;
	private String regDate;
	private String lastLoginDate;
	private String email;
	private String status; // 01: Α€»σ; 95: °­Επ; 99: Ε»Επ

	public String getMemberkey() {
		return memberkey;
	}

	public void setMemberkey(String memberkey) {
		this.memberkey = memberkey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
