package com.capstone.apppal.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.vecmath.Vector3f;

public class CommonFunctions {
  public static boolean isTriangleAnglesOk(Vector3f a, Vector3f b, Vector3f c) {
    Vector3f calc = new Vector3f();
    calc.sub(a, b);
    float disAB = calc.length();
    calc = new Vector3f();
    calc.sub(b, c);
    float disBC = calc.length();
    calc = new Vector3f();
    calc.sub(c, a);
    float disCA = calc.length();
    float radA = (float) Math.acos((disAB * disAB + disCA * disCA - disBC * disBC) / (2 * disAB * disCA)) / (float) Math.PI;
    if (radA <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
      float radB = (float) Math.acos((disAB * disAB + disBC * disBC - disCA * disCA) / (2 * disAB * disBC)) / (float) Math.PI;
      if (radB <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
        float radC = (float) Math.acos((disBC * disBC + disCA * disCA - disAB * disAB) / (2 * disBC * disCA)) / (float) Math.PI;
        if (radC <= GlobalState.MAXIMUM_RADIAN_FOR_DRAWING) {
          return true;
        }
      }
    }
    return false;
  }

  public static String Encrypted(String password, String roomCode) {
    String result = "";
    String input = password + roomCode;
    try {
			/* MessageDigest 클래스의 getInstance() 메소드의 매개변수에 "SHA-256" 알고리즘 이름을 지정함으로써
				해당 알고리즘에서 해시값을 계산하는 MessageDigest를 구할 수 있다 */
      MessageDigest mdSHA256 = MessageDigest.getInstance("SHA-256");

      // 데이터(패스워드+ email)를 한다. 즉 '암호화'와 유사한 개념
      mdSHA256.update(input.getBytes("UTF-8"));

      // 바이트 배열로 해쉬를 반환한다.
      byte[] sha256Hash = mdSHA256.digest();

      // StringBuffer 객체는 계속해서 append를 해도 객체는 오직 하나만 생성된다. => 메모리 낭비 개선
      StringBuffer hexSHA256hash = new StringBuffer();

      // 256비트로 생성 => 32Byte => 1Byte(8bit) => 16진수 2자리로 변환 => 16진수 한 자리는 4bit
      for (byte b : sha256Hash) {
        String hexString = String.format("%02x", b);
        hexSHA256hash.append(hexString);
      }
      result = hexSHA256hash.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }
}
