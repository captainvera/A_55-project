package pt.upa.transporter;

import javax.xml.ws.Endpoint;

import pt.upa.transporter.ws.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.ws.Context;

public class TransporterApplication {

  private String _url, _name;
  private Endpoint _endpoint;
  private UDDINaming _uddiNaming;

  public void createServer(String uddiURL, String name, String url) throws Exception {
    _endpoint = null;
    _uddiNaming = null;
    _url = url;
    _name = name;
    _endpoint = Endpoint.create(new TransporterPort(name));

    // publish endpoint
    System.out.printf("Starting %s%n", url);
    _endpoint.publish(url);

    // publish to UDDI
    System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
    _uddiNaming = new UDDINaming(uddiURL);
    _uddiNaming.rebind(name, url);
  }

  public void runServer() throws Exception {
    System.out.println("Awaiting connections");
    System.out.println("Press enter to shutdown");
    System.in.read();
  }

  public void closeServer() throws Exception { 
    try {
      if (_endpoint != null) {
        // stop endpoint
        _endpoint.stop();
        System.out.printf("Stopped %s%n", _url);
      }
    } catch (Exception e) {
      System.out.printf("Caught exception when stopping: %s%n", e);
    }
    try {
      if (_uddiNaming != null) {
        // delete from UDDI
        _uddiNaming.unbind(_name);
        System.out.printf("Deleted '%s' from UDDI%n", _name);
      }
    } catch (Exception e) {
      System.out.printf("Caught exception when deleting: %s%n", e);
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println(TransporterApplication.class.getSimpleName() + " starting...");

    if (args.length < 3) {
      System.err.println("Argument(s) missing!");
      System.err.printf("Usage: java %s uddiURL wsName wsURL%n",TransporterApplication.class.getName());
      return;
    }

    String uddiURL = args[0];
    String name = args[1];
    String url = args[2];

    Context.WS_IDENTIFIER = name ;
    Context.WS_KEYSTORE_FILE = "keys/" + name + ".jks";

    TransporterApplication tc = new TransporterApplication();
    try{
      tc.createServer(uddiURL, name, url); 
      tc.runServer();
    } catch(Exception e){
      System.out.printf("Caught exception when creating the server: %s%n", e);
    } finally {
      tc.closeServer();    
    }    
  }
}
