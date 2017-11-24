package com.sonar.service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.sonar.model.OrderAsks;
import com.sonar.model.OrderBids;
import com.sonar.model.TradeOrder;

@ClientEndpoint
public class TradeWebSocketCall {

	Session session = null; 
	private boolean responeFlag;
	private TradeRestService tradeRestService;
	private int upTick, downTick, zeroTick;
	final static Logger logger = Logger.getLogger(TradeWebSocketCall.class);

	public int getUpTick() {
		return upTick;
	}

	public int getDownTick() {
		return downTick;
	}

	public int getZeroTick() {
		return zeroTick;
	}

	public void setUpTick(int upTick) {
		this.upTick = upTick;
	}

	public void setDownTick(int downTick) {
		this.downTick = downTick;
	}

	public void setZeroTick(int zeroTick) {
		this.zeroTick = zeroTick;
	}

	public TradeWebSocketCall() {
		try {
			URI uri = new URI("wss://ws.bitso.com");
			ContainerProvider.getWebSocketContainer()
					.connectToServer(this, uri);
		} catch (DeploymentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnMessage
	public void onMessage(String message) {
		logger.info("message" + message);
		JsonObject respObj = Json.createReader(new StringReader(message))
				.readObject();

		// logger.info("respObj" + respObj.toString());
		if (respObj.containsKey("response")) {
			if (respObj.getString("response").equals("ok")) {
				responeFlag = true;
			}
		}

		if (respObj.containsKey("payload") && responeFlag == true) {

			JsonArray payloadArray = respObj.getJsonArray("payload");
			logger.info("payloadArray length" + payloadArray.size());

			logger.info("sequence is" + respObj.getJsonNumber("sequence"));

			payloadArray.forEach(jsonValue -> createTradeBook(jsonValue,
					respObj.get("book").toString()));

		}

	}
/**
 * 
 * @param jsonValue
 * @param books
 */
	private void createTradeBook(JsonValue jsonValue, String books) {
		JsonObject jsonObj = (JsonObject) jsonValue;
		String Id = (jsonObj.get("i") != null ? jsonObj.get("i").toString().replaceAll("\\W", "")
				: null);
		String amount = (jsonObj.get("a") != null ? jsonObj.get("a").toString().replaceAll("\\W", "")
				: null);
		String rate = (jsonObj.get("r") != null ? jsonObj.get("r").toString().replaceAll("\\W", "")
				: null);
		String sellbuy = (jsonObj.get("t") != null ? jsonObj.get("t")
				.toString().replaceAll("\\W", "") : null);
		String vale = (jsonObj.get("v") != null ? jsonObj.get("v").toString().replaceAll("\\W", "")
				: null);
		String makeOrderId = (jsonObj.get("mo") != null ? jsonObj.get("mo")
				.toString().replaceAll("\\W", "") : null);
		String takeOrderId = (jsonObj.get("to") != null ? jsonObj.get("to")
				.toString().replaceAll("\\W", "") : null);
		String maker_side = (sellbuy.equals("0") ? "buy" : "sell");

		this.tradeRestService.getTradeList().add(
				new TradeOrder(books, ZonedDateTime.now().format(
						DateTimeFormatter.ISO_OFFSET_DATE_TIME), amount,
						maker_side, rate, Id));

		this.tradingStrategy(this.tradeRestService.getTradeList(), rate);

	}
/**
 * 
 * @param tradeList
 * @param rate
 */
	private void tradingStrategy(ObservableList<TradeOrder> tradeList,
			String rate) {

		TradeOrder preveTradeRec = tradeList.get((tradeList.size() - 2));

		logger.info("preveTradeRec ID" + preveTradeRec.getTid());
		logger.info("preveTradeRec PRICE" + preveTradeRec.getPrice());

		String prevPrice = preveTradeRec.getPrice().get().replaceAll("\\W", "");

		logger.info("rate" + Double.parseDouble(rate.replaceAll("\\W", "")));
		logger.info("preveTradeRec LONG" + Double.parseDouble(prevPrice));

		int result = Double.compare(
				Double.parseDouble(preveTradeRec.getPrice().get().replaceAll("\\W", "")),
				Double.parseDouble(rate));

		if (result == 0) {
			this.zeroTick = this.zeroTick + 1;
			logger.info("zerotick" + this.zeroTick);
		} else if (result > 0) {
			logger.info("First Value is greater than the second");
			this.downTick = this.downTick + 1;
			logger.info("downTick" + this.downTick);
			this.setUpTick(0);
		} else {
			logger.info("First Value is less than the second");
			this.upTick = this.upTick + 1;
			logger.info("upTick" + this.upTick);
			this.setDownTick(0);
		}

	}

	public void sendMessage(TradeRestService orderRestService)
			throws IOException, EncodeException {

		this.tradeRestService = orderRestService;
		session.getBasicRemote().sendObject(buildJSONData());
	}

	private String buildJSONData() {
		JsonObject jsonObject = Json.createObjectBuilder()
				.add("action", "subscribe").add("book", "btc_mxn")
				.add("type", "trades").build();

		logger.info("jsonObject" + jsonObject.toString());
		return jsonObject.toString();
	}

	public static void main(String args[]) throws IOException, EncodeException {
		TradeWebSocketCall bookWebSocketCall = new TradeWebSocketCall();
		TradeRestService tradeRestService = new TradeRestService();
		bookWebSocketCall.sendMessage(tradeRestService);
	}
}
