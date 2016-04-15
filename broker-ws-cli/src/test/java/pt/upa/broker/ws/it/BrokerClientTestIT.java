package pt.upa.broker.ws.it;

import org.junit.*;
import static org.junit.Assert.*;

import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.*;
import pt.upa.broker.*;

import java.util.List;
import java.util.ArrayList;
/**
 *  Integration Test example
 *
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers
 */
public class BrokerClientTestIT {

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

    @Test(expected=UnavailableTransportFault_Exception.class)
    public void testUnavailableTransportBecausePrice() throws Exception{
      System.out.println("[TEST] Unavailable Transport Price");
      client.requestTransport("Beja", "Lisboa", 101);
    }

    @Test(expected=UnavailableTransportPriceFault_Exception.class)
    public void testUnavalaiblePrice() throws Exception{
      System.out.println("[TEST] Price High ");
      client.requestTransport("Porto", "Lisboa", 21);
    }

    @Test
    public void sucessRequest(){
      try {
        String origin = "Leiria";
        String destination ="Lisboa";

        System.out.println("[TEST] Regular job");
        int price = 50;
        String id = client.requestTransport(origin,destination,price);

        TransportView transport = client.viewTransport(id);
        assertEquals(transport.getOrigin(), origin);
        assertEquals(transport.getDestination(), destination);
        assertEquals(transport.getState(), TransportStateView.BOOKED);

        System.out.println("[TEST] Request Job tests completed successfuly");
      }catch(Exception e){
        fail("Failed test because of exception: " + e.getMessage());
      }
    }

    @Test(expected=UnknownTransportFault_Exception.class)
    public void testunknownTransport() throws Exception{
      System.out.println("[TEST] Unknown Transport ");
      client.viewTransport("failTransport");
    }


}
