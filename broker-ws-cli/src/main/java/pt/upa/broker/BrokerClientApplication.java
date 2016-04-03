package pt.upa.broker;

import pt.upa.broker.ws.cli.*;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		
		BrokerClient brokerclient = new BrokerClient();
		
		brokerclient.connectToBroker("UpaBroker");
		brokerclient.ping("Broker Client");
		
	}

}
