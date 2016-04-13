package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import pt.upa.broker.ws.BrokerPort.Location; 

public class Job {

    protected String _companyName;
    protected String _jobIdentifier;
    protected String _jobOrigin;
    protected String _jobDestination;
    protected int _jobPrice;
    protected JobStateView _jobState;

    public Job(String companyName, String jobIdentifier, String jobOrigin,
               String jobDestination, int jobPrice, JobStateView jobState){
      _companyName = companyName;
      _jobIdentifier = jobIdentifier;
      _jobOrigin = jobOrigin;
      _jobDestination = jobDestination;
      _jobPrice = jobPrice;
      _jobState = jobState;
    }

    public Job(JobView jv){
      _companyName = jv.getCompanyName();
      _jobIdentifier = jv.getJobIdentifier();
      _jobOrigin = jv.getJobOrigin();
      _jobDestination = jv.getJobDestination();
      _jobPrice = jv.getJobPrice();
      _jobState = jv.getJobState(); 
    }

    public JobView toJobView() {
      JobView jv = new JobView();
      jv.setCompanyName(_companyName);
      jv.setJobIdentifier(_jobIdentifier);
      jv.setJobOrigin(_jobOrigin);
      jv.setJobDestination(_jobDestination);
      jv.setJobPrice(_jobPrice);
      jv.setJobState(_jobState);
      return jv;
    }
    
    public String getCompanyName() {
        return _companyName;
    }

    public void setCompanyName(String value) {
        this._companyName = value;
    }

    public String getJobIdentifier() {
        return _jobIdentifier;
    }

    public void setJobIdentifier(String value) {
        this._jobIdentifier = value;
    }

    public String getJobOrigin() {
        return _jobOrigin;
    }

    public void setJobOrigin(String value) {
        this._jobOrigin = value;
    }

    public String getJobDestination() {
        return _jobDestination;
    }

    public void setJobDestination(String value) {
        this._jobDestination = value;
    }

    public int getJobPrice() {
        return _jobPrice;
    }

    public void setJobPrice(int value) {
        this._jobPrice = value;
    }

    public JobStateView getJobState() {
        return _jobState;
    }

    public void setJobState(JobStateView value) {
        this._jobState = value;
    }

}
