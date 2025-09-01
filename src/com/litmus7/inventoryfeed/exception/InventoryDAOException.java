package com.litmus7.inventoryfeed.exception;

public class InventoryDAOException extends Exception {
	
	private final String errorCode;
	
	public InventoryDAOException(String message, Throwable cause, String errorCode) {
		super(message,cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
}
