package com.sonar.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.binding.BooleanBinding;
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
 

@ClientEndpoint
public class OrderWebSocketCall {

	Session session = null;
	private boolean responeFlag;
	private String sequenceNo;
	private  OrderRestService orderRestService;
	final static Logger logger = Logger.getLogger(OrderWebSocketCall.class);
	
	public OrderWebSocketCall(){
		try {
		URI uri = new URI("wss://ws.bitso.com");	
		ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
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
		logger.info("message"+message);		
		JsonObject respObj = Json.createReader(new StringReader(message))
				.readObject();
		
		//logger.info("respObj" + respObj.toString());
		if (respObj.containsKey("response")) {
			if (respObj.getString("response").equals("ok")) {
				responeFlag = true;
			}
		}
		
		if (respObj.containsKey("payload") && responeFlag == true) {
			
			JsonArray payloadArray = respObj.getJsonArray("payload");	
			logger.info("payloadArray length"+payloadArray.size());
			
			logger.info("sequence is"+respObj.getJsonNumber("sequence"));
			
			payloadArray.forEach(jsonValue ->createOrderBook(jsonValue,respObj.getJsonNumber("sequence").toString()));	
			
			

		}
		
      }
	

	private void createOrderBook(JsonValue jsonValue,String sequenceNo) {
		 JsonObject jsonObj = (JsonObject)jsonValue;
		 logger.info("order incoming"+jsonObj.get("o"));
//		 logger.info("date"+jsonObj.get("d"));
//		 logger.info("rate"+jsonObj.get("r"));		 
//		 logger.info("sellbuy"+jsonObj.get("t"));
//		 logger.info("amount"+jsonObj.get("a"));
//		 logger.info("vale"+jsonObj.get("v"));
//		 logger.info("status"+jsonObj.get("s"));
		 
		 String order = (jsonObj.get("o")!=null?jsonObj.get("o").toString().replaceAll("\\W", ""):null);
		 String date = (jsonObj.get("d")!=null?jsonObj.get("d").toString().replaceAll("\\W", ""):null);
		 String rate = (jsonObj.get("r")!=null?jsonObj.get("r").toString().replaceAll("\\W", ""):null);
		 String sellbuy = (jsonObj.get("t")!=null?jsonObj.get("t").toString().replaceAll("\\W", ""):null);
		 String amount = (jsonObj.get("a")!=null?jsonObj.get("a").toString().replaceAll("\\W", ""):null);
		 String vale = (jsonObj.get("v")!=null?jsonObj.get("v").toString().replaceAll("\\W", ""):null);
		 String status = (jsonObj.get("s")!=null?jsonObj.get("s").toString().replaceAll("\\W", ""):null);
		 
		
		 if(Long.parseLong(sequenceNo) > Long.parseLong(this.sequenceNo)){
				logger.info("Incoming sequence no is greater thena previous");
		 
		 
		 
		 Object  activated = (jsonObj.get("t").equals("0")? new OrderBids("btc_mxn",rate,amount,order): 
			 new OrderAsks("btc_mxn",rate,amount,order));

		 
		
	
		 if(activated.getClass().getName().equals("com.sonar.model.OrderBids") && "open".equals(status)){
			 logger.info("In OPEN and ORderBIDS");
			 //orderRestService.getOrderBidsList().clear();
			 orderRestService.getOrderBidsList().add((OrderBids) activated);
		 }
		 if(activated.getClass().getName().equals("com.sonar.model.OrderBids") && "cancelled".equals(status)){
			 logger.info("In cancelled and ORderBIDS");
			
			 logger.info(order+"removed"+orderRestService.getOrderBidsList().removeIf(orderBids -> orderBids.getOid().equals(order)));
			// orderRestService.getOrderBidsList().add((OrderBids) activated);
		 }
		  if(activated.getClass().getName().equals("com.sonar.model.OrderAsks")&& "open".equals(status)){
			  logger.info("In Open and ORderAsks");
			// orderRestService.getOrderAsksList().clear();
			 orderRestService.getOrderAsksList().add((OrderAsks) activated);
		 }		
		  if(activated.getClass().getName().equals("com.sonar.model.OrderAsks")&& "cancelled".equals(status)){
			  logger.info("In Cancelled and ORderAsks");			  
			  
				 
			 logger.info(order+"removed"+orderRestService.getOrderAsksList().removeIf(orderAsks -> orderAsks.getOid().equals(order)));
			  
				// orderRestService.getOrderAsksList().add((OrderAsks) activated);
			 }
			
		 }
		
	}


	public void sendMessage(OrderRestService orderRestService) throws IOException, EncodeException {
	//	logger.info("session is" + this.session.getId());	 
	  this.orderRestService = orderRestService;
	  this.sequenceNo = this.orderRestService.getSequenceNo().replaceAll("\\W", "");
		session.getBasicRemote().sendObject(buildJSONData());
	}

	private String buildJSONData() {
		JsonObject jsonObject = Json.createObjectBuilder()
				.add("action", "subscribe").add("book", "btc_mxn")
				.add("type", "diff-orders").build();

		logger.info("jsonObject" + jsonObject.toString());
		return jsonObject.toString();
	}
	
	public static void main(String args[]) throws IOException, EncodeException{
		OrderWebSocketCall bookWebSocketCall = new OrderWebSocketCall();
		OrderRestService orderRestService = new OrderRestService();
		bookWebSocketCall.sendMessage(orderRestService);
	}
}
