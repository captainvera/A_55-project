!package pt.upa.transporter.ws.cli;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.transporter.ws.*;

public class TransporterClient {

  TransporterPortType _transporter;

  public TransporterClient() {
  }

  public void ping() {
    System.out.printf("Pinging transporter%nGot: \"%s%n", _transporter.ping("name")+"\"");
  }
 
  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
    JobView response = null;
    response = _transporter.requestJob(origin, destination, price);
    return response;
  } 

  public void connectToTransporter(String name) throws Exception {
    String uddiURL = "http://localhost:9090";

    System.out.printf("Connecting to %s%n Contacting UDDI at %s%n", name, uddiURL);
    UDDINaming uddiNaming = new UDDINaming(uddiURL);

    System.out.printf("Looking for '%s'%n", name);
    String endpointAddress = uddiNaming.lookup(name);

    if (endpointAddress == null) {
      System.out.println("Transporter not found!");
      return;
    } else {
      System.out.printf("Found %s%n", endpointAddress);
    }

    TransporterPortType port = null;

    TransporterService service = new TransporterService();
    port = service.getTransporterPort();		

    System.out.println("Setting endpoint address ...");
    BindingProvider bindingProvider = (BindingProvider) port;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();
    requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

    System.out.println("Connection succesful");
    _transporter = port;
  }

  public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
    JobView response = null;
    response = _transporter.decideJob(id, accept);
    return response;
  }

  public JobView jobStatus(String id){
    return _transporter.jobStatus(id);
  } 
  
  public void clearJobs(){
    _transporter.clearJobs();
  }

  public List<JobView> listJobs() {
    return _transporter.listJobs();
  } 
}
