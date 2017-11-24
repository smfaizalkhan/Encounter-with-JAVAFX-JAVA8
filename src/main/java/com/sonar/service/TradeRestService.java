package com.sonar.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.sonar.config.Configuration;
import com.sonar.model.OrderAsks;
import com.sonar.model.OrderBids;
import com.sonar.model.TradeOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TradeRestService { 

	private ObservableList<TradeOrder> tradeList;
	final static Logger logger = Logger.getLogger(TradeRestService.class);

	public TradeRestService() {
		tradeList = FXCollections.observableArrayList();
	}

	public ObservableList<TradeOrder> getTradeList() {
		logger.info("tradead" + tradeList.size());
		return tradeList;
	}
/**
 * 
 * @param configuration
 */
	public void callRestService(Configuration configuration) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String url = configuration.getRestAPIURL()
				+ "/v3/trades?book=btc_mxn&limit="
				+ configuration.getMaxOrders();
		logger.info("URL IS" + url);
		HttpGet getRequest = new HttpGet(url);
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
			logger.info("Output from Server for Trade .... \n");
			
			while ((output = br.readLine()) != null) {
				respObj = Json.createReader(new StringReader(output))
						.readObject();
				logger.info("respObj" + respObj);
				if (respObj.containsKey("success")
						&& respObj.get("success").toString().equals("true")) {

					respObj.getJsonArray("payload")
							.forEach(
									trades -> {
										JsonObject tradeObect = (JsonObject) trades;
										this.tradeList
												.add(new TradeOrder(
														tradeObect.get("book")
																.toString(),
														tradeObect.get(
																"created_at")
																.toString(),
														tradeObect
																.get("amount")
																.toString(),
														tradeObect.get(
																"maker_side")
																.toString(),
														tradeObect.get("price")
																.toString(),
														tradeObect.get("tid")
																.toString()));
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
