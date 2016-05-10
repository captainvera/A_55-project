package pt.upa.ca.ws;

import javax.jws.WebService;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;

import java.security.MessageDigest;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {

  private TreeMap<String, String> _certificates = new TreeMap<String, String>(); 

  public CAImpl(){
    _certificates.put("UpaTransporter1", "keys/UpaTransporter1.cer");
    _certificates.put("UpaTransporter2", "keys/upatransporter2.cer");
    _certificates.put("Broker", "keys/broker.cer");
  }

  public String requestCertificate(String name) {
    System.out.println("--Retrieving certificate for " + name);
    byte[] data = readFile(_certificates.get(name));

    System.out.println("--Encoding to Base64");
    System.out.println("Sending data: "+ data);
    return printBase64Binary(data);
  }


  private byte[] readFile(String path) {
    byte[] content = null;
    try {
      FileInputStream fis = new FileInputStream(path);
      content = new byte[fis.available()];
      fis.read(content);
      fis.close();
    }catch(FileNotFoundException e) {
      content = null;
      System.out.println("Problem reading file. Returning null!");
    }catch(IOException e){
      content = null;
      System.out.println("Problem reading file. Returning null!");
    }
    return content;
  }
}
