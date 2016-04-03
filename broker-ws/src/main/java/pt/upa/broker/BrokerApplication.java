package pt.upa.broker;

import javax.xml.ws.Endpoint;
import pt.upa.broker.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
	
	String uddiURL = args[0];
	String name = args[1];
	String url = args[2];
		
	Endpoint endpoint = null;
	UDDINaming uddiNaming = null;
	
	try {
		endpoint = Endpoint.create(new BrokerPort());
		
		//publishing endpoint
		System.out.printf("Publishing endpoint %s%n", url);
		endpoint.publish(url);
		
		//publishing to UDDI
		System.out.printf("Publishing to UDDI: %s%n With name: %s%n", uddiURL, name);
		uddiNaming = new UDDINaming(uddiURL);
		uddiNaming.rebind(name, url);
		
		//connecting to Transports
		//while ( )
		
		//TODO (erase comment)
		//being a nice good server
		//waiting for connections
		System.out.println("Waiting for connections");
		System.out.println("Time is money, friend!");
		System.in.read();
	}catch (Exception e){
		System.out.printf("Caught exception: %s%n", e);
		e.printStackTrace();
	}finally{
		try{
			if(endpoint != null)
				endpoint.stop();
			System.out.printf("Stopping endpoint @ %s%n", url);
		}catch (Exception e){
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();
		}
	}
	
		
		
	}

}
