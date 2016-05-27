package pt.upa.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.ByteArrayOutputStream;
import java.lang.StringBuffer;
import java.util.Iterator;

import pt.upa.ws.Handlers;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class PenetrationTestHandler implements SOAPHandler<SOAPMessageContext> {

  public Set<QName> getHeaders() {
    return null;
  }

  boolean _set = false;
  String _old_nonce;
  SOAPMessage _old_message;

  public boolean handleMessage(SOAPMessageContext smc) {
    try{
      Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound) {
        int level = getVal();
        if(level == 1)
         repeatMessage(smc);
        else if(level == 2)
         tamperNonce(smc);
        else if(level == 3)
         tamperBody(smc);
        else if(level == 4)
         logBody(smc);
      }
    }catch(Exception e){
      return false;
    } 
    return true;
  }

  public boolean handleFault(SOAPMessageContext smc) {
    return true;
  }

  // nothing to clean up
  public void close(MessageContext messageContext) {
  }

  /**
   * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
   * outgoing or incoming message. Write a brief message to the print stream
   * and output the message. The writeTo() method can throw SOAPException or
   * IOException
   */
  public void tamperIdentifier(SOAPMessageContext smc) throws Exception {
    SOAPMessage message = smc.getMessage();

    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

    if(!_set){
      _set = true;
      _old_nonce = Handlers.getHeader(soapEnvelope, "nonce");
    }else{

       Name qname = soapEnvelope.createName("identifier", "pt.upa", "http://pt.upa"); 
      SOAPHeader soapHeader = soapEnvelope.getHeader();

      Iterator it = soapHeader.getChildElements(qname);

      SOAPElement element = (SOAPElement) it.next();
      element.setValue("WRONGID");
    }
  }

  public void tamperNonce(SOAPMessageContext smc) throws Exception {
    SOAPMessage message = smc.getMessage();

    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

    if(!_set){
      _set = true;
      _old_nonce = Handlers.getHeader(soapEnvelope, "nonce");
    }else{

      Name qname = soapEnvelope.createName("nonce", "pt.upa", "http://pt.upa"); 
      SOAPHeader soapHeader = soapEnvelope.getHeader();

      Iterator it = soapHeader.getChildElements(qname);

      SOAPElement element = (SOAPElement) it.next();
      element.setValue(_old_nonce);
    }
  }

  public void tamperBody(SOAPMessageContext smc) throws Exception {
    SOAPMessage message = smc.getMessage();

    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
    SOAPBody body = soapEnvelope.getBody();
    String content = body.getFirstChild().getTextContent();
    content = "tamperedbody"; 
    body.getFirstChild().setTextContent(content);
    // System.out.println("Got content: " + content);  
  }

  public void logBody(SOAPMessageContext smc) throws Exception {
    SOAPMessage message = smc.getMessage();      
    StringBuffer sbuf = new StringBuffer();
    try {
      sbuf.append("\n");
      sbuf.append(message.toString()); 
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      message.writeTo(baos);                     
      sbuf.append("\nMessage Desc:"+baos.toString());         
      sbuf.append("\n");
    }
    catch (Exception e) {
      sbuf.append("Exception in SOAP Handler: " + e);
    }
    System.out.println(sbuf.toString());
  }

  public void repeatMessage(SOAPMessageContext smc) throws Exception {
      
    SOAPMessage message = smc.getMessage();
    
    if(!_set){
      _set = true;
      _old_message = message;
    }else{
      smc.setMessage(_old_message);
    }
  } 

  public int getVal() throws Exception {
    String result = "";
    Properties prop = new Properties();
    
    InputStream input = new FileInputStream("config.properties");
    if(input != null){
      prop.load(input);
      return Integer.parseInt(prop.getProperty("level"));
    } else {
      System.out.println("Config not found, default to 0");
      return 0;
    }
  }
}
