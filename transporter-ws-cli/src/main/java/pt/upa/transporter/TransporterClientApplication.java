package pt.upa.transporter;

import java.util.List;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.transporter.ws.*;

public class TransporterClientApplication {

  public static void main(String[] args) throws Exception {
    System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

    TransporterClient tc = new TransporterClient();
    try{
      tc.connectToTransporter("UpaTransporter1");
      tc.ping();
      System.out.printf("[REQUEST] Portalegre->Lisboa for 10%n");
      JobView jv = tc.requestJob("Portalegre", "Lisboa", 10);
      if(jv != null){
        System.out.printf("[RESPONSE] ID: %s | Price:  %d%n", jv.getJobIdentifier(), jv.getJobPrice());

        System.out.printf("[REQUEST] Changing %s to ACCEPTED%n", jv.getJobIdentifier());
        tc.decideJob(jv.getJobIdentifier(), true);

        jv = tc.jobStatus(jv.getJobIdentifier());
        System.out.printf("[RESPONSE] %s state is %s%n", jv.getJobIdentifier(), jv.getJobState().value());
      }else{
        System.out.println("A transportadora nao trabalha neste dominio.");
      }
      System.out.printf("[REQUEST] Pousos->Leiria for 80%n");
      JobView jv2 = tc.requestJob("Pousos", "Leiria", 80);
      System.out.printf("[RESPONSE] ID: %s | Price:  %d%n", jv2.getJobIdentifier(), jv2.getJobPrice());
      System.out.printf("[REQUEST] List all jobs");
      List<JobView> jobs = tc.listJobs();
      for(JobView j : jobs){
        System.out.printf("[RESPONSE] %s | STATE: %s | PRICE: %d%n", j.getJobIdentifier(), j.getJobState().value(), j.getJobPrice());
      }
    }catch(Exception e){
      System.out.println("Something fucked up!" + e.getMessage());
    }
  }
}
