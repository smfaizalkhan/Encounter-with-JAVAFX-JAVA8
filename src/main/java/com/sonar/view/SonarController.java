package com.sonar.view;
 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import javax.websocket.EncodeException;

import org.apache.log4j.Logger;

import com.sonar.config.Configuration;
import com.sonar.model.OrderAsks;
import com.sonar.model.OrderBids;
import com.sonar.model.TradeOrder;
import com.sonar.service.OrderRestService;
import com.sonar.service.OrderWebSocketCall;
import com.sonar.service.TradeRestService;
import com.sonar.service.TradeWebSocketCall;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SonarController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Tab OrderTab;

	@FXML
	private TableView<OrderAsks> OrderTabAsks;

	@FXML
	private TableColumn<OrderAsks, String> OrderTabAsksBook;

	@FXML
	private TableColumn<OrderAsks, String> OrderTabAsksPrice;

	@FXML
	private TableColumn<OrderAsks, String> OrderTabAsksAmount;

	@FXML
	private TableColumn<OrderAsks, String> OrderTabAsksOid;

	@FXML
	private TableView<OrderBids> OrderTabBids;

	@FXML
	private TableColumn<OrderBids, String> OrderTabBidBook;

	@FXML
	private TableColumn<OrderBids, String> OrderTabBidPrice;

	@FXML
	private TableColumn<OrderBids, String> OrderTabBidAmount;

	@FXML
	private TableColumn<OrderBids, String> OrderTabBidOid;

	@FXML
	private TableView<TradeOrder> Tradetable;

	@FXML
	private TableColumn<TradeOrder, String> TradeID;

	@FXML
	private TableColumn<TradeOrder, String> TradeBook;

	@FXML
	private TableColumn<TradeOrder, String> TradeAmount;

	@FXML
	private TableColumn<TradeOrder, String> TradePrice;

	@FXML
	private TableColumn<TradeOrder, String> TradeCreated;

	@FXML
	private TableColumn<TradeOrder, String> MakerSide;

	@FXML
	private TextField upTickCount;

	@FXML
	private TextField downTickCount;

	@FXML
	private TextField uptickcounttext;

	@FXML
	private TextField downtickcounttext;

	@FXML
	private TextField zeroTickCount;

	@FXML
	private TextField zerotickcounttext;
	private OrderRestService orderRestService;
	private TradeRestService tradeRestService;
	private Configuration configuration;
	private int sellCount, buyCount;
	final static Logger logger = Logger.getLogger(SonarController.class);

	@FXML
	public void initialize() {
		orderRestService = new OrderRestService();
		tradeRestService = new TradeRestService();
		Properties properties = new Properties();
		try {
	
			properties.load(SonarController.class.getResourceAsStream("/config.properties"));
			configuration = new Configuration( 
					properties.getProperty("webSocketURL"),
					properties.getProperty("restAPIURL"),
					Integer.parseInt(properties.get("upTick").toString()),
					Integer.parseInt(properties.get("downTick").toString()),
					Integer.parseInt(properties.get("maxOrders").toString()),
					Integer.parseInt(properties.get("minOrders").toString()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	CompletableFuture<Void> orderCompletableFuture = CompletableFuture
			.supplyAsync(() -> callOrderRestService()).thenApply(Void -> callOrderWebScoket());//
		CompletableFuture<Void> tradescompletableFuture = CompletableFuture
				.supplyAsync(() -> callTradeRestService()).thenApply(Void -> callTradeWebScoket());//
	}

	private Void callTradeWebScoket() {
		System.out.println("tradescompletableFuture  Output");
		TradeWebSocketCall tradeWebSocketCall = new TradeWebSocketCall();
		try {
			tradeWebSocketCall.sendMessage(tradeRestService);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
		Tradetable.getItems().addListener(new ListChangeListener<TradeOrder>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends TradeOrder> arg0) {
				logger.info("tradeWebSocketCall.getUpTick()"
						+ tradeWebSocketCall.getUpTick());
				logger.info("tradeWebSocketCall.getDownTick()"
						+ tradeWebSocketCall.getDownTick());
				logger.info("tradeWebSocketCall.getZeroTick()"
						+ tradeWebSocketCall.getZeroTick());
				upTickCount.setText(Integer.toString(tradeWebSocketCall
						.getUpTick()));
				downTickCount.setText(Integer.toString(tradeWebSocketCall
						.getDownTick()));
				zeroTickCount.setText(Integer.toString(tradeWebSocketCall
						.getZeroTick()));

				logger.info("config count" + configuration.getUpTick());

				if (tradeWebSocketCall.getUpTick() == configuration.getUpTick()) {
					uptickcounttext.setText("SOLD " + (++sellCount) + "BTC");
					tradeWebSocketCall.setUpTick(0);
					upTickCount.clear();

				}
				if (tradeWebSocketCall.getDownTick() == configuration
						.getDownTick()) {
					downtickcounttext.setText("BOUGHT " + (++buyCount) + "BTC");
					tradeWebSocketCall.setDownTick(0);
					downTickCount.clear();
				}

				zerotickcounttext.setText("STAUS QUO");

			}
		});
		return null;
	}

	private Void callOrderWebScoket() {
		System.out.println("orderCompletableFuture  Output");
		OrderWebSocketCall bookWebSocketCall = new OrderWebSocketCall();
		try {
			bookWebSocketCall.sendMessage(orderRestService);
		} catch (IOException | EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrderTabAsks.getItems().addListener(
				new ListChangeListener<OrderAsks>() {

					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends OrderAsks> arg0) {

					}
				});
		return null;
	}

	private Void callOrderRestService() {
		orderRestService.callRestService(configuration);

		OrderTabAsksBook.setCellValueFactory(asks -> asks.getValue()
				.bookProperty());
		OrderTabAsksPrice.setCellValueFactory(asks -> asks.getValue()
				.priceProperty());
		OrderTabAsksAmount.setCellValueFactory(asks -> asks.getValue()
				.amountProperty());
		OrderTabAsksOid.setCellValueFactory(asks -> asks.getValue()
				.oidProperty());

		OrderTabBidBook.setCellValueFactory(bids -> bids.getValue()
				.bookProperty());
		OrderTabBidPrice.setCellValueFactory(bids -> bids.getValue()
				.priceProperty());
		OrderTabBidAmount.setCellValueFactory(bids -> bids.getValue()
				.amountProperty());
		OrderTabBidOid.setCellValueFactory(bids -> bids.getValue()
				.oidProperty());

		OrderTabAsks.setItems(orderRestService.getOrderAsksList());

		OrderTabBids.setItems(orderRestService.getOrderBidsList());

		return null;

	}

	private Void callTradeRestService() {

		tradeRestService.callRestService(configuration);

		TradeID.setCellValueFactory(trade -> trade.getValue().getTid());
		TradeBook.setCellValueFactory(trade -> trade.getValue().getBook());
		TradeAmount.setCellValueFactory(trade -> trade.getValue().getAmount());
		TradePrice.setCellValueFactory(trade -> trade.getValue().getPrice());
		TradeCreated.setCellValueFactory(trade -> trade.getValue()
				.getCreate_at());
		MakerSide
				.setCellValueFactory(trade -> trade.getValue().getMaker_Side());

		Tradetable.setItems(tradeRestService.getTradeList());
		return null;
		
	}
}
