package com.appPal.main.dto;

import java.util.HashMap;

public class ResultDto {
	private int status;
	private String message;
	private HashMap<String, Object> data;
	
	public ResultDto() {
		this.status = 0;
		this.message = "";
		this.data = new HashMap<String, Object>();
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public HashMap<String, Object> getData() {
		return data;
	}
	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}
	public void addData(String key, Object value) {
		this.data.put(key, value);
	}
	
}
