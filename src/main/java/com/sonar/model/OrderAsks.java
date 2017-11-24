package com.sonar.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderAsks {
 
	private StringProperty book;
	private StringProperty price;
	private StringProperty amount;
	private StringProperty oid;

	public OrderAsks(String book, String price, String amount, String oid) {
		super();
		this.book = new SimpleStringProperty(book);
		this.price = new SimpleStringProperty(price);
		this.amount = new SimpleStringProperty(amount);
		this.oid = new SimpleStringProperty(oid);
	}

	public String getBook() {
		return book.get();
	}

	public StringProperty bookProperty() {
		return book;
	}

	public void setBook(String book) {
		this.book.set(book);
	}

	public String getPrice() {
		return price.get();
	}

	public void setPrice(String price) {
		this.price.set(price);
	}

	public StringProperty priceProperty() {
		return price;
	}

	public String getAmount() {
		return amount.get();
	}

	public void setAmount(String amount) {
		this.amount.set(amount);
	}

	public StringProperty amountProperty() {
		return amount;
	}

	public String getOid() {
		return oid.get();
	}

	public void setOid(String oid) {
		this.oid.set(oid);
	}

	public StringProperty oidProperty() {
		return oid;
	}

}
