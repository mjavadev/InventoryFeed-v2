package com.litmus7.inventoryfeed.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.litmus7.inventoryfeed.constant.MessageConstants;
import com.litmus7.inventoryfeed.constant.SqlConstants;
import com.litmus7.inventoryfeed.dto.Inventory;
import com.litmus7.inventoryfeed.exception.InventoryDAOException;
import com.litmus7.inventoryfeed.util.DbConnectionUtil;

public class InventoryDAO {

	private final static Logger logger = LogManager.getLogger(InventoryDAO.class);
	
	public int saveInventory (Inventory inventory) throws InventoryDAOException {
		
		logger.trace("Entering saveInventory() for SKU: {}", inventory.getSku());
		
		Connection connection = null;
		try {
			connection = DbConnectionUtil.getConnection();
			
			connection.setAutoCommit(false);
			
			String inventoryInsertQuery = SqlConstants.INSERT_INVENTORY;
			
			try(PreparedStatement preparedStatement = connection.prepareStatement(inventoryInsertQuery))
			{
			preparedStatement.setInt(1, inventory.getSku());
			preparedStatement.setString(2, inventory.getProductName());
			preparedStatement.setInt(3, inventory.getQuantity());
			preparedStatement.setDouble(4, inventory.getPrice());
			
			int rowsInserted = preparedStatement.executeUpdate();
			
			connection.commit();
			
			logger.info("Inserted {} row inventory successfully for SKU: {}", rowsInserted,inventory.getSku());
			
			logger.trace("Exiting saveInventory() for SKU: {}", inventory.getSku());
			
			return rowsInserted;
			}
			
		} catch (SQLException e) {
			logger.error("Error Code: {} Message: {} SKU: {}",MessageConstants.ERROR_CODE_DB_SAVE_INVENTORY,MessageConstants.ERROR_DB_SAVE_INVENTORY,inventory.getSku(),e);
			
			if (connection!=null) {
				try {
					connection.rollback();
					logger.info("All database changes rolled back successfully");
				} catch (SQLException e1) {
					logger.error("Roll back operation failed", e1);
				}
			}
			
			throw new InventoryDAOException(MessageConstants.ERROR_DB_SAVE_INVENTORY,e, MessageConstants.ERROR_CODE_DB_SAVE_INVENTORY);
			
		} finally {
			if (connection!=null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					logger.error("Database connection closing failed",e);
				}
			}
		}
	}
	
	
	
	}
