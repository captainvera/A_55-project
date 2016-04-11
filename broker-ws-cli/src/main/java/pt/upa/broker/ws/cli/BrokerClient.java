package pt.upa.broker.ws.cli;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import javax.xml.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import pt.upa.broker.ws.*;


public class BrokerClient {

	BrokerPortType broker;

	public BrokerClient(){

	}

	public void connectToBroker(String name) throws Exception{

		String uURL = "http://localhost:9090";
		UDDINaming uddiNaming = new UDDINaming(uURL);
		String epAddress = uddiNaming.lookup(name);

		if(epAddress == null){
			System.out.println("Broker not found");
			return;
		}
		else System.out.printf("Connected to broker @%s%n", epAddress);

		BrokerService service = new BrokerService();

		broker = service.getBrokerPort();

		BindingProvider bindingProvider = (BindingProvider) broker;

		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, epAddress);
		System.out.printf("Connection to %s succesfull%n", name);

	}

	public void ping(String name){
		System.out.printf("Pinging the Transporters...%nResponse: %s%n", broker.ping(name));
	}

	public void clearTransports(){
		System.out.println("Clearing all Jobs...");
		broker.clearTransports();
		System.out.println("Jobs cleared for all Transporters");

	}

		public TransportView viewTransport(String id){
			System.out.println("Retrieving transport with " + id);
			TransportView response = null;
			try{
				response = broker.viewTransport(id);
			} catch (UnknownTransportFault_Exception e) {System.out.println("UNKNOWN TRANSPORT EXCEPTION");}

			return response;
		}

		public String requestTransport(String origin, String destination, int priceMax){
			System.out.println("Request Received Origin: " + origin + " Destination: " + destination);
			String response = null;
			try{
				response = broker.requestTransport(origin, destination, priceMax);
			} catch (InvalidPriceFault_Exception | UnavailableTransportFault_Exception |UnavailableTransportPriceFault_Exception | UnknownLocationFault_Exception e) {
				System.out.println("ERROR IN REQUESTING");
			}
			return response;
		}

		public List<TransportView> listTransports(){
			return broker.listTransports();
		}


}
