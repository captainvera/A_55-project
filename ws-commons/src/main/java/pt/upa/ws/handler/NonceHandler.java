package pt.upa.ws.handler;

import java.util.Set;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.security.SecureRandom;
import java.nio.ByteBuffer;

import pt.upa.ws.Handlers;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class NonceHandler implements SOAPHandler<SOAPMessageContext> {

  //TODO change to HashSet
  private HashSet<Long> _once = new HashSet<Long>();
  private HashSet<Long> _generated = new HashSet<Long>();

  public Set<QName> getHeaders() {
    return null;
  }

  public boolean handleMessage(SOAPMessageContext smc) {
    try{
      Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound) {
        SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
        System.out.println("-- Outbound message -> Adding nonce --");

        long nonce = getUniqueNonce();

        System.out.println("-- Added nonce " + nonce + " --");
        Handlers.addHeader(env, "nonce", String.valueOf(nonce));
        return true;
      }else{
        SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
        System.out.println("-- Inbound message -> Checking nonce --");

        String nonce = Handlers.getHeader(env, "nonce");

        System.out.println("-- Got nonce " + nonce + " --");
        verifyNonce(Long.parseLong(nonce));
        return true;
      }
    }catch(Exception e){
      throw new RuntimeException();
    }
  }

  public boolean handleFault(SOAPMessageContext smc) {
    try{
      Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound) {
        SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
        System.out.println("-- Outbound message -> Adding nonce --");

        long nonce = getUniqueNonce();

        System.out.println("-- Added nonce " + nonce + " --");
        Handlers.addHeader(env, "nonce", String.valueOf(nonce));
        return true;
      }else{
        SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
        System.out.println("-- Inbound message -> Checking nonce --");

        String nonce = Handlers.getHeader(env, "nonce");

        System.out.println("-- Got nonce " + nonce + " --");
        verifyNonce(Long.parseLong(nonce));
        return true;
      }
    }catch(Exception e){
      throw new RuntimeException();
    }
  }

  // nothing to clean up
  public void close(MessageContext messageContext) {
  }

  public long getUniqueNonce(){
    long l = generateRandomSecureLong();
    while(nonceGenerated(l)){
      l = generateRandomSecureLong();
    }
    addGeneratedNonce(l); 
    return l;
  }

  public void verifyNonce(long nonce){
    if(nonceUsed(nonce)){
      System.out.println("Detected repeated nonce on message. Discarding!");
      throw new RuntimeException();
    } 
    addNonce(nonce);
  }

  public boolean nonceUsed(long nonce){
    return _once.contains(nonce); 
  }

  public boolean nonceGenerated(long nonce){
    return _generated.contains(nonce); 
  }

  public long generateRandomSecureLong(){
    try{
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

      final byte array[] = new byte[8];
      random.nextBytes(array);

      ByteBuffer bb = ByteBuffer.wrap(array);
      return bb.getLong(); 
    }catch(Exception e){
      System.out.println("Failed generating random secure number... System compromised? Aborting!");
      throw new RuntimeException();
    }
  }

  public void addNonce(long nonce){
    _once.add(nonce);
  }

  public void addGeneratedNonce(long nonce){
    _generated.add(nonce);
  }
}
