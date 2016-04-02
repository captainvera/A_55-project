package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

    TransporterClient tc = new TransporterClient();

    tc.connectToTransporter("UpaTransporter1");
    tc.ping();
	}
}
