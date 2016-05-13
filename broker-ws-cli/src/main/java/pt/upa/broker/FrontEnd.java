package pt.upa.broker;

import pt.upa.Shell;
import pt.upa.Command;

import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

import java.util.List;

public class FrontEnd extends Shell {

  private BrokerClient _brokerClient;
  private boolean _connected = false;

  public static void main(String[] args) throws Exception {
    FrontEnd sh = new FrontEnd();
    sh.execute();
  }

  public void printTransportView(TransportView tv){
    System.out.println("---[TransportView] ID: " + tv.getId());
    System.out.println("Origin: " + tv.getOrigin() + " | Destination: " + tv.getDestination() + " | Price:" + tv.getPrice());
  }

  public FrontEnd(){
    super("FrontEnd");
    _brokerClient = new BrokerClient();

    new Command(this, "quit", "exit shell") {
      public void execute(String[] args) {
	      try {
          System.out.println("Closing FrontEnd... Goodbye!");
          System.exit(0);
	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };

    new Command(this, "connect", "request transport") { 
      @Override
      public void execute(String[] args) {
	      try {
          if(args.length > 0) System.out.println("Ignoring arguments");
          _brokerClient.connectToBroker("UpaBroker"); 
          _connected = true;
	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };

    new Command(this, "view", "request transport") { 
      @Override
      public void execute(String[] args) {
	      try {
          if(!_connected){
            System.out.println("Please connect first");
            throw new RuntimeException();
          }
          if(args.length != 1 ){
            System.out.println("Wrong number of arguments! Expected 1");
            throw new RuntimeException();
          } 
          TransportView tv = _brokerClient.viewTransport(args[0]);
          printTransportView(tv);
	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };

    new Command(this, "request", "request transport") { 
      @Override
      public void execute(String[] args) {
	      try {
          if(!_connected){
            System.out.println("Please connect first");
            throw new RuntimeException();
          }
          if(args.length != 3 ){
            System.out.println("Wrong number of arguments! Expected 3");
            throw new RuntimeException();
          } 
          String tv = _brokerClient.requestTransport(args[0],args[1],Integer.parseInt(args[2]));
          System.out.println(tv);

	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };

    new Command(this, "list", "request transport") { 
      @Override
      public void execute(String[] args) {
	      try {
          if(!_connected){
            System.out.println("Please connect first");
            throw new RuntimeException();
          }
          if(args.length > 0 ){
            System.out.println("Ignoring arguments");
          } 
          List<TransportView> ltv = _brokerClient.listTransports();
          for(TransportView tv : ltv)
            printTransportView(tv);
           
	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };

    new Command(this, "clear", "request transport") { 
      @Override
      public void execute(String[] args) {
	      try {
          if(!_connected){
            System.out.println("Please connect first");
            throw new RuntimeException();
          }
          if(args.length > 0 ){
            System.out.println("Ignoring arguments");
          } 
          _brokerClient.clearTransports();
	      } catch (Exception e) { throw new RuntimeException(""+e); }
      }
    };
  }
}
