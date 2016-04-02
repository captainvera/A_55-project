package pt.upa.transporter;

import java.util.List;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.transporter.ws.*;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

    TransporterClient tc = new TransporterClient();

    tc.connectToTransporter("UpaTransporter1");
    tc.ping();
    System.out.printf("[REQUEST] Leiria->Lisboa for 10%n");
    JobView jv = tc.requestJob("Leiria", "Lisboa", 10);
    System.out.printf("[RESPONSE] ID: %s | Price:  %d%n", jv.getJobIdentifier(), jv.getJobPrice());

    System.out.printf("[REQUEST] Changing %s to ACCEPTED%n", jv.getJobIdentifier());
    tc.decideJob(jv.getJobIdentifier(), true);

    jv = tc.jobStatus(jv.getJobIdentifier());
    System.out.printf("[RESPONSE] %s state is %s%n", jv.getJobIdentifier(), jv.getJobState().value());
    System.out.printf("[REQUEST] Porto->Leiria for 80%n");
    JobView jv2 = tc.requestJob("Porto", "Leiria", 80);

    System.out.printf("[REQUEST] List all jobs");
    List<JobView> jobs = tc.listJobs();
    for(JobView j : jobs){
      System.out.printf("[RESPONSE] %s | STATE: %s | PRICE: %d%n", j.getJobIdentifier(), j.getJobState().value(), j.getJobPrice());
    }
	}
}
