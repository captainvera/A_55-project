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
public class TransporterTest {

  // static members
  public static TransporterApplication _transporter_odd, _transporter_even;
  public static TransporterClient _client, _client_odd, _client_even;

  // one-time initialization and clean-up

  @BeforeClass
  public static void oneTimeSetUp() {
    try {
      _transporter_odd = new TransporterApplication();
      _transporter_even = new TransporterApplication();

      _transporter_odd.createServer("http://localhost:9090",  "UpaTransporter1", "http://localhost:8081/transporter-ws/endpoint");
      _transporter_even.createServer("http://localhost:9090",  "UpaTransporter2", "http://localhost:8082/transporter-ws/endpoint");
      System.out.println("[TEST] Started odd and even servers");
      _client_odd = new TransporterClient();
      _client_even = new TransporterClient();
      _client = new TransporterClient();

      _client_odd.connectToTransporter("UpaTransporter1");
      _client_even.connectToTransporter("UpaTransporter2");
      _client.connectToTransporter("UpaTransporter1");
    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  @AfterClass
  public static void oneTimeTearDown() {
    try { 
      _transporter_even.closeServer();
      _transporter_odd.closeServer();
    }catch(Exception e ){
      System.out.println(e.getMessage());
    }
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
  }


  // tests

  @Test
  public void testPrice() {
    try {
      int price = 101;
      String origin = "Leiria";
      String destination ="Lisboa";

      System.out.println("[TEST] Testing price conditions for even and odd transporters");
      System.out.println("[TEST] price = 101");
      assertEquals(_client_odd.requestJob(origin,destination,price), null);
      assertEquals(_client_even.requestJob(origin,destination,price), null);

      System.out.println("[TEST] price = 100");
      price = 100;
      assertTrue(_client_odd.requestJob(origin,destination,price).getJobPrice() > price);
      assertTrue(_client_even.requestJob(origin,destination,price).getJobPrice() < price);

      System.out.println("[TEST] price = 99");
      price=99;
      assertTrue(_client_odd.requestJob(origin,destination,price).getJobPrice() < price);
      assertTrue(_client_even.requestJob(origin,destination,price).getJobPrice() > price);

      System.out.println("[TEST] price = 9");
      price=9;
      assertTrue(_client_odd.requestJob(origin,destination,price).getJobPrice() < price);
      assertTrue(_client_even.requestJob(origin,destination,price).getJobPrice() < price);

      System.out.println("[TEST] price = 0");
      price=0;
      assertEquals(_client_odd.requestJob(origin,destination,price).getJobPrice(), 0);
      assertEquals(_client_even.requestJob(origin,destination,price).getJobPrice(), 0);

      System.out.println("[TEST] Price tests completed successfuly");
    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testLocation() {
    try{
      int price = 50;
      String origin = "Leiria";
      String destination ="Lisboa";
      JobView jv_odd, jv_even;

      System.out.println("[TEST] Testing location conditions for even and odd transporters");

      System.out.println("[TEST] Leiria->Lisboa");
      jv_odd = _client_odd.requestJob(origin,destination,price); 
      jv_even = _client_even.requestJob(origin,destination,price);
      assertEquals(jv_odd.getJobOrigin(), "Leiria");
      assertEquals(jv_odd.getJobDestination(), "Lisboa");
      assertEquals(jv_even.getJobOrigin(), "Leiria");
      assertEquals(jv_even.getJobDestination(), "Lisboa");

      System.out.println("[TEST] Porto->Lisboa"); 
      origin = "Porto";
      destination = "Lisboa";
      jv_odd = _client_odd.requestJob(origin,destination,price); 
      jv_even = _client_even.requestJob(origin,destination,price);
      assertEquals(jv_odd, null);
      assertEquals(jv_even.getJobOrigin(), "Porto");
      assertEquals(jv_even.getJobDestination(), "Lisboa");

      System.out.println("[TEST] Portalegre->Leiria");
      origin = "Portalegre";
      destination = "Leiria";
      jv_odd = _client_odd.requestJob(origin,destination,price); 
      jv_even = _client_even.requestJob(origin,destination,price);
      assertEquals(jv_odd.getJobOrigin(), "Portalegre");
      assertEquals(jv_odd.getJobDestination(), "Leiria");
      assertEquals(jv_even, null);

      System.out.println("[TEST] Porto->Bragança");
      origin = "Porto";
      destination = "Braganca";
      jv_odd = _client_odd.requestJob(origin,destination,price); 
      jv_even = _client_even.requestJob(origin,destination,price);
      assertEquals(jv_odd, null);
      assertEquals(jv_even.getJobOrigin(), "Porto");
      assertEquals(jv_even.getJobDestination(), "Braganca");

      System.out.println("[TEST] Portalegre -> Faro");
      origin= "Portalegre";
      destination = "Faro";
      jv_odd = _client_odd.requestJob(origin,destination,price); 
      jv_even = _client_even.requestJob(origin,destination,price);
      assertEquals(jv_odd.getJobOrigin(), "Portalegre");
      assertEquals(jv_odd.getJobDestination(), "Faro");
      assertEquals(jv_even, null);

      System.out.println("[TEST] Location tests completed successfuly");
    }catch(Exception e){
      fail("Failed test because of exception: " + e.getMessage());
    }
  }

  @Test
  public void testDecideJob(){
    System.out.println("[TEST] Testing decideJob()");
    try{
      JobView jv = _client.requestJob("Leiria", "Lisboa", 50); 
      String id = jv.getJobIdentifier();
      jv = _client.decideJob(id, true);
      assertEquals(jv.getJobState(), JobStateView.ACCEPTED); 

      jv = _client.requestJob("Coimbra", "Lisboa", 50); 
      id = jv.getJobIdentifier();
      jv = _client.decideJob(id, false);
      assertEquals(jv.getJobState(), JobStateView.REJECTED);
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
