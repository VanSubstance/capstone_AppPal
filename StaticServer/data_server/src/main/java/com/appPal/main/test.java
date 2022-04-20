package com.appPal.main;

import com.appPal.main.util.UtilFunction;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("테스트");
		String encryptedString = UtilFunction.testSHA256("테스트");
		System.out.println(encryptedString);
		System.out.println("멤버키");
		System.out.println(UtilFunction.generateUuidV4());
	}

}
