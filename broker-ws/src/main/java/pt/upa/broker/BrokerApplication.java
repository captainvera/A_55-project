package pt.upa.broker;

import java.util.ArrayList;
import java.util.Map;
import pt.upa.transporter.ws.*;
import pt.upa.broker.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.upa.ws.Context;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");

	String uddiURL = args[0];
	String name = args[1];
	String url = args[2];

  Context.WS_KEYSTORE_FILE = "keys/Broker.jks";
  Context.WS_IDENTIFIER = "Broker";

	Endpoint endpoint = null;
	UDDINaming uddiNaming = null;
	boolean isPrimary = true;
	try {
		String uURL = "http://localhost:9090";
		uddiNaming = new UDDINaming(uURL);
		String epAddress = uddiNaming.lookup(name);
		if(epAddress == null){
			System.out.println("[DEBUG] I'm primary!");
			isPrimary = true;
		}
		else{
			isPrimary = false;
			System.out.println("[DEBUG] I'm secondary!");
		}

    BrokerPort _broker = new BrokerPort(isPrimary, url);
		endpoint = Endpoint.create(_broker);

		//publishing endpoint
		System.out.printf("Publishing endpoint %s%n", url);
		endpoint.publish(url);

		//publishing to UDDI
		if(isPrimary){
			System.out.printf("Publishing to UDDI: %s%n With name: %s%n", uddiURL, name);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);
		}

		System.out.println("Waiting for connections");
		System.out.println("Time is money, friend!");
		System.in.read();
    isPrimary = _broker.getPrimary();
    _broker.shutdown();

	}catch (Exception e){
		System.out.printf("Caught exception: %s%n", e);
		e.printStackTrace();
	}finally{
		try{
			if(endpoint != null)
				endpoint.stop();
			System.out.printf("Stopping endpoint @ %s%n", url);
		}catch (Exception e){
			System.out.printf("Caught exception when stopping: %s%n", e);
			e.printStackTrace();
		}

		try {
			if (uddiNaming != null && isPrimary) {
				uddiNaming.unbind(name);
				System.out.printf("Deleted '%s' from UDDI@ %s%n", name, uddiURL);
			}
		} catch (Exception e) {
			System.out.printf("Caught exception when unbinding: %s%n", e);
		}
	}


	}

}
