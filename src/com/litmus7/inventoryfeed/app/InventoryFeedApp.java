package com.litmus7.inventoryfeed.app;

import java.util.Scanner;

import com.litmus7.inventoryfeed.controller.InventoryController;
import com.litmus7.inventoryfeed.dto.Response;

public class InventoryFeedApp {

	private final static InventoryController inventoryController = new InventoryController();
	
	public static void main(String[] args) {
		
		final String inputDirectoryPath = "C:\\Users\\HP\\eclipse-workspace\\InventoryFeed-v1\\input";
		
		final String processedDirectoryPath = "C:\\Users\\HP\\eclipse-workspace\\InventoryFeed-v1\\processed";
		
		final String errorDirectoryPath = "C:\\Users\\HP\\eclipse-workspace\\InventoryFeed-v1\\error";
		
		try (Scanner scanner = new Scanner(System.in)) {
			
			boolean flag = false;
			
			System.out.println("Starting application...");
			
			while (!flag) {
				
				System.out.println("1.Process all files\n2.Exit");
				System.out.print("Enter your choice: ");
				
				if(scanner.hasNext()) {
					
					int choice = scanner.nextInt();
					scanner.nextLine();
					
					switch (choice) {
						case 1:
							Response<Void,Boolean,String> processFilesResult = inventoryController.processAllFiles(inputDirectoryPath,processedDirectoryPath,errorDirectoryPath);
							if (processFilesResult.getApplicationStatus()) System.out.println(processFilesResult.getMessage());
							else System.err.println(processFilesResult.getMessage());
							break;
						case 2 :
							flag=true;
							System.out.println("Exiting application.");
							break;
						default:
							System.out.println("Invalid choice.Try again");
							scanner.next();
					}
					
				} else {
					System.out.println("Invalid number.Try again");
					scanner.next();
					}
				
			}

		}
		
	}

}
