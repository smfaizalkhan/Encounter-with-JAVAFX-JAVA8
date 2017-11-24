package com.sonar.config;

public class Configuration {

	private String webSocketURL;
	private String restAPIURL;
	private int upTick;
	private int downTick;
	private int maxOrders; 
	private int minOrders;

	public Configuration(String webSocketURL, String restAPIURL, int upTick,
			int downTick, int maxOrders, int minOrders) {
		super();
		this.webSocketURL = webSocketURL;
		this.restAPIURL = restAPIURL;
		this.upTick = upTick;
		this.downTick = downTick;
		this.maxOrders = maxOrders;
		this.minOrders = minOrders;
	}

	public int getMaxOrders() {
		return maxOrders;
	}

	public void setMaxOrders(int maxOrders) {
		this.maxOrders = maxOrders;
	}

	public String getWebSocketURL() {
		return webSocketURL;
	}

	public void setWebSocketURL(String webSocketURL) {
		this.webSocketURL = webSocketURL;
	}

	public String getRestAPIURL() {
		return restAPIURL;
	}

	public void setRestAPIURL(String restAPIURL) {
		this.restAPIURL = restAPIURL;
	}

	public int getUpTick() {
		return upTick;
	}

	public void setUpTick(int upTick) {
		this.upTick = upTick;
	}

	public int getDownTick() {
		return downTick;
	}

	public void setDownTick(int downTick) {
		this.downTick = downTick;
	}

	public int getMinOrders() {
		return minOrders;
	}

	public void setMinOrders(int minOrders) {
		this.minOrders = minOrders;
	}

	public void setupConfig() {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
