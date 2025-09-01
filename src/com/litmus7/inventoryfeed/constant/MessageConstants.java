package com.litmus7.inventoryfeed.constant;

public class MessageConstants {

	//DAO
	//1.failure
	public static final String ERROR_DB_SAVE_INVENTORY = "Database error while saving inventory";
	public static final String ERROR_CODE_DB_SAVE_INVENTORY = "INV_DB_001";
	public static final String ERROR_UI_INV_DB_001 = "We couldn’t save the inventory details. Please try again.";
	
	public static final String ERROR_DB_ROLLBACK = "Rollback failed";
	public static final String ERROR_CODE_DB_ROLLBACK = "INV_DB_002";
	public static final String ERROR_UI_INV_DB_002 = "Rollback failed; manual intervention required.";
	
	
	
	
	
	//Service
	//1.failure
	public static final String ERROR_SERVICE_SAVE_INVENTORY = "In Service layer: Database failed to create inventory";

	
	public static final String ERROR_SERVICE_CSV_FILE_READ = "In Service layer: Csv file read operaion failed";
	public static final String ERROR_CODE_SERVICE_CSV_FILE_READ = "INV_SRV_001";
	public static final String ERROR_UI_INV_SRV_001 =  "Failed to read CSV file. Please check the format and try again.";
	
	
	public static final String ERROR_SERVICE_INPUT_DIRECTORY_READ = "In Service layer: Input directory read operation to find csv files failed";
	public static final String ERROR_CODE_SERVICE_INPUT_DIRECTORY_READ = "INV_SRV_002";
	public static final String ERROR_UI_INV_SRV_002 = "Unable to access input files. Please check directory permissions.";
	
	public static final String ERROR_NO_CSV_FILES_FOUND_IN_INPUT_DIR  = "No CSV files found in input folder.";
	public static final String ERROR_CODE_NO_CSV_FILES_FOUND_IN_INPUT_DIR = "INV_SRV_003";
	public static final String ERROR_UI_INV_SRV_003 = "No CSV files found in input folder.";
	
	
	
	
	//Controller
	//1.failure
	public static final String ERROR_CONTROLLER_INVALID_INPUT_DIRECTORY = "Invalid input directory";
	public static final String ERROR_CONTROLLER_INVALID_PROCESSED_DIRECTORY = "Invalid processed directory";
	public static final String ERROR_CONTROLLER_INVALID_ERROR_DIRECTORY = "Invalid error directory";
	public static final String ERROR_CONTROLLER_INVENTORY_CREATION = "Inventory creation failed";
	public static final String ERROR_FILE_PROCESSED = "Failed to process {} file(s)";
	public static final String ERROR_NO_FILES_PROCESSED = "All files failed to process";
	
	//2.Success
	public static final String ALL_FILE_PROCESSED_SUCCESSFULLY = "All file(s) processed successfully";
	public static final String FILE_PROCESSED_SUCCESSFULLY = "Successfully processed {} file(s)";
	public static final String FILE_PARTIAL_PROCESSED_SUCCESSFULLY_AND_PARTIAL_FAILED =  "%s file(s) processed succesfully and %s file(s) processing failed.";
	
	
	
	//General
	public static final String ERROR_MESSAGE= "Something went wrong. Please try again later.";
	
	
	
}
