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
import java.util.concurrent.atomic.AtomicInteger;

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
	
	private static final AtomicInteger successCount = new AtomicInteger(0);
	private static final AtomicInteger failureCount = new AtomicInteger(0);
	private static final AtomicInteger completedCount = new AtomicInteger(0);
	
	public List<Integer> createInventory(String inputDirectoryPath,String processedDirectoryPath,String errorDirectoryPath) throws InventoryServiceException {
		logger.trace("Entering createInventory()");
		
		List<Thread> threads = new ArrayList<>();
		
		List<Path> csvFiles = new ArrayList<>();
		
		int totalNumberOfFilesToProcess = 0;
		
		try {
			csvFiles = findCsvFiles(inputDirectoryPath);
			totalNumberOfFilesToProcess = csvFiles.size();
			if (csvFiles == null || totalNumberOfFilesToProcess==0) {
				logger.error("No CSV files found in input folder.");
				throw new InventoryServiceException(MessageConstants.ERROR_NO_CSV_FILES_FOUND_IN_INPUT_DIR, MessageConstants.ERROR_CODE_NO_CSV_FILES_FOUND_IN_INPUT_DIR);
		} catch (IOException e) {
			logger.error("Error reading input directory: {}", e.getMessage());
			throw new InventoryServiceException(MessageConstants.ERROR_SERVICE_INPUT_DIRECTORY_READ, e, MessageConstants.ERROR_CODE_SERVICE_INPUT_DIRECTORY_READ);
		}
		
		for (Path file: csvFiles) {
			
			Runnable runnable = () -> {
				try {
					
					List<Inventory> inventories =  readCsvFile(file);
					
					for (Inventory inventory : inventories) { 
							int inventoriesAdded = inventoryDAO.saveInventory(inventory);
					}
					
					logger.info("Inventory created successfully for file: {}",file.getFileName());
					
					try {
						moveFile(file, processedDirectoryPath);
						successCount.incrementAndGet();
						logger.info("File moved to processed folder: {}", file.getFileName());
					} catch (IOException e) {
						logger.error("Failed to move file to processed folder: {}",file.getFileName());
					}
					
				}catch (IOException e) {
					logger.error("In Service layer: Csv file read operaion failed for {}",file.getFileName(),e);
					
					try {
						moveFile(file, errorDirectoryPath);	
						failureCount.incrementAndGet();
						logger.info("File moved to error folder: {}",file.getFileName());
					} catch (IOException e1) {
						logger.error("Failed to move file to error folder: {}",file.getFileName(),e1);
					}
					
				}catch (InventoryDAOException e) {
					logger.error("Error code: {} Message: {}",e.getErrorCode(),MessageConstants.ERROR_SERVICE_SAVE_INVENTORY,e);
					
					try {
						moveFile(file, errorDirectoryPath);
						failureCount.incrementAndGet();
						logger.info("File moved to error folder: {}",file.getFileName());
					} catch (IOException e1) {
						logger.error("Failed to move file to error folder: {}",file.getFileName(),e1);
					}
				
				} finally {
					completedCount.incrementAndGet();
				}
			};
			
			Thread thread = new Thread(runnable);
			threads.add(thread);
			thread.start();
			
		}
		
		waitForAllThreadsToComplete(threads);
		
		generateReportOfThreadsUsage(totalNumberOfFilesToProcess, threads);
		
		if (totalNumberOfFilesToProcess==successCount.get() && totalNumberOfFilesToProcess>0) 
			logger.info("Inventory creation successful for all {} file(s)", successCount.get());
		else if (totalNumberOfFilesToProcess>successCount.get() && successCount.get()>0) {
			logger.info("Inventory creation success for {} files",successCount.get());
			logger.warn("Inventory creation failed for {} file(s)",failureCount.get());
		}
		else if (successCount.get()==0)
			logger.warn("Inventory creation completely failed");
		
		logger.trace("Exiting createInventory()");
		return List.of(totalNumberOfFilesToProcess,successCount.get());
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
	
	private void waitForAllThreadsToComplete(List<Thread> threads) {
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.warn("Warning: Interrupted while waiting for thread: {}",thread.getName(),e);
			}
		}
	}
	
	private void moveFile(Path source,String destination) throws IOException {
		Files.move(source,  Paths.get(destination).resolve(source.getFileName()),
				StandardCopyOption.REPLACE_EXISTING);
	}
	
	private void generateReportOfThreadsUsage(int totalNumberOfFilesToProcess,List<Thread> threads) {
		logger.info("\n=== PROCESSING COMPLETE ===");
		logger.info("Total files to process: {}",totalNumberOfFilesToProcess);
		logger.info("Total threads created: {}",threads.size());
		logger.info("Completed threads: {}",completedCount.get());
		logger.info("Successfully processed threads: {}",successCount.get());
		logger.info("Failed threads: {}",failureCount.get());
	}
}
