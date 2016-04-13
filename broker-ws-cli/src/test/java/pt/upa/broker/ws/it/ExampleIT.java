package pt.upa.broker.ws.it;

import org.junit.*;
import static org.junit.Assert.*;

import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.*;
import pt.upa.broker.*;

/**
 *  Integration Test example
 *
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers
 */
public class ExampleIT {

    // static members
    private static BrokerClient client;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
      client = new BrokerClient();
      try{
        client.connectToBroker("UpaBroker");
      } catch(Exception e){e.printStackTrace();}
    }

    @AfterClass
    public static void oneTimeTearDown() {
      client = null;
    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
      client.clearTransports();
    }


    // tests

    @Test(expected=InvalidPriceFault_Exception.class)
    public void testInvalidPrice() throws Exception{
      System.out.println("[TEST] Invalid Price ");
      client.requestTransport("Leiria", "Lisboa", 0);
        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

    @Test(expected=UnknownLocationFault_Exception.class)
    public void testInvalidOrigin() throws Exception{
      System.out.println("[TEST] Invalid Origin ");
      client.requestTransport("Munique", "Lisboa", 10);
    }

    @Test(expected=UnknownLocationFault_Exception.class)
    public void testInvalidDestination() throws Exception{
      System.out.println("[TEST] Invalid Destination ");
      client.requestTransport("Lisboa", "Munique", 10);
    }

    @Test(expected=UnavailableTransportFault_Exception.class)
    public void testUnavailableTransport() throws Exception{
      System.out.println("[TEST] Unavailable Transport ");
      client.requestTransport("Beja", "Porto", 10);
    }

    @Test
    public void sucessRequest() throws Exception{
      System.out.println("[TEST] Sucess ");
      String id = client.requestTransport("Lisboa", "Porto", 10);
      assertEquals(id, "Lisboa" + "T" + "Porto" + "ID0");
    }

}
