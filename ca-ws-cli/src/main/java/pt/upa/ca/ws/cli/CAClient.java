package pt.upa.ca.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.upa.ca.ws.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class CAClient {

  private CA _port;


	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", CAClient.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];

    CAClient client = new CAClient();
    client.connect();
	}
  
  public void connect() throws Exception {
    String uddiURL = "http://localhost:9090";
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

    String name = "CA";
		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);

		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		CAImplService service = new CAImplService();
		_port = service.getCAImplPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
  }

  public String requestCertificate(String name){
    return _port.requestCertificate(name);
  } 
}
