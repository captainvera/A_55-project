package pt.upa.broker;

import java.net.SocketTimeoutException;
import java.util.*;
import javax.xml.ws.*;
import java.util.ArrayList;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

		BrokerClient brokerclient = new BrokerClient();

		brokerclient.connectToBroker("UpaBroker");
    brokerclient.ping("Broker Client");
		String request = brokerclient.requestTransport("Lisboa", "Leiria", 10);
		System.out.println ("REQUEST: " + request);
		List<TransportView> cenas = new ArrayList<TransportView>();
		cenas = brokerclient.listTransports();
		System.out.println("PRIMARY: ");
		for(TransportView t : cenas){
			System.out.println("TRANSPORT: " + t.getId() + " price " + t.getPrice() + " state: " + t.getState().value());
		}
		System.out.println("----------------------------");
		brokerclient.clearTransports();
		cenas = brokerclient.listTransports();
		System.out.println("PRIMARY: ");
		for(TransportView t : cenas){
			System.out.println("TRANSPORT: " + t.getId() + " price " + t.getPrice() + " state: " + t.getState().value());
		}
		System.out.println("----------------------------");
		try{
			while(true){
				String tv = brokerclient.ping("me");
        Thread.sleep(2000);
				System.out.println("[RESPONSE] " + tv);
			}
		} catch(WebServiceException wse) {
      System.out.println("Caught: " + wse.getCause());
      Throwable cause = wse.getCause();
      if (cause != null && cause instanceof SocketTimeoutException) {
          System.out.println("The cause was a timeout exception: " + cause);
      }
  	}
	}

}
