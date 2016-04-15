package pt.upa.transporter.ws;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.List;

import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.*;
import pt.upa.transporter.*;

import java.lang.Thread;
/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class TransporterTest {

  // static members
  public static TransporterPort _transporter, _transporter_odd, _transporter_even;

  // one-time initialization and clean-up

  @BeforeClass
  public static void oneTimeSetUp() throws Exception {
    _transporter = new TransporterPort("UpaTransporter9");
    _transporter_odd = new TransporterPort("UpaTransporter1");
    _transporter_even = new TransporterPort("UpaTransporter2");
  }

  @AfterClass
  public static void oneTimeTearDown() throws Exception {
    _transporter_odd = null;
    _transporter_even = null;
  }


  // members


  // initialization and clean-up for each test

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
    _transporter_odd.clearJobs();
    _transporter_even.clearJobs();
    _transporter.clearJobs();
  }


  // tests

  @Test
  public void testPrice101() throws Exception {
    int price = 101;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] Testing price conditions for even and odd transporters");
    System.out.println("[TEST] price = 101");
    assertEquals(_transporter_odd.requestJob(origin,destination,price), null);
    assertEquals(_transporter_even.requestJob(origin,destination,price), null);
  }

  @Test
  public void testPrice100() throws Exception {
    int price = 100;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] price = 100");
    assertTrue(_transporter_odd.requestJob(origin,destination,price).getJobPrice() > price);
    assertTrue(_transporter_even.requestJob(origin,destination,price).getJobPrice() < price);
  }

  @Test
  public void testPrice99() throws Exception {
    int price = 99;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] price = 99");
    assertTrue(_transporter_odd.requestJob(origin,destination,price).getJobPrice() < price);
    assertTrue(_transporter_even.requestJob(origin,destination,price).getJobPrice() > price);
  }
  
  @Test
  public void testPrice11() throws Exception {
    int price = 11;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] price = 11");
    assertTrue(_transporter_odd.requestJob(origin,destination,price).getJobPrice() < price);
    assertTrue(_transporter_even.requestJob(origin,destination,price).getJobPrice() > price);
  }

  @Test
  public void testPrice10() throws Exception {
    int price = 10;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] price = 10");
    assertTrue(_transporter_odd.requestJob(origin,destination,price).getJobPrice() < price);
    assertTrue(_transporter_even.requestJob(origin,destination,price).getJobPrice() < price);
  } 

  @Test
  public void testPrice0() throws Exception {
    int price = 0;
    String origin = "Leiria";
    String destination ="Lisboa";

    System.out.println("[TEST] price = 0");
    assertEquals(_transporter_odd.requestJob(origin,destination,price).getJobPrice(), 0);
    assertEquals(_transporter_even.requestJob(origin,destination,price).getJobPrice(), 0);
  }


  @Test
  public void testLocationCenterCenter() throws Exception {
    int price = 50;
    String origin = "Leiria";
    String destination ="Lisboa";
    JobView jv_odd, jv_even;

    System.out.println("[TEST] CENTRO->CENTRO");
    jv_odd = _transporter_odd.requestJob(origin,destination,price); 
    jv_even = _transporter_even.requestJob(origin,destination,price);
    assertEquals(jv_odd.getJobOrigin(), "Leiria");
    assertEquals(jv_odd.getJobDestination(), "Lisboa");
    assertEquals(jv_even.getJobOrigin(), "Leiria");
    assertEquals(jv_even.getJobDestination(), "Lisboa");
  }

  @Test
  public void testLocationNorthCenter() throws Exception {
    int price = 50;
    String origin = "Porto";
    String destination = "Lisboa";
    JobView jv_odd, jv_even;

    System.out.println("[TEST] NORTE->CENTRO"); 
    jv_odd = _transporter_odd.requestJob(origin,destination,price); 
    jv_even = _transporter_even.requestJob(origin,destination,price);
    assertEquals(jv_odd, null);
    assertEquals(jv_even.getJobOrigin(), "Porto");
    assertEquals(jv_even.getJobDestination(), "Lisboa");
  }
  
  @Test
  public void testLocationSouthCenter() throws Exception {
    int price = 50;
    String origin = "Portalegre";
    String destination = "Leiria";
    JobView jv_odd, jv_even;

    System.out.println("[TEST] SUL->CENTRO");
    jv_odd = _transporter_odd.requestJob(origin,destination,price); 
    jv_even = _transporter_even.requestJob(origin,destination,price);
    assertEquals(jv_odd.getJobOrigin(), "Portalegre");
    assertEquals(jv_odd.getJobDestination(), "Leiria");
    assertEquals(jv_even, null);

  }

  @Test
  public void testLocationNorthNorth() throws Exception {
    int price = 50;
    String origin = "Porto";
    String destination = "Braganca";
    JobView jv_odd, jv_even;

    System.out.println("[TEST] NORTE->NORTE"); 
    jv_odd = _transporter_odd.requestJob(origin,destination,price); 
    jv_even = _transporter_even.requestJob(origin,destination,price);
    assertEquals(jv_odd, null);
    assertEquals(jv_even.getJobOrigin(), "Porto");
    assertEquals(jv_even.getJobDestination(), "Braganca");
  }

  @Test
  public void testLocationSouthSouth() throws Exception {
    int price = 50;
    String origin = "Portalegre";
    String destination = "Faro";
    JobView jv_odd, jv_even;

    System.out.println("[TEST] SUL -> SUL");
    jv_odd = _transporter_odd.requestJob(origin,destination,price); 
    jv_even = _transporter_even.requestJob(origin,destination,price);
    assertEquals(jv_odd.getJobOrigin(), "Portalegre");
    assertEquals(jv_odd.getJobDestination(), "Faro");
    assertEquals(jv_even, null);

  }
  
  @Test
  public void testDecideJobTrue() throws Exception {
    System.out.println("[TEST] Testing decideJob()");
    JobView jv = _transporter.requestJob("Leiria", "Lisboa", 50); 
    String id = jv.getJobIdentifier();
    jv = _transporter.decideJob(id, true);
    assertEquals(jv.getJobState(), JobStateView.ACCEPTED); 
  }

  @Test
  public void testDecideJobFalse() throws Exception {
    System.out.println("[TEST] Testing decideJob()");
    JobView jv = _transporter.requestJob("Leiria", "Lisboa", 50); 
    String id = jv.getJobIdentifier();
    jv = _transporter.decideJob(id, false);
    assertEquals(jv.getJobState(), JobStateView.REJECTED); 
  }

  @Test(expected = BadJobFault_Exception.class)
    public void testDecideJobTrueException() throws Exception {
      _transporter.decideJob("FAKEID", true);
    }

  @Test(expected = BadJobFault_Exception.class)
    public void testDecideJobFalseException() throws Exception {
      _transporter.decideJob("FAKEID", false);
    }

  @Test(expected = BadJobFault_Exception.class)
    public void testDecideJobNotProposedException() throws Exception {    
      JobView jv = _transporter.requestJob("Leiria", "Lisboa", 50);
      String id = jv.getJobIdentifier();
      _transporter.decideJob(id, true);
      _transporter.decideJob(id, true);
    }

  @Test
  public void testJobStatus() throws Exception {
    System.out.println("[TEST] Testing jobStatus()");
    JobView jv = _transporter.requestJob("Leiria", "Lisboa", 50);
    String id = jv.getJobIdentifier();
    JobView jv_response = _transporter.jobStatus(id);
    assertEquals(jv_response.getJobState(), JobStateView.PROPOSED);

    _transporter.decideJob(id, true);
    jv_response = _transporter.jobStatus(id);

    assertEquals(jv_response.getJobState(), JobStateView.ACCEPTED);

    jv_response = _transporter.jobStatus("FAKEID"); 
    assertNull(jv_response);
  }

  @Test
  public void testListJobs() throws Exception {
    _transporter.requestJob("Leiria", "Lisboa", 50);
    _transporter.requestJob("Lisboa", "Coimbra", 10);
    _transporter.requestJob("Coimbra", "Leiria", 5);
    List<JobView> jobs = _transporter.listJobs();
    assertEquals(jobs.size(), 3);
  }

  @Test 
  public void testClearJobs() throws Exception {
    _transporter.requestJob("Leiria", "Lisboa", 50);
    _transporter.requestJob("Leiria", "Coimbra", 50);
    _transporter.requestJob("Coimbra", "Lisboa", 50);
    _transporter.clearJobs();
    List<JobView> jobs = _transporter.listJobs();
    assertEquals(jobs.size(), 0);
  }

  @Test
  public void testJobSimulation() throws Exception {
    System.out.println("[TEST] Testing job Simulation()");
    JobView jv = _transporter.requestJob("Leiria", "Lisboa", 50);
    String id = jv.getJobIdentifier();

    JobView response;
    _transporter.decideJob(id, true);
    int accum = 0, timer=500;
    
    while(accum <= 5000){
      Thread.sleep(timer);      
      accum += timer;
      response = _transporter.jobStatus(id); 
      if(response.getJobState().equals(JobStateView.HEADING)) {
        accum = 0;
        break;
      }
    }
    if(accum != 0.0) fail("Job didn't change to HEADING");
    
    while(accum <= 5000){
      Thread.sleep(timer);      
      accum += timer;
      response = _transporter.jobStatus(id); 
      if(response.getJobState().equals(JobStateView.ONGOING)) {
        accum = 0;
        break;
      }
    }
    if(accum != 0) fail("Job didn't change to ONGOING");
    
    while(accum <= 5000){
      Thread.sleep(timer);      
      accum += timer;
      response = _transporter.jobStatus(id); 
      if(response.getJobState().equals(JobStateView.COMPLETED)) {
        accum = 0;
        break;
      }
    }
    if(accum != 0) fail("Job didn't change to ONGOING");

    System.out.println("[TEST] Completed job simulation test");
    
  }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOriginEven() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on origin for even transporters");
      _transporter_even.requestJob("Leiria","ABCD",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOriginOdd() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on origin for odd transporters");
      _transporter_even.requestJob("Leiria","ABCD",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestinationEven() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on destination for even transporters");
      _transporter_even.requestJob("ABCD","Leiria",50); 
    }

  @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestinationOdd() throws Exception {
      System.out.println("[TEST] Testing BadLocationFault_Exception on destination for odd transporters");
      _transporter_even.requestJob("ABCD","Leiria",50); 
    }

  @Test(expected = BadPriceFault_Exception.class)
    public void testWrongPriceEven() throws Exception {
      System.out.println("[TEST] Testing BadPriceFault_Exception for even transporters");
      _transporter_even.requestJob("Leiria","Lisboa",-20); 
    }

  @Test(expected = BadPriceFault_Exception.class)
    public void testWrongPriceOdd() throws Exception {
      System.out.println("[TEST] Testing BadPriceFault_Exception for odd transporters");
      _transporter_even.requestJob("Leiria","Lisboa",-20); 
    }

}
