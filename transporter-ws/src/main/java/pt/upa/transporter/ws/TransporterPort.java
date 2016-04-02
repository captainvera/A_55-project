package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;

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
    _jobs = new ArrayList<JobView>();
    System.out.printf("Created TransporterPort with name %s and last digit %d%n", _name, _num);
  }

  public String ping(String name) {
    return "Returning ping from " + name;
  }

  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
    
    if(price > 100){
      return null;
    }else if(price <= 10){
      price = (price == 0) ? 0 : price-1; 
    }else{
      if(price%2 == 0){
         price = (_num%2==0) ? price-10 : price+10;
      }else{
        price = (_num%2==0) ? price+10 : price-10;
      }
    }
    
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
  
  public JobView decideJob(String id, boolean accept){
    JobView jv = getJobByIdentifier(id);
    if(jv == null){
      System.out.println("Requested Job not found!");
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
