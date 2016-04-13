package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.xml.registry.JAXRException;

import mockit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class ExampleTest{

	final static JobView jv1 = new JobView();
	final static JobView jv2 = new JobView();
	static BrokerPort broker;
	
	@Mocked UDDINaming uddi;
	
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
    	jv1.setCompanyName("UpaTransporter1");
      String identifier = "Lisboa" + "T" + "Leiria" + "ID" + "1";
      jv1.setJobIdentifier(identifier);
      jv1.setJobOrigin("Lisboa");
      jv1.setJobDestination("Leiria");
      jv1.setJobPrice(10);
      jv1.setJobState(JobStateView.PROPOSED);
      
      jv2.setCompanyName("UpaTransporter1");
      String identifier1 = "Leiria" + "T" + "Coimbra" + "ID" + "2";
      jv2.setJobIdentifier(identifier1);
      jv2.setJobOrigin("Leiria");
      jv2.setJobDestination("Coimbra");
      jv2.setJobPrice(15);
      jv2.setJobState(JobStateView.PROPOSED);
      
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() throws Exception {

//    	new StrictExpectations() {{
//    		uddi.lookup(anyString); 
//    		result = "String";
//    		uddi.lookup(anyString); 
//    		result = "String";
//    		uddi.lookup(anyString); 
//    		result = null;
//    	}};
      broker = new BrokerPort();
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void test() {
    	
    	System.out.println("WOOW");

        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

}