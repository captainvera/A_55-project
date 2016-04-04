package pt.upa.broker.ws;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	TreeMap<String, TransporterPortType> transporters = new TreeMap<String, TransporterPortType>();
	ArrayList<TransportView> _transports = new ArrayList<TransportView>();

	public BrokerPort() throws Exception{
		System.out.printf("Broker Initalized.");
		Integer transportnum = new Integer(1);
		String epAddress;
		TransporterPortType transp;
		String uURL = "http://localhost:9090";

		UDDINaming uddiNaming = new UDDINaming(uURL);
		//connecting to Transports
		while (true){
			String name = "UpaTransporter" + transportnum.toString();
			epAddress = uddiNaming.lookup(name);
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
			transporters.put(name, transp);
			transportnum++;

		}
	}

	public String ping(String name){
		String res = new String();
		System.out.printf("Pinging all transporters%n");
		for (Map.Entry<String, TransporterPortType> entry : transporters.entrySet()){
			res = res + entry.getValue().ping(name);
		}
		return "Contact has been established with " + transporters.size() + " Transporters. With messages:\n" + res;
	}

	protected TransportView getTransportViewById(String id){
		for(TransportView t : _transports){
      if(t.getId().equals(id)){
        return t;
      }
    }
    return null;
  }


	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		TransportView tv = getTransportViewById(id);
		if(tv==null) throw new UnknownTransportFault_Exception("Transport with " + id + " not found", new UnknownTransportFault());

		/*
		 * O id do transport é igual ao do job ???
		 */
		 String name = tv.getName();
		 TransporterPortType tpt = transporters.get(name);
		 JobStateView state = tpt.jobStatus(id).getJobState();
		 switch (state) {
			 	case HEADING:
			 		tv.setState(TransportStateView.HEADING);
					break;

				case ONGOING:
					tv.setState(TransportStateView.ONGOING);
					break;

				case COMPLETED:
					tv.setState(TransportStateView.COMPLETED);
					break;

				default:
					break;
			}

			return tv;
	}

	public void clearTransports(){
		int i = 0;
		System.out.printf("Executing order 66.%n");
		for (Map.Entry<String, TransporterPortType> entry : transporters.entrySet()){
			entry.getValue().clearJobs();
			System.out.printf("Order 66 on transporter %d%n", i+1);
			i++;
		}
		System.out.println("The Sith Shall Rule again.");
	}

	public List<TransportView> listTransports(){

		return new ArrayList<TransportView>(_transports);

	}

	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		return "temporary String";
	}

}
