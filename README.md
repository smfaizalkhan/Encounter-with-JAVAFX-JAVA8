# SonarTrading

Requirments
 1) JDK 1.8
 2) Apache Maven 3.5.2
 3) git client


Build and Run

1) Clone the Project 
2) Navigate to Project folder and run the command mvn clean package
  ![sonar](https://user-images.githubusercontent.com/22238550/33215577-cff6d4e2-d149-11e7-9ec8-38ab96f4a978.png)
3) It creates target folder and jar.Navigate to the target folder and run the command java -jar SonarTrading-0.0.1-SNAPSHOT.jar
![sonar](https://user-images.githubusercontent.com/22238550/33215714-2361ab98-d14a-11e7-9305-a1f208b59d41.png)
4) It starts the Application
   ![sonar](https://user-images.githubusercontent.com/22238550/33215763-5c9ac5c0-d14a-11e7-8eef-341066dd7f0b.png)
   ![sonar](https://user-images.githubusercontent.com/22238550/33215944-2d72dd40-d14b-11e7-8d36-449f6d7236f7.png)   
 
 
 Configuration
 
 In src/main/resources/config.properties:
  
  1) webSocketURL=wss://ws.bitso.com
  2) restAPIURL=https://api.bitso.com
  3) upTick=3
  4) downTick=3
  5) maxOrders=50
  6) minOrders=50
 
 Check List
 
  
| Feature                                                      |  File name              |Method name              |
| ------------------------------------------------------------ |:-----------------------:| -----------------------:|
|Schedule the polling of trades over REST.                     | TradeRestService.java   | callRestService()       |
|Request a book snapshot over REST.                            | OrderRestService.java   | callRestService()       |
|Listen for diff-orders over websocket.                        | OrderWebSocketCall.java |   sendMessage()         |
|Replay diff-orders.                                           | OrderWebSocketCall.java | createOrderBook()       |
|Use config option X to request  recent trades.                | config.properties       | maxOrders               |
|Use config option X to limit number of ASKs displayed in UI.  | config.properties       | maxOrders               |
|The loop that causes the trading algorithm to reevaluate.     | TradeWebSocketCall      | tradingStrategy()       |
 
  
  
        

