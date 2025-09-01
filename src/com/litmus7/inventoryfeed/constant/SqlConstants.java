package com.litmus7.inventoryfeed.constant;

public class SqlConstants {

	public static final String INVENTORY_TABLE = "inventory";
	
	public static final String INVENTORY_COL_SKU = "SKU";
	public static final String INVENTORY_COL_PRODUCTNAME = "ProductName";
	public static final String INVENTORY_COL_QUANTITY = "Quantity";
	public static final String INVENTORY_COL_PRICE = "Price";
	
	public static final String INSERT_INVENTORY = "insert into "+INVENTORY_TABLE+" values (?,?,?,?)";
	
	
	
}
