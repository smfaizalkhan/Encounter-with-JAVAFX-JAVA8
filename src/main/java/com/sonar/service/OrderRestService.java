package com.sonar.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener.Change;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.sonar.config.Configuration;
import com.sonar.model.OrderAsks;
import com.sonar.model.OrderBids;

public class OrderRestService { 

	private ObservableList<OrderBids> orderBidsList;
	private ObservableList<OrderAsks> orderAsksList;
	private String sequenceNo;
	private int i, j;
	final static Logger logger = Logger.getLogger(OrderRestService.class);

	public OrderRestService() {
		orderBidsList = FXCollections.observableArrayList();
		orderAsksList = FXCollections.observableArrayList();
	}

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public ObservableList<OrderBids> getOrderBidsList() {
		return orderBidsList;
	}

	public ObservableList<OrderAsks> getOrderAsksList() {
		System.out.println("orderAsks" + orderAsksList.size());
		return orderAsksList;
	}

  /**
   * 
   * @param configuration
   */
	public void callRestService(Configuration configuration) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(configuration.getRestAPIURL()
				+ "/v3/order_book/?book=btc_mxn&aggregate=false"); // &aggregate=false
		getRequest.addHeader("accept", "application/json");

		HttpResponse response;
		JsonObject respObj = null;
		JsonObject payLoadObj = null;
		try {
			response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String output;
			logger.info("Output from Server for Order.... \n");
			

			while ((output = br.readLine()) != null) {
				logger.info("output"+output);
				respObj = Json.createReader(new StringReader(output))
						.readObject();
				logger.info("respObj" + respObj);
				if (respObj.containsKey("success")
						&& respObj.get("success").toString().equals("true")) {
					payLoadObj = respObj.getJsonObject("payload");
					this.setSequenceNo(payLoadObj.get("sequence").toString());

					payLoadObj.getJsonArray("bids").forEach(
							bids -> {
								JsonObject bidObect = (JsonObject) bids;
								if (i < configuration.getMaxOrders()) {

									this.orderBidsList.add(new OrderBids(
											bidObect.getString("book"),
											bidObect.getString("price"),
											bidObect.getString("amount"),
											bidObect.getString("oid")));// "oid",bidObect.getString("oid")
									++i;
								}
							});

					payLoadObj.getJsonArray("asks").forEach(
							asks -> {
								JsonObject asksObj = (JsonObject) asks;

								if (j < configuration.getMaxOrders()) {
									this.orderAsksList.add(new OrderAsks(
											asksObj.getString("book"), asksObj
													.getString("price"),
											asksObj.getString("amount"),
											asksObj.getString("oid")));// "oid"
																		// ,
																		// asksObj.getString("oid")
									++j;
								}
							});
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpClient.getConnectionManager().shutdown();

	}

}
