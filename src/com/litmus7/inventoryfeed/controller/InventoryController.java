package com.litmus7.inventoryfeed.controller;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.litmus7.inventoryfeed.constant.MessageConstants;
import com.litmus7.inventoryfeed.dto.Response;
import com.litmus7.inventoryfeed.exception.InventoryServiceException;
import com.litmus7.inventoryfeed.service.InventoryService;
import com.litmus7.inventoryfeed.util.ErrorMessageUtil;

public class InventoryController {
	
	private final InventoryService inventoryService = new InventoryService();
	
	private final static Logger logger = LogManager.getLogger(InventoryController.class);
	
	public Response<Void,Boolean,String> processAllFiles(String inputDirectoryPath,String processedDirectoryPath,String errorDirectoryPath) {
		
		logger.trace("Entering processAllFiles()");
		
		if ( !Files.exists(Paths.get(inputDirectoryPath)) || !Files.isDirectory(Paths.get(inputDirectoryPath)) ) {
			logger.error("Message: {}",MessageConstants.ERROR_CONTROLLER_INVALID_INPUT_DIRECTORY);
			return new Response<>(false,MessageConstants.ERROR_CONTROLLER_INVALID_INPUT_DIRECTORY);
		}
		if ( !Files.exists(Paths.get(processedDirectoryPath)) || !Files.isDirectory(Paths.get(processedDirectoryPath)) ) {
			logger.error("Message: {}",MessageConstants.ERROR_CONTROLLER_INVALID_PROCESSED_DIRECTORY);
			return new Response<>(false,MessageConstants.ERROR_CONTROLLER_INVALID_PROCESSED_DIRECTORY);
		}
		if ( !Files.exists(Paths.get(errorDirectoryPath)) || !Files.isDirectory(Paths.get(errorDirectoryPath)) ) {
			logger.error("Message: {}",MessageConstants.ERROR_CONTROLLER_INVALID_ERROR_DIRECTORY);
			return new Response<>(false,MessageConstants.ERROR_CONTROLLER_INVALID_ERROR_DIRECTORY);
		}
		
		int processedCount=0;
		
		try {
			processedCount = inventoryService.createInventory(inputDirectoryPath, processedDirectoryPath, errorDirectoryPath);
		} catch (InventoryServiceException e) {
			logger.error("Messages: {}",ErrorMessageUtil.getMessage(e.getErrorCode()));
			return new Response<>(false,ErrorMessageUtil.getMessage(e.getErrorCode()));
		} catch (Exception e) {
			logger.error("Messages: {}",MessageConstants.ERROR_MESSAGE);
			e.printStackTrace();
			return new Response<>(false,MessageConstants.ERROR_MESSAGE);
		}
		
		
		
		if (processedCount>0) {
			logger.info(MessageConstants.FILE_PROCESSED_SUCCESSFULLY, processedCount);
			return new Response<>(true,"Processed file(s) successfully: " + processedCount);
		}
		
		logger.warn("No files processed");
		
		logger.trace("Exiting processAllFiles()");
		
		return new Response<>(false,"Failed to process any files");
	}
	
}
