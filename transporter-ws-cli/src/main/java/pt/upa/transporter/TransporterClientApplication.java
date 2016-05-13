package pt.upa.transporter;

import java.util.List;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.transporter.ws.*;
import pt.upa.ws.Security;
import java.security.cert.Certificate;

import pt.upa.ws.Context;

public class TransporterClientApplication {

  public static void main(String[] args) throws Exception {
    System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

    /**
     * Very basic example execution for a TransporterClient
     */
    TransporterClient tc = new TransporterClient("Broker");
    try{
      Certificate c = Security.getCertificateFromCA("UpaTransporter1");
      System.out.println("Got certificate: " + c.toString());
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
        System.out.println("A transportadora nao trabalha nesta zona.");
      }

      System.out.printf("[REQUEST] Lisboa->Leiria for 80%n");
      JobView jv2 = tc.requestJob("Lisboa", "Leiria", 80);
      System.out.printf("[RESPONSE] ID: %s | Price:  %d%n", jv2.getJobIdentifier(), jv2.getJobPrice());
      System.out.printf("[REQUEST] List all jobs");
      List<JobView> jobs = tc.listJobs();
      for(JobView j : jobs){
        System.out.printf("[RESPONSE] %s | STATE: %s | PRICE: %d%n", j.getJobIdentifier(), j.getJobState().value(), j.getJobPrice());
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
