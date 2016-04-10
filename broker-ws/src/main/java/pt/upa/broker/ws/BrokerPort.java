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

  public enum Location {
    Porto(Zone.NORTE),
    Braga(Zone.NORTE),
    VianaDoCastelo(Zone.NORTE),
    VilaReal(Zone.NORTE),
    Braganca(Zone.NORTE),
    Lisboa(Zone.CENTRO),
    Leiria(Zone.CENTRO),
    Santarem(Zone.CENTRO),
    CasteloBranco(Zone.CENTRO),
    Coimbra(Zone.CENTRO),
    Aveiro(Zone.CENTRO),
    Viseu(Zone.CENTRO),
    Guarda(Zone.CENTRO),
    Setubal(Zone.SUL),
    Evora(Zone.SUL), 
    Portalegre(Zone.SUL),
    Beja(Zone.SUL),
    Faro(Zone.SUL); 
    
    private Zone _zone;

    private Location(Zone z){
      this._zone = z; 
    }
   
    public boolean NORTE(){
      return _zone.equals(Zone.NORTE);
    } 
    
    public boolean SUL(){
      return _zone.equals(Zone.SUL);
    } 

    public boolean CENTRO(){
      return _zone.equals(Zone.CENTRO);
    } 
    
    public Zone getZone(){
      return _zone;
    }

    public enum Zone {
      NORTE,
      CENTRO,
      SUL
    }

    public String value() {
        return name();
    }

    public static Location fromValue(String v) {
      try{
        Location ret = valueOf(v);
        return ret;
      }catch(IllegalArgumentException e){
        return null;
      }
    }
  } 

	TreeMap<String, TransporterPortType> transporters = new TreeMap<String, TransporterPortType>();
	ArrayList<TransportView> _transports = new ArrayList<TransportView>();
	int _idCounter;

	public BrokerPort() throws Exception{
		System.out.printf("Broker Initalized.");
		_idCounter = 0;
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
		String name = tv.getTransporterCompany();
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

	public Map.Entry<String, TransporterPortType> checkBestTransporter (String origin, String destination, int price, TransportView transport) throws UnknownLocationFault_Exception, InvalidPriceFault_Exception{
		int min = 101;
		Map.Entry<String, TransporterPortType> bestTransporter = null;

		for (Map.Entry<String, TransporterPortType> entry : transporters.entrySet()){
			try{
				JobView proposedJob = entry.getValue().requestJob(origin, destination, price);
				if (proposedJob != null && proposedJob.getJobPrice() < min){
					min = proposedJob.getJobPrice();
					bestTransporter = entry;
				}

			} catch (BadLocationFault_Exception e) {throw new UnknownLocationFault_Exception(e.getMessage(), new UnknownLocationFault());
			} catch (BadPriceFault_Exception e) { throw new InvalidPriceFault_Exception(e.getMessage(), new InvalidPriceFault());}
		}
		transport.setPrice(min);
		transport.setTransporterCompany(bestTransporter.getKey());
		return bestTransporter;
	}

	public void confirmJob(Map.Entry<String, TransporterPortType> bestTransporter, String idRequest){
		for (Map.Entry<String, TransporterPortType> entry : transporters.entrySet()){
			try{ //WUUUUUUUUUUUUUUT need halp here
				if (bestTransporter.getKey().equals(entry.getKey()))
				entry.getValue().decideJob(idRequest, true);

				else entry.getValue().decideJob(idRequest, false);
			} catch (BadJobFault_Exception e){}

		}
	}

	public void rejectAllOptions (String idRequest){
		for (Map.Entry<String, TransporterPortType> entry : transporters.entrySet()){
			try{
				entry.getValue().decideJob(idRequest, false);
			} catch (BadJobFault_Exception e){}
		}
	}

	public String requestTransport(String origin, String destination, int priceMax)
	throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

		String idRequest = origin + "T" + destination + "ID" + _idCounter;
		_idCounter++;
		String res = "";
		TransportView newTransport = new TransportView();
		newTransport.setId(idRequest);
		newTransport.setOrigin(origin);
		newTransport.setDestination(destination);
		newTransport.setState(TransportStateView.REQUESTED);
		Map.Entry<String, TransporterPortType> bestTransporter = checkBestTransporter(origin, destination, priceMax, newTransport);

		if(bestTransporter == null){
			newTransport.setState(TransportStateView.FAILED);
			throw new UnavailableTransportFault_Exception("No Transport Available", new UnavailableTransportFault());
		}

		newTransport.setState(TransportStateView.BUDGETED);

		if(newTransport.getPrice() > priceMax){
			rejectAllOptions(idRequest);
			newTransport.setState(TransportStateView.FAILED);
			throw new UnavailableTransportPriceFault_Exception("Price above maximum given by client", new UnavailableTransportPriceFault());
		}
		else{
			confirmJob(bestTransporter, idRequest);
			newTransport.setState(TransportStateView.BOOKED);
			res = "Request accepted by " + newTransport.getTransporterCompany();
		}
		_transports.add(newTransport);
		return idRequest;
	}

}
