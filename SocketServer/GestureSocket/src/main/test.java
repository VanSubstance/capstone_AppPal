package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class test {

	public static void main(String[] args) {
		System.out.println("Recognizer is executed...");
		String pythonExe = System.getenv("PYTHON_LOCATION");
		String recognizerLocation = "..\\pythonModule\\a.py";
		ProcessBuilder builder = new ProcessBuilder(pythonExe, recognizerLocation, "3", "5");
		Process process;
		try {
			process = builder.start();
			int exitVal = process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8")); // 서브 프로세스가 출력하는 내용을 받기 위해
			String line;
			while ((line = br.readLine()) != null) {
			     System.out.println(">>>  " + line); // 표준출력에 쓴다
			}
			if(exitVal != 0) {
			  // 비정상 종료
			  System.out.println("Python is terminated with exception.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Recognizer is terminated...");
	}

}
