package com.litmus7.inventoryfeed.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.litmus7.inventoryfeed.constant.MessageConstants;
import com.litmus7.inventoryfeed.dao.InventoryDAO;
import com.litmus7.inventoryfeed.dto.Inventory;
import com.litmus7.inventoryfeed.exception.InventoryDAOException;
import com.litmus7.inventoryfeed.exception.InventoryServiceException;

public class InventoryService {
	
	private final InventoryDAO inventoryDAO = new InventoryDAO();
	
	private final static Logger logger = LogManager.getLogger(InventoryService.class);
	
	public int createInventory(String inputDirectoryPath,String processedDirectoryPath,String errorDirectoryPath) throws InventoryServiceException {
		
		logger.trace("Entering createInventory()");
		
		List<Path> csvFiles = new ArrayList<>();
		
		try {
			csvFiles = findCsvFiles(inputDirectoryPath);
		} catch (IOException e) {
			logger.error("Error reading input directory: {}", e.getMessage());
			throw new InventoryServiceException(MessageConstants.ERROR_SERVICE_INPUT_DIRECTORY_READ, e, MessageConstants.ERROR_CODE_SERVICE_INPUT_DIRECTORY_READ);
		}
		
		int successfulInventoriesCreation = 0;
		         
		for (Path file : csvFiles) {  
			
			boolean flag = true;
			
			try {
				
				List<Inventory> inventories =  readCsvFile(file);  
				
				for (Inventory inventory : inventories) { 
					try {
						int inventoriesAdded = inventoryDAO.saveInventory(inventory);
					} catch (InventoryDAOException e) {
						logger.error("Error code: {} Message: {}",e.getErrorCode(),MessageConstants.ERROR_SERVICE_SAVE_INVENTORY,e);
						flag = false;
						break; 
					}
				}
				
				if (flag) {
					successfulInventoriesCreation+=1;
					
					logger.info("Inventory created successfully for file: {}",file.getFileName());
					
					try {
						Files.move(file, 
								Paths.get(processedDirectoryPath).resolve(file.getFileName()),
								StandardCopyOption.REPLACE_EXISTING);
						logger.info("File moved to processed folder: {}", file.getFileName());
					} catch (IOException e) {
						logger.error("Failed to move file to processed folder: {}",file.getFileName());
					}
					
				}
				else {
					logger.error("Inventory creation failed for file: {}",file.getFileName());
					try {
						
						Files.move(file, 
						Paths.get(errorDirectoryPath).resolve(file.getFileName()),
						StandardCopyOption.REPLACE_EXISTING);
						
						logger.info("File moved to error folder: {}",file.getFileName());
						
					} catch (IOException e) {
						logger.error("Failed to move file to error folder: {}",file.getFileName(),e);
					}
				}
				
			} catch (IOException e) {
				logger.error("In Service layer: Csv file read operaion failed for {}",file.getFileName(),e);
			} 
			
		}
		
		if (successfulInventoriesCreation>0) 
			logger.info("Inventory creation successful for {} file(s)", successfulInventoriesCreation);
		else
			logger.warn("Inventory creation completely failed");
		
		logger.trace("Exiting createInventory()");
		return successfulInventoriesCreation;
	}
	
	
	
	//helper methods for service layer
	private List<Path> findCsvFiles(String directoryPath) throws IOException{
		
		List<Path> csvFiles = new ArrayList<>();  
		
		Path inputDirectory = Paths.get(directoryPath);  
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(inputDirectory,"*.csv")){
			
			for (Path file : directoryStream) {
				if (Files.isRegularFile(file)) {
					csvFiles.add(file);
					logger.trace("In findCsvFiles(): Csv file found: " + file);
				}
			}
		}
		return csvFiles;
	}
	
	private List<Inventory> readCsvFile (Path filePath) throws IOException {
		
		try (BufferedReader bufferedReader = Files.newBufferedReader(filePath)) {
			
			List<Inventory> inventories = new ArrayList<>();
			
			String line= bufferedReader.readLine();
			
			while ((line = bufferedReader.readLine())!=null) {
				Inventory inventory = processLine(line);
				inventories.add(inventory);
			}
			
			return inventories;
			
		}
	}
	
	private Inventory processLine (String line) {
		String[] fields = line.split(",");
		Inventory inventory = new Inventory();
		inventory.setSku(Integer.parseInt(fields[0].trim()));
		inventory.setProductName(fields[1].trim());
		inventory.setQuantity(Integer.parseInt(fields[2].trim()));
		inventory.setPrice(Double.parseDouble(fields[3].trim()));

		return inventory;
	}
	
}
