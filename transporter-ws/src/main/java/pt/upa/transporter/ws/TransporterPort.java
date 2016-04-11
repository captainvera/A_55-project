package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import pt.upa.broker.ws.BrokerPort.Location; 

@WebService(
  endpointInterface="pt.upa.transporter.ws.TransporterPortType",
  wsdlLocation="transporter.1_0.wsdl",
  name="UpaTransporter1",
  portName="TransporterPort",
  targetNamespace="http://ws.transporter.upa.pt/",
  serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType {

  protected ArrayList<JobView> _jobs;
  protected String _name;
  protected int _num;
  protected int _idCounter;
  protected boolean _even;

  protected JobView getJobByIdentifier(String identifier){
    for(JobView j : _jobs){
      if(j.getJobIdentifier().equals(identifier)){
        return j;
      }
    }
    return null;
  }

  public TransporterPort(String name) {
    _idCounter = 0;
    _name = name;
    _num = Character.getNumericValue(name.charAt(name.length() - 1));

    
    _even = (_num%2==0);

    if(_num%2==0) _even = true;
    else _even = false;
    
    _jobs = new ArrayList<JobView>();
    System.out.printf("Created TransporterPort with name %s and last digit %d%n", _name, _num);
  }

  public String ping(String name) {
    return _name + " returning ping from " + name;
  }

  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
  
    /**
     * Location checking
     */

    Location l_origin = Location.fromValue(origin);
    Location l_destination = Location.fromValue(destination);

    if(l_origin == null){ 
      BadLocationFault blf = new BadLocationFault();
      blf.setLocation(origin);
      throw new BadLocationFault_Exception("Invalid origin:", blf);
    }

    if(l_destination == null){ 
      BadLocationFault blf = new BadLocationFault();
      blf.setLocation(destination);
      throw new BadLocationFault_Exception("Invalid location:", blf);
    }    
    
    if(!_even){
      if(l_origin.NORTE()){ 
        return null;
      }else if(l_destination.NORTE()){
        return null;
      } 
    }else{
      if(l_origin.SUL()){ 
        return null;
      }else if(l_destination.SUL()){
        return null;
      } 
    } 
    
    /**
     * Price checking
     */

    if(price < 0) {
      BadPriceFault bpf = new BadPriceFault();
      bpf.setPrice(price);
      throw new BadPriceFault_Exception("Invalid price:", bpf);
    }

    if(price > 100){
      return null;
    }else if(price <= 10){
      price = (price == 0) ? 0 : price-1; 
    }else{
      if(price%2==0){
        price = (_even) ? price-10 : price+10;
      }else{
        price = (_even) ? price+10 : price-10;
      }
    }
   
    /**
     * Response message construction
     */ 

    JobView jv = new JobView();
    jv.setCompanyName(_name);
    String identifier = origin + "T" + destination + "ID" + _idCounter;
    jv.setJobIdentifier(identifier);
    _idCounter++;
    jv.setJobOrigin(origin);
    jv.setJobDestination(destination);
    jv.setJobPrice(price);
    jv.setJobState(JobStateView.PROPOSED);

    _jobs.add(jv);

    return jv;
  }
  
  public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
    JobView jv = getJobByIdentifier(id);
    if(jv == null){
      BadJobFault bjf = new BadJobFault();
      bjf.setId(id);
      throw new BadJobFault_Exception("Invalid job id:", bjf);
    }  
    if(accept){
      jv.setJobState(JobStateView.ACCEPTED);
    }else{
      jv.setJobState(JobStateView.REJECTED);
    }
    return jv;
  }
  
  public JobView jobStatus(String id){
    return getJobByIdentifier(id);
  }

  public List<JobView> listJobs(){
    return new ArrayList<JobView>(_jobs);
  }

  public void clearJobs(){
    _jobs.clear(); 
  }

}
