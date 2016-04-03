package pt.upa.broker.ws;

import java.util.List;
import java.util.ArrayList;

import javax.jws.WebService;

@WebService(
		endpointInterface="pt.upa.broker.ws.BrokerPortType",
		wsdlLocation="broker.1_0.wsdl",
		name="UpaBroker",
		portName="BrokerPort",
		targetNamespace="http://ws.broker.upa.pt/",
		serviceName = "BrokerService"
)

public class BrokerPort implements BrokerPortType{
	
	public BrokerPort(){
		System.out.printf("Broker Initalized.");
	}
	
	public String ping(String name){
		
		return "Broker has been pinged by " + name;
	}
	
	public String pingTransporters(){
		int _num = 0;
		
		return "Contact has been established with " + _num + " Transporters.";
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return new TransportView();
	}
	
	public void clearTransports(){
		return;
	}
	
	public List<TransportView> listTransports(){
		
		return new ArrayList<TransportView>();
	
	}
	
	public String requestTransport(String origin, String destination, int price) 
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		return "temporary String";
	}

}
