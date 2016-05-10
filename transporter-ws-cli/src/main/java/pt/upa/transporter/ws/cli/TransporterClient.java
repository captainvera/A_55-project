package pt.upa.transporter.ws.cli;

import java.util.*;
import javax.xml.ws.*;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.transporter.ws.*;

import javax.xml.ws.handler.MessageContext;
import javax.annotation.Resource;

import pt.upa.Location;
import pt.upa.ws.handler.SecurityHandler;

public class TransporterClient {

  TransporterPortType _transporter;

  private String _name;
 
  private void setMessageContext(){
    BindingProvider bindingProvider = (BindingProvider) _transporter;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();

		requestContext.put(SecurityHandler.WS_IDENTIFIER, _name);
		requestContext.put(SecurityHandler.WS_KEYSTORE_FILE, "keys/"+_name+".jks");
		requestContext.put(SecurityHandler.WS_CERT_FILE, "keys/"+_name+".cer");
  }

  public TransporterClient() {
    _name = "UpaTransporter1";
  }

  public String ping() {
    setMessageContext();
		return _transporter.ping("name");
  }
 
  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
    setMessageContext();
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

  public void connectToTransporterByURI(String endp) throws Exception {
    TransporterPortType port = null;
    TransporterService service = new TransporterService();
    port = service.getTransporterPort();		
    System.out.println("Setting endpoint address ...");
    BindingProvider bindingProvider = (BindingProvider) port;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();
    requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endp);
    System.out.println("Connection succesful");
    _transporter = port;
  }
  
  public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
    setMessageContext();
    JobView response = null;
    response = _transporter.decideJob(id, accept);
    return response;
  }

  public JobView jobStatus(String id){
    setMessageContext();
    return _transporter.jobStatus(id);
  } 
  
  public void clearJobs(){
    setMessageContext();
    _transporter.clearJobs();
  }

  public List<JobView> listJobs() {
    setMessageContext();
    return _transporter.listJobs();
  } 
}
