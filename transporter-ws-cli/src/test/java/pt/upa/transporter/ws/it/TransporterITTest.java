package pt.upa.transporter.ws;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.List;

import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.*;
import pt.upa.transporter.*;
/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class TransporterITTest {

  // static members
  public static TransporterClient _client, _client_odd, _client_even;

  // one-time initialization and clean-up

  @BeforeClass
  public static void oneTimeSetUp() {
    try {
      _client = new TransporterClient();
      _client_odd = new TransporterClient();
      _client_even = new TransporterClient();

      _client.connectToTransporter("UpaTransporter1");
      _client_odd.connectToTransporter("UpaTransporter1");
      _client_even.connectToTransporter("UpaTransporter2");
    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  @AfterClass
  public static void oneTimeTearDown() {
  }


  // members


  // initialization and clean-up for each test

  @Before
  public void setUp() {

  }

  @After
  public void tearDown() {
    _client_odd.clearJobs();
    _client_even.clearJobs();
    _client.clearJobs();
  }


  // tests

  @Test
  public void testRequestJob() {
    try {
      int price = 101;
      String origin = "Leiria";
      String destination ="Lisboa";

      System.out.println("[TEST] Testing requestJob() for even and odd transporters");

      System.out.println("[TEST] Null job because of price");
      assertNull(_client_odd.requestJob(origin,destination,price));
      assertNull(_client_even.requestJob(origin,destination,price));

      System.out.println("[TEST] Regular job");
      price = 50; 
      JobView jv_odd = _client_odd.requestJob(origin,destination,price);
      JobView jv_even = _client_even.requestJob(origin,destination,price);

      assertTrue(jv_odd.getJobPrice() > price);
      assertTrue(jv_even.getJobPrice() < price);

      assertEquals(jv_odd.getJobOrigin(), origin);    
      assertEquals(jv_odd.getJobDestination(), destination);
      assertEquals(jv_odd.getJobState(), JobStateView.PROPOSED);

      System.out.println("[TEST] Null job because of destination");
      origin = "Porto";
      destination = "Faro";
      assertNull(_client_odd.requestJob(origin,destination,price));
      assertNull(_client_even.requestJob(origin,destination,price));

      System.out.println("[TEST] Request Job tests completed successfuly");
    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testDecideJob() {
    try{
      int price = 50;
      String origin = "Leiria";
      String destination ="Lisboa";

      System.out.println("[TEST] Testing decideJob() for even and odd transporters");

      JobView jv_odd = _client_odd.requestJob(origin,destination,price);
      JobView jv_even = _client_even.requestJob(origin,destination,price);

      jv_odd = _client_odd.decideJob(jv_odd.getJobIdentifier(), true);
      jv_even = _client_even.decideJob(jv_even.getJobIdentifier(), true);

      assertEquals(jv_odd.getJobState(), JobStateView.ACCEPTED);
      assertEquals(jv_even.getJobState(), JobStateView.ACCEPTED);

      jv_odd = _client_odd.requestJob(origin,destination,price);
      jv_even = _client_even.requestJob(origin,destination,price);

      jv_odd = _client_odd.decideJob(jv_odd.getJobIdentifier(), false);
      jv_even = _client_even.decideJob(jv_even.getJobIdentifier(), false);

      assertEquals(jv_odd.getJobState(), JobStateView.REJECTED);
      assertEquals(jv_even.getJobState(), JobStateView.REJECTED);

    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  } 

  @Test(expected = BadJobFault_Exception.class)
  public void testDecideJobTrueException() throws Exception {
    _client.decideJob("FAKEID", true);
  }

  @Test(expected = BadJobFault_Exception.class)
  public void testDecideJobFalseException() throws Exception {
    _client.decideJob("FAKEID", false);
  }

  @Test
  public void testJobStatus(){
    System.out.println("[TEST] Testing jobStatus()");
    try{
      JobView jv = _client.requestJob("Leiria", "Lisboa", 50);
      String id = jv.getJobIdentifier();
      JobView jv_response = _client.jobStatus(id);
      assertEquals(jv_response.getJobState(), JobStateView.PROPOSED);

      _client.decideJob(id, true);
      jv_response = _client.jobStatus(id);

      assertEquals(jv_response.getJobState(), JobStateView.ACCEPTED);
      
      jv_response = _client.jobStatus("FAKEID"); 
      assertNull(jv_response);

    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testListJobs(){
    try{
      _client.requestJob("Leiria", "Lisboa", 50);
      _client.requestJob("Lisboa", "Coimbra", 10);
      _client.requestJob("Coimbra", "Leiria", 5);
      List<JobView> jobs = _client.listJobs();
      assertEquals(jobs.size(), 3);
    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test 
  public void testClearJobs(){
    try{
      _client.requestJob("Leiria", "Lisboa", 50);
      _client.requestJob("Leiria", "Coimbra", 50);
      _client.requestJob("Coimbra", "Lisboa", 50);
      _client.clearJobs();
      List<JobView> jobs = _client.listJobs();
      assertEquals(jobs.size(), 0);
    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testJobSimulation(){
    try{

    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOriginEven() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on origin for even transporters");
      _client_even.requestJob("Leiria","ABCD",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOriginOdd() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on origin for odd transporters");
      _client_even.requestJob("Leiria","ABCD",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestinationEven() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on destination for even transporters");
      _client_even.requestJob("ABCD","Leiria",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestinationOdd() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on destination for odd transporters");
      _client_even.requestJob("ABCD","Leiria",50); 
    }

  @Test(expected = BadPriceFault_Exception.class)
    public void testWrongPriceEven() throws Exception {
      System.out.println("[TEST] Testing BadPriceFault_Exception for even transporters");
      _client_even.requestJob("Leiria","Lisboa",-20); 
    }

  @Test(expected = BadPriceFault_Exception.class)
    public void testWrongPriceOdd() throws Exception {
      System.out.println("[TEST] Testing BadPriceFault_Exception for odd transporters");
      _client_even.requestJob("Leiria","Lisboa",-20); 
    }

}
