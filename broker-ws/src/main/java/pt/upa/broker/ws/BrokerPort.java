package pt.upa.broker.ws;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebService;
import javax.xml.ws.BindingProvider;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.*;

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
        Location ret = null;
        if(v != null) {
          ret = valueOf(v);
        }
        return ret;
      }catch(IllegalArgumentException e){
        return null;
      }
    }
  }

	TreeMap<String, TransporterClient> transporters = new TreeMap<String, TransporterClient>();
	ArrayList<Transport> _transports = new ArrayList<Transport>();
	int _idCounter;


  public BrokerPort() throws Exception{
    System.out.println("Broker Initalized.");
    connectToTransporters();
  }

  private void connectToTransporters() throws JAXRException{
    System.out.println("Checking for Transporters.");

    String uURL = "http://localhost:9090";
    UDDINaming uddiNaming = new UDDINaming(uURL);
    Collection<String> endpoints = uddiNaming.list("UpaTransporter%");
    System.out.println("Creating clients to connect to " + endpoints.size() + " Transporters");
    createTransporterClients(endpoints);

  }

  private void createTransporterClients( Collection<String> endpoints) throws JAXRException{
    String name;
    for (String endp : endpoints) {
      TransporterClient client = new TransporterClient();
      try{
        client.connectToTransporterByURI(endp);
        name = client.ping();
        transporters.put(name,client);
        System.out.println("Transporter: " + name + " was added to broker");
        } catch(Exception e){e.printStackTrace();}
		}

	}

	public String ping(String name){
		String res = new String();
		System.out.printf("Pinging all transporters%n");
		for (Map.Entry<String, TransporterClient> entry : transporters.entrySet()){
			entry.getValue().ping();
		}
		return "Contact has been established with " + transporters.size() + " Transporters.\n";
	}

	protected Transport getTransportById(String id){
		for(Transport t : _transports){
			if(t.getId().equals(id)){
				return t;
			}
		}
		return null;
	}


	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		Transport tv = getTransportById(id);
		if(tv == null){
      UnknownTransportFault utf = new UnknownTransportFault();
      utf.setId(id);
      throw new UnknownTransportFault_Exception("Transport with " + id + " not found", utf);
    }
		String name = tv.getTransporterCompany();
		TransporterClient tpt = transporters.get(name);
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

		return tv.toTransportView();
	}

	public void clearTransports(){
		int i = 0;
		System.out.printf("Executing order 66.%n");
		for (Map.Entry<String, TransporterClient> entry : transporters.entrySet()){
			entry.getValue().clearJobs();
			System.out.printf("Order 66 on transporter %d%n", i+1);
			i++;
		}
		System.out.println("The Sith Shall Rule again.");
	}

	public List<TransportView> listTransports(){
    ArrayList<TransportView> _tviews = new ArrayList<TransportView>();
    for(Transport t : _transports){
      _tviews.add(t.toTransportView());
    }
    return _tviews;
	}

	public void checkBestTransporter (ArrayList<JobView> jobs, Transport transport){
		int min = 101;
		JobView bestJob = null;
    for(JobView job : jobs){
      if(job.getJobPrice() < min){
        transport.setTransporterCompany(job.getCompanyName());
        transport.setPrice(job.getJobPrice());
        transport.setId(job.getJobIdentifier());
        min = job.getJobPrice();
      }
    }
  }

	public void confirmJob(ArrayList<JobView> jobs, Transport transport){
		for (JobView job : jobs){
			try{
				if (transport.getTransporterCompany().equals(job.getCompanyName()))
				transporters.get(job.getCompanyName()).decideJob(job.getJobIdentifier(), true);

        else transporters.get(job.getCompanyName()).decideJob(job.getJobIdentifier(), false);

      } catch (BadJobFault_Exception e){
        e.printStackTrace();
      }
    }
  }

  public void rejectAllOptions (ArrayList<JobView> jobs){
    for (JobView job : jobs){
      try{
        transporters.get(job.getCompanyName()).decideJob(job.getJobIdentifier(), false);
      } catch (BadJobFault_Exception e){
        e.printStackTrace();
      }
    }
  }

  public ArrayList<JobView> makeRequests(String origin, String destination, int price)
    throws InvalidPriceFault_Exception, UnknownLocationFault_Exception{
    ArrayList<JobView> _jobs = new ArrayList<JobView>();
    for (Map.Entry<String, TransporterClient> entry : transporters.entrySet()){
      try{
        JobView proposedJob = entry.getValue().requestJob(origin, destination, price);
        if (proposedJob != null) _jobs.add(proposedJob);
      }
      catch (BadLocationFault_Exception e) {
        UnknownLocationFault ulf = new UnknownLocationFault();
        ulf.setLocation(e.getFaultInfo().getLocation());
        throw new UnknownLocationFault_Exception("Invalid location:", ulf);
      }
      catch (BadPriceFault_Exception e) {
        InvalidPriceFault ipf = new InvalidPriceFault();
        ipf.setPrice(e.getFaultInfo().getPrice());
        throw new InvalidPriceFault_Exception("Invalid price:", ipf);
      }
    }
    return _jobs;
  }


	public String requestTransport(String origin, String destination, int priceMax)
	throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
    transporters.clear();
    try{
      connectToTransporters();
    } catch(JAXRException e) {e.printStackTrace();}

    Location l_origin = Location.fromValue(origin);
    Location l_destination = Location.fromValue(destination);

    if(l_origin == null){
      UnknownLocationFault ulf = new UnknownLocationFault();
      ulf.setLocation(origin);
      throw new UnknownLocationFault_Exception("Invalid origin:", ulf);
    }

    if(l_destination == null){
      UnknownLocationFault ulf = new UnknownLocationFault();
      ulf.setLocation(destination);
      throw new UnknownLocationFault_Exception("Invalid destination:", ulf);
    }

    if(priceMax <= 0) {
      InvalidPriceFault ipf = new InvalidPriceFault();
      ipf.setPrice(priceMax);
      throw new InvalidPriceFault_Exception("Invalid price:", ipf);
    }

		Transport newTransport = new Transport(origin, destination, priceMax, TransportStateView.REQUESTED);

    ArrayList<JobView> _jobs = makeRequests(origin, destination, priceMax);

    if(_jobs.isEmpty()){
			newTransport.setState(TransportStateView.FAILED);
      UnavailableTransportFault utf = new UnavailableTransportFault();
      utf.setOrigin(origin);
      utf.setDestination(destination);
			throw new UnavailableTransportFault_Exception("No Transport Available", utf);
		}

		checkBestTransporter(_jobs, newTransport);

		newTransport.setState(TransportStateView.BUDGETED);

		if(newTransport.getPrice() > priceMax){
			rejectAllOptions(_jobs);
			newTransport.setState(TransportStateView.FAILED);
      UnavailableTransportPriceFault utpf = new UnavailableTransportPriceFault();
      utpf.setBestPriceFound(newTransport.getPrice());
			throw new UnavailableTransportPriceFault_Exception("Price above maximum given by client", utpf);
		}

		else{
			confirmJob(_jobs, newTransport);
			newTransport.setState(TransportStateView.BOOKED);
		}
		_transports.add(newTransport);
		return newTransport.getId();
	}


}
