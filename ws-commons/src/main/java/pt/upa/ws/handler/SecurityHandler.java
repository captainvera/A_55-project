package pt.upa.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
/**
 * This SOAPHandler encrypts the contents of the message context for inbound and
 * outbound messages.
 */
public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String WS_IDENTIFIER = "pt.upa.ws.identifier";
	public static final String WS_KEYSTORE_FILE = "pt.upa.ws.keystorefile";
	public static final String WS_CERT_FILE = "pt.upa.ws.certfile";

  public Set<QName> getHeaders() {
    return null;
  }

  public boolean handleMessage(SOAPMessageContext smc) {
    try{
		  Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		  if (outbound) {
        System.out.println("-- Outbound message -> Encrypting message");
        return encryptMessage(smc);
      }else{
        /**
         * TODO:: VERIFY MESSAGE AUTHENTICITY
         */ 
        return true;
      }
    }catch(Exception e){
      return false; 
    }
  }

  public boolean handleFault(SOAPMessageContext smc) {
    try{
		  Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		  if (outbound) {
        System.out.println("-- Outbound message -> Encrypting message");
        return encryptMessage(smc);
      }else{
        return true;
      }
    }catch(Exception e){
      return false; 
    }
  }

  // nothing to clean up
  public void close(MessageContext messageContext) {
  }

  private boolean encryptMessage(SOAPMessageContext map) throws Exception {
    SOAPMessage message = map.getMessage();

    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
    SOAPBody body = soapEnvelope.getBody();
    String content = body.getFirstChild().getTextContent();

    //Shouldn't encrypt empty messages?
    if(content.equals(null)) return false;

    /**
     * TODO:Calculate MessageID (MID) and add to body
     */

    /**
     * Create digital signature with the certificate
     */

    byte[] data = content.getBytes(); 

    System.out.println("-- Using current WS context: " + "\n" +  
        "> " + (String) map.get(WS_IDENTIFIER) + "\n" +  
        "> " + (String) map.get(WS_KEYSTORE_FILE) + "\n" +  
        "> " + (String) map.get(WS_CERT_FILE)); 

    /**
     * TODO: Should password be hardcoded or???
     */

    String keystorefile = (String) map.get(WS_KEYSTORE_FILE);
    String password = "ins3cur3";
    String keyalias = (String) map.get(WS_IDENTIFIER);
    String keypassword = "1nsecure";

    PrivateKey privateKey = getPrivateKeyFromKeystore(keystorefile, password.toCharArray(), keyalias, keypassword.toCharArray());
    Signature sig = Signature.getInstance("SHA1WithRSA");
    sig.initSign(privateKey);
    sig.update(data);
    byte[] signature = sig.sign();
    
    /**
     * Add signature to message header
     */

    SOAPHeader soapHeader = soapEnvelope.getHeader();
    if (soapHeader == null)
      soapHeader = soapEnvelope.addHeader();

    Name name = soapEnvelope.createName("signature", "pt.upa", "http://pt.upa");
    SOAPHeaderElement element = soapHeader.addHeaderElement(name);

    String sigContent = printBase64Binary(signature);
    element.addTextNode(sigContent);

    System.out.println("-- Successfuly signed message");
    return true;
  }

  public static PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,
      String keyAlias, char[] keyPassword) throws Exception {

    KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);
    PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);

    return key;
  }

  public static KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword) throws Exception {
    FileInputStream fis;
    try {
      fis = new FileInputStream(keyStoreFilePath);
    } catch (FileNotFoundException e) {
      System.err.println("Keystore file <" + keyStoreFilePath + "> not fount.");
      return null;
    }
    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    keystore.load(fis, keyStorePassword);
    return keystore;
  }

  private void decryptMessage(SOAPMessageContext map) {
    SOAPMessage message = map.getMessage();

    /**
     * Verify digital signature of message
     */

    /**
     * Confirm MID is valid
     */

    /**
     * Parse proper message
     */
  }
}
