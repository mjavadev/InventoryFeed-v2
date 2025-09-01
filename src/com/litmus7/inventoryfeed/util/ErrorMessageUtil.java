package com.litmus7.inventoryfeed.util;

import java.util.HashMap;
import java.util.Map;

import com.litmus7.inventoryfeed.constant.MessageConstants;

public class ErrorMessageUtil {
	
	private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();
	
	static {
		
		ERROR_MESSAGES.put(MessageConstants.ERROR_CODE_DB_SAVE_INVENTORY, MessageConstants.ERROR_UI_INV_DB_001);
		ERROR_MESSAGES.put(MessageConstants.ERROR_CODE_DB_ROLLBACK, MessageConstants.ERROR_UI_INV_DB_002);

		
		ERROR_MESSAGES.put(MessageConstants.ERROR_CODE_SERVICE_CSV_FILE_READ, MessageConstants.ERROR_UI_INV_SRV_001);
		ERROR_MESSAGES.put(MessageConstants.ERROR_CODE_SERVICE_INPUT_DIRECTORY_READ, MessageConstants.ERROR_UI_INV_SRV_002);
	}
	
	public static String getMessage(String errorCode) {
		return ERROR_MESSAGES.getOrDefault(errorCode, MessageConstants.ERROR_MESSAGE);
	}

}
