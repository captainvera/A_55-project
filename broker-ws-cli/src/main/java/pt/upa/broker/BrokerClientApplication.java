package pt.upa.broker;

import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

		BrokerClient brokerclient = new BrokerClient();

		brokerclient.connectToBroker("UpaBroker");
		brokerclient.ping("Broker Client");
		brokerclient.clearTransports();
		String request = brokerclient.requestTransport("Lisboa", "Leiria", 10);
		System.out.println ("REQUEST: " + request);
		TransportView tv = brokerclient.viewTransport(request);
		System.out.println("[RESPONSE] " + tv.getId() + " | STATE: " + tv.getState().value());
	}

}
