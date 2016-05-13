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

  private String _url;
  private final int maxRetries = 4;
  public BrokerClient(){

  }



  public void connectToBroker(String name) throws Exception{

    String uURL = "http://localhost:9090";
    UDDINaming uddiNaming = new UDDINaming(uURL);
    String epAddress = uddiNaming.lookup(name);
  
    _url = epAddress;
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

    int connectionTimeout = 2000;
    final List<String> CONN_TIME_PROPS = new ArrayList<String>();
    CONN_TIME_PROPS.add("com.sun.xml.ws.connect.timeout");
    CONN_TIME_PROPS.add("com.sun.xml.internal.ws.connect.timeout");
    CONN_TIME_PROPS.add("javax.xml.ws.client.connectionTimeout");

    // Set timeout until a connection is established (unit is milliseconds; 0 means infinite)
    for (String propName : CONN_TIME_PROPS)
      requestContext.put(propName, connectionTimeout);
    System.out.printf("Set connection timeout to %d milliseconds%n", connectionTimeout);

    int receiveTimeout = 30000;
    final List<String> RECV_TIME_PROPS = new ArrayList<String>();
    RECV_TIME_PROPS.add("com.sun.xml.ws.request.timeout");
    RECV_TIME_PROPS.add("com.sun.xml.internal.ws.request.timeout");
    RECV_TIME_PROPS.add("javax.xml.ws.client.receiveTimeout");

    // Set timeout until the response is received (unit is milliseconds; 0 means infinite)
    for (String propName : RECV_TIME_PROPS)
      requestContext.put(propName, receiveTimeout);
    System.out.printf("Set receive timeout to %d milliseconds%n", receiveTimeout);

    System.out.printf("Connection to %s succesfull%n", name);
  }

  public String ping(String name){
    int tries = 0; 
    while(tries < maxRetries){
      try{
        return broker.ping(name);
      } catch(WebServiceException wse){
        tries++;
        System.out.println("Connection Error.\nAttempting to reconect\n");
        handleURLChange();
        try{
          Thread.sleep(2000);
        } catch(Exception e){ e.printStackTrace();}
      }
    }
    return null;
  }
  public void clearTransports(){
    System.out.println("Clearing all Jobs...");
    broker.clearTransports();
    System.out.println("Jobs cleared for all Transporters");

  }

  public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
    System.out.println("Retrieving transport with " + id);
    TransportView response = null;
    response = broker.viewTransport(id);
    return response;
  }

  public String requestTransport(String origin, String destination, int priceMax)
    throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
                    UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
             System.out.println("Request Received Origin: " + origin + " Destination: " + destination);
             String response = null;
             response = broker.requestTransport(origin, destination, priceMax);
             return response;
  }

  public List<TransportView> listTransports(){
    return broker.listTransports();
  }

  private void handleURLChange() {
    try{
      String uURL = "http://localhost:9090";
      UDDINaming uddiNaming = new UDDINaming(uURL);
      String epAddress = uddiNaming.lookup("UpaBroker");
      System.out.println(epAddress + "  ::  " + _url);
      if(epAddress != null && epAddress != _url ){
        _url = epAddress;
        connectToBroker("UpaBroker");
      }
    } catch (Exception e) {e.printStackTrace();}


  }
}
