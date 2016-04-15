package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.JAXRException;

import mockit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;


public class BrokerUnitTest{

	static JobView jv1 = new JobView();
	static JobView jv2 = new JobView();
	static BrokerPort broker;
	static ArrayList<String> _endpoints = new ArrayList<String>();
	@Mocked UDDINaming uddi;
	@Mocked TransporterClient client;

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		jv1.setCompanyName("UpaTransporter1");
		String identifier = "Lisboa" + "T" + "Leiria" + "ID" + "1";
		jv1.setJobIdentifier(identifier);
		jv1.setJobOrigin("Leiria");
		jv1.setJobDestination("Lisboa");
		jv1.setJobPrice(50);
		jv1.setJobState(JobStateView.PROPOSED);

		jv2.setCompanyName("UpaTransporter1");
		String identifier1 = "Leiria" + "T" + "Coimbra" + "ID" + "2";
		jv2.setJobIdentifier(identifier1);
		jv2.setJobOrigin("Leiria");
		jv2.setJobDestination("Coimbra");
		jv2.setJobPrice(15);
		jv2.setJobState(JobStateView.PROPOSED);

		_endpoints.add("String for first transporter");
	}

	@AfterClass
	public static void oneTimeTearDown() {

		_endpoints = null;

	}

	// initialization and clean-up for each test
	@Before
	public void setUp() throws Exception {

		new StrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
		}};

		broker = new BrokerPort();
	}

	@After
	public void tearDown() {
	}

	public void populate() throws Exception{
		System.out.println("[TEST] Price too high ");
		new NonStrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
			client.requestJob("Leiria", "Lisboa", 50);
			result = jv1;
			client.decideJob(anyString, true);
		}};

		broker.requestTransport("Leiria", "Lisboa", 50);
		broker.requestTransport("Leiria", "Lisboa", 50);
		broker.requestTransport("Leiria", "Lisboa", 50);

		
	}
	
	@Test
	public void testsuccess() throws Exception{
		new StrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
			client.requestJob("Leiria", "Lisboa", 50);
			result = jv1;
			client.decideJob("Lisboa" + "T" + "Leiria" + "ID" + "1", true);
			result = jv1;
			client.jobStatus("Lisboa" + "T" + "Leiria" + "ID" + "1");
			result = jv1;
		}};
		
		String identifier = broker.requestTransport("Leiria", "Lisboa", 50);
		TransportView transport = broker.viewTransport(identifier);
    assertEquals(transport.getOrigin(), "Leiria");
    assertEquals(transport.getDestination(), "Lisboa");
    assertEquals(transport.getState(), TransportStateView.BOOKED);

	}

	/*
	 * Testing Exception handling
	 */
	@Test(expected=InvalidPriceFault_Exception.class)
	public void testInvalidPrice() throws Exception{
		System.out.println("[TEST] Invalid Price ");
		new StrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
		}};
		broker.requestTransport("Leiria", "Lisboa", 0);
	}

	@Test(expected=UnknownLocationFault_Exception.class)
	public void testUnknownOrigin() throws Exception{
		System.out.println("[TEST] Unknown Origin");
		new StrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
		}};
		broker.requestTransport("Munique", "Lisboa", 10);
	}

	@Test(expected=UnknownLocationFault_Exception.class)
	public void testUnknownDestination() throws Exception{
		System.out.println("[TEST] Unknown Destination ");
		new StrictExpectations() {{
			new UDDINaming(anyString);

			uddi.list("UpaTransporter%"); 
			result = _endpoints;

			new TransporterClient();

			client.connectToTransporterByURI(anyString);
			client.ping();
			result = "UpaTransporter1";
		}};
		broker.requestTransport("Lisboa", "Munique", 10);
	}

	@Test(expected=UnavailableTransportFault_Exception.class)
	public void testUnavailableTransport() throws Exception{
		System.out.println("[TEST] Unavailable Transport ");
		new StrictExpectations() {{
			new UDDINaming(anyString);
  		uddi.list("UpaTransporter%"); 
  		result = _endpoints;
  		new TransporterClient();
  		client.connectToTransporterByURI(anyString);
  		client.ping();
  		result = "UpaTransporter1";
  		
			client.requestJob("Beja", "Porto", 10);
			result = new UnavailableTransportFault_Exception("Fabrication", null);
		}};
		broker.requestTransport("Beja", "Porto", 10);
	}
	
	@Test(expected=UnavailableTransportFault_Exception.class)
  public void testPriceTooHigh() throws Exception{
    System.out.println("[TEST] Price too high ");
    new StrictExpectations() {{
			new UDDINaming(anyString);
  		
  		uddi.list("UpaTransporter%"); 
  		result = _endpoints;
  		
  		new TransporterClient();
  		
  		client.connectToTransporterByURI(anyString);
  		client.ping();
  		result = "UpaTransporter1";
			client.requestJob("Beja", "Lisboa", 101);
			result = null;
		}};
    broker.requestTransport("Beja", "Lisboa", 101);
  }
  
	@Test
	public void testListJobs() throws Exception{

		populate();
		List<TransportView> transps = broker.listTransports();
		assertEquals(transps.size(), 3);

	}
	
	@Test
	public void clearTransports(){
		System.out.println("[TEST] Price too high ");
		new StrictExpectations() {{
			client.clearJobs();
		}};

		broker.clearTransports();

	}


}