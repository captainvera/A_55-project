package pt.upa.broker.ws;

import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;

import javax.jws.WebService;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.transporter.ws.*;

@WebService(
		endpointInterface="pt.upa.broker.ws.BrokerPortType",
		wsdlLocation="broker.1_0.wsdl",
		name="UpaBroker",
		portName="BrokerPort",
		targetNamespace="http://ws.broker.upa.pt/",
		serviceName = "BrokerService"
)

public class BrokerPort implements BrokerPortType{
	
	ArrayList<TransporterPortType> transporters = new ArrayList<TransporterPortType>(); 
	public BrokerPort() throws Exception{
		System.out.printf("Broker Initalized.");
		Integer transportnum = new Integer(1);
		String epAddress;
		TransporterPortType transp;
		String uURL = "http://localhost:9090";

		UDDINaming uddiNaming = new UDDINaming(uURL);
		//connecting to Transports
		while (true){
			
			epAddress = uddiNaming.lookup("UpaTransporter" + transportnum.toString());
			if(epAddress == null){
				System.out.printf("UpaTransporter #%d not found%n", transportnum);
				break;
			}else System.out.printf("Connected to transporter #%d%n", transportnum);
			
			TransporterService service = new TransporterService();
			
			transp = service.getTransporterPort();
			
			BindingProvider bindingProvider = (BindingProvider) transp;
			
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, epAddress);
			System.out.printf("Connection to %s succesfull%n","UpaTransporter" + transportnum.toString());
			transporters.add(transp);
			transportnum++;
			
		}
	}
	
	public String ping(String name){
		
		int _num = transporters.size();
		return "Contact has been established with " + _num + " Transporters.";
	}

	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return new TransportView();
	}
	
	public void clearTransports(){
		System.out.printf("Executing order 66.%n");
		for (int i=0; i < transporters.size();i++){
			transporters.get(i).clearJobs();
			System.out.printf("Order 66 on transporter %d%n", i+1);
		}
		System.out.println("The Sith Shall Rule again.");
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
