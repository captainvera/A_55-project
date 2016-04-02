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

  public String ping(String name) {
    return "Returning ping from " + name;
  }

  public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
    return new JobView(); 
  }
  
  public JobView decideJob(String id, boolean accept){
    return new JobView();
  }
  
  public JobView jobStatus(String id){
    return new JobView();
  }

  public List<JobView> listJobs(){
    return new ArrayList<JobView>();
  }

  public void clearJobs(){
  
  }

}
