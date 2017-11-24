package com.sonar.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TradeOrder {

	private StringProperty book;
	private StringProperty create_at; 
	private StringProperty amount;
	private StringProperty maker_Side;
	private StringProperty tid;
	private StringProperty price;

	public TradeOrder(String books, String created_at, String amount,
			String maker_side, String price, String tid) {

		super();
		this.book = new SimpleStringProperty(books);
		this.create_at = new SimpleStringProperty(created_at);
		this.amount = new SimpleStringProperty(amount);
		this.price = new SimpleStringProperty(price);
		this.tid = new SimpleStringProperty(tid);
		this.maker_Side = new SimpleStringProperty(maker_side);

	}

	public StringProperty getBook() {
		return book;
	}

	public void setBook(StringProperty book) {
		this.book = book;
	}

	public StringProperty getCreate_at() {
		return create_at;
	}

	public void setCreate_at(StringProperty create_at) {
		this.create_at = create_at;
	}

	public StringProperty getAmount() {
		return amount;
	}

	public void setAmount(StringProperty amount) {
		this.amount = amount;
	}

	public StringProperty getMaker_Side() {
		return maker_Side;
	}

	public void setMaker_Side(StringProperty maker_Side) {
		this.maker_Side = maker_Side;
	}

	public StringProperty getTid() {
		return tid;
	}

	public void setTid(StringProperty tid) {
		this.tid = tid;
	}

	public StringProperty getPrice() {
		return price;
	}

	public void setPrice(StringProperty price) {
		this.price = price;
	}

}
