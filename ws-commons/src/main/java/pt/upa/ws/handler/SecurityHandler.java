package pt.upa.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import pt.upa.ws.Security;
import pt.upa.ws.Handlers;
import pt.upa.ws.Context;


/**
 * This SOAPHandler encrypts the contents of the message context for inbound and
 * outbound messages.
 */

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

  public static final String WS_IDENTIFIER = "pt.upa.ws.identifier";
  public static final String WS_KEYSTORE_FILE = "pt.upa.ws.keystorefile";
  public static final String WS_CERT_FILE = "pt.upa.ws.certfile";

  private TreeMap<String, Certificate> _cache = new TreeMap<String, Certificate>();

  private String _WS_IDENTIFIER;
  private String _WS_KEYSTORE_FILE; 
  private String _keystore_password = "1nsecure";
  private String _key_password = "ins3cur3";

  public SecurityHandler(){
    _WS_IDENTIFIER = Context.WS_IDENTIFIER;
    _WS_KEYSTORE_FILE = Context.WS_KEYSTORE_FILE;
  }

  public Set<QName> getHeaders() {
    return null;
  }

  public boolean handleMessage(SOAPMessageContext smc) {
    try{
      Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound) {
        System.out.println("-- Outbound message -> Encrypting message --");
        return encryptMessage(smc);
      }else{
        System.out.println("-- Inbound message -> Verifying message --");
        return verifyMessage(smc);
      }
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  public boolean handleFault(SOAPMessageContext smc) {
    try{
      Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound) {
        System.out.println("-- Outbound message -> Encrypting message --");
        return encryptMessage(smc);
      }else{
        return true;
      }
    }catch(Exception e){
      throw new RuntimeException();
    }
  }

  // nothing to clean up
  public void close(MessageContext messageContext) {
  }

  /**
   * Name and Nounce must be included in the digital signature calculation to ensure no tampering
   * Current format used for calculation is (where + means concatenation):
   * body_content+identifier+nonce
   */

  private boolean encryptMessage(SOAPMessageContext map) throws Exception {
    SOAPMessage message = map.getMessage();

    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
    SOAPBody body = soapEnvelope.getBody();
    String content = body.getFirstChild().getTextContent();

    System.out.println("-- Signing message --");

    //Shouldn't we encrypt empty messages?
    if(content.equals(null)) return false;
    
    //Fetch the nonce to calculate signature
    String nonce = Handlers.getHeader(soapEnvelope, "nonce");
    
    if(nonce.equals(null)){
      System.out.println("-- [ERROR] No nonce found in message... aborting! --");
      throw new RuntimeException();
    }

    //Get data to create the signature from
    byte[] data = content.getBytes(); 
    data = Handlers.concatenateByteArray(data, _WS_IDENTIFIER.getBytes(), nonce.getBytes());

    //Fetch our private key to create the signature TODO: should be in function?
    String keystorefile = _WS_KEYSTORE_FILE;
    String keyalias = _WS_IDENTIFIER;

    PrivateKey privateKey = Security.getPrivateKeyFromKeystore(keystorefile, _key_password.toCharArray(), keyalias, _keystore_password.toCharArray());

    if(privateKey == null){
      System.out.println("-- [ERROR] Couldn't find private key in keystore... returning! --");
      throw new RuntimeException();
    }

    Signature sig = Signature.getInstance("SHA1WithRSA");
    sig.initSign(privateKey);
    sig.update(data);

    byte[] signature = sig.sign();
    
    //Write signature to a header using base64 encoding
    String sigContent = printBase64Binary(signature);
    Handlers.addHeader(soapEnvelope, "signature", sigContent); 

    //Write identifier to a header aswell for message verification
    Handlers.addHeader(soapEnvelope, "identifier", _WS_IDENTIFIER);

    System.out.println("-- Successfuly signed message --");
    return true;
  }


  private boolean verifyMessage(SOAPMessageContext map) throws Exception {
    SOAPMessage message = map.getMessage();
    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
    SOAPBody body = soapEnvelope.getBody();
    SOAPHeader header = soapEnvelope.getHeader();
    String content = body.getFirstChild().getTextContent();

    //Check if there is a header with a signature
    String ws_identifier = Handlers.getHeader(soapEnvelope, "identifier");
    if(ws_identifier == null)
     throw new RuntimeException("-- [ERROR] Received message without Identification! Rejecting message --"); 

    //Fetch the correct certificate
    Certificate cert = getCertificate(ws_identifier);
    
    //Get CA public key from keystore
    PublicKey ca_key = Security.getCAPublicKey(_WS_KEYSTORE_FILE, "ins3cur3");

    if(ca_key == null){
      System.out.println("-- [ERROR] CA key not found in keystore! Certificates can't be authenticated --");
      throw new RuntimeException();
    }

    //Verify if the retrieved certificate is correctly signed by CA 
    if(!Security.verifySignedCertificate(cert, ca_key)) {
      System.out.println("-- [ERROR] Certificate provided is wrongly signed by CA. Aborting --");
      throw new RuntimeException();
    }
    
    //Get message digital signature and parse it to binary
    String signature_content = Handlers.getHeader(soapEnvelope, "signature");
    if(signature_content == null){
      System.out.println("-- [ERROR] No digital signature found. Aborting --");
      throw new RuntimeException(); 
    }
      
    byte[] signature_data = parseBase64Binary(signature_content);

    String nonce = Handlers.getHeader(soapEnvelope, "nonce");
    if(nonce.equals(null)){
      System.out.println("-- [ERROR] No nonce found. Aborting --");
      throw new RuntimeException();
    }

    byte[] actual_data = content.getBytes();
    actual_data = Handlers.concatenateByteArray(actual_data, ws_identifier.getBytes(), nonce.getBytes());
    //Verify if the digital signature of the received message is correct
    System.out.println("-- Verifying signature from " + ws_identifier + " --");
    if(!Security.verifyDigitalSignature(signature_data, actual_data, cert.getPublicKey())){
      System.out.println("-- [ERROR] Digital Signature for message can't be verified. Aborting! --");
      throw new RuntimeException();
    }

    return true;
  }

  public Certificate getCertificate(String name){
    if(_cache.containsKey(name))
      return _cache.get(name);

    Certificate cert = Security.getCertificateFromCA(name);
    _cache.put(name, cert);

    return cert;
  }
}
