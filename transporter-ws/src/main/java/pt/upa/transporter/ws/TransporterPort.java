package pt.upa.transporter.ws;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

  protected ArrayList<Job> _jobs;
  protected String _name;
  protected int _num;
  protected int _idCounter;
  protected boolean _even;
  
  /**
   * -----------------------------------------------
   * Timer Logic
   */

  protected int randomInterval(int min, int max){
    Random rand = new Random();
    int t = rand.nextInt((max-min)+1)+min;
    return t;
  }

  protected void setHeadingInterval(Job j){
    Timer t = new Timer();

    t.schedule(new TimerTask(){
      @Override
      public void run() {
        j.setJobState(JobStateView.HEADING);
        setOngoingInterval(j);
      }
    }, randomInterval(1000,5000));
  }
  
  protected void setOngoingInterval(Job j){
    Timer t = new Timer();

    t.schedule(new TimerTask(){
      @Override
      public void run() {
        j.setJobState(JobStateView.ONGOING);
        setCompletedInterval(j);
      }
    }, randomInterval(1000,5000));
  }
  
  protected void setCompletedInterval(Job j){
    Timer t = new Timer();

    t.schedule(new TimerTask(){
      @Override
      public void run() {
        j.setJobState(JobStateView.COMPLETED);
      }
    }, randomInterval(1000,5000));
  }

  /*
   * ------------------------------------------------
   */

  protected Job getJobByIdentifier(String identifier){
    for(Job j : _jobs){
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
    
    _jobs = new ArrayList<Job>();
    System.out.printf("Created TransporterPort with name %s and last digit %d%n", _name, _num);
  }

  public String ping(String name) {
    return _name;
  }

  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
  
    /**
     * Location checking ------------------------------------------------------------
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
     * Price checking -----------------------------------------------------------------
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
     * Response message construction --------------------------------------------------
     */ 

    String identifier = origin + "T" + destination + "ID" + _idCounter;
    _idCounter++;

    Job jv = new Job(_name, identifier, origin, destination, price, JobStateView.PROPOSED);

    _jobs.add(jv);

    return jv.toJobView();
  }
  
  public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
    Job jv = getJobByIdentifier(id);
    if(jv == null || jv.getJobState() != JobStateView.PROPOSED){
      BadJobFault bjf = new BadJobFault();
      bjf.setId(id);
      throw new BadJobFault_Exception("Invalid job id:", bjf);
    }  
    if(accept){
      jv.setJobState(JobStateView.ACCEPTED);
      setHeadingInterval(jv);
    }else{
      jv.setJobState(JobStateView.REJECTED);
    }
    return jv.toJobView();
  }
  
  public JobView jobStatus(String id) {
    Job j = getJobByIdentifier(id);
    return (j == null) ? null : j.toJobView();
  }

  public List<JobView> listJobs(){
    ArrayList<JobView> _jviews = new ArrayList<JobView>();
    for(Job j : _jobs){
      _jviews.add(j.toJobView());
    }
    return _jviews;
  }

  public void clearJobs(){
    _jobs.clear(); 
  }

}
