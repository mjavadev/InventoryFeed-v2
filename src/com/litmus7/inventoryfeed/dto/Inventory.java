package com.litmus7.inventoryfeed.dto;

public class Inventory {
	
	private int sku;
	private String productName;
	private int quantity;
	private double price;
	
	public int getSku() {
		return sku;
	}
	public void setSku(int sku) {
		this.sku = sku;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "Inventory [sku=" + sku + ", productName=" + productName + ", quantity=" + quantity + ", price=" + price
				+ "]";
	}
	
	
}
