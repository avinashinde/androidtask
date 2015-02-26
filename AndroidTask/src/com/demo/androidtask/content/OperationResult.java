package com.demo.androidtask.content;

public class OperationResult {

	public static final int OPERATION_FAILED = 0;
	
	public static final int OPERATION_SUCCESSFUL = 1;
	
	private int result = OPERATION_FAILED;
	
	private String message = "";
	
	public OperationResult(int result) {
		this.result = result;
	}
	
	public OperationResult(int result, String message) {
		this.result = result;
		this.message = message;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
