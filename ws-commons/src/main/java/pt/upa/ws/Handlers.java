package pt.upa.ws;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import java.util.Iterator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class Handlers {

  public static void addHeader(SOAPEnvelope soapEnvelope, String name, String content) throws Exception {
    SOAPHeader soapHeader = soapEnvelope.getHeader();
    if (soapHeader == null)
      soapHeader = soapEnvelope.addHeader();

    Name qname = soapEnvelope.createName(name, "pt.upa", "http://pt.upa");
    SOAPHeaderElement element = soapHeader.addHeaderElement(qname);

    element.addTextNode(content);
  }

  public static String getHeader(SOAPEnvelope soapEnvelope, String name) throws Exception {
    Name qname = soapEnvelope.createName(name, "pt.upa", "http://pt.upa"); 

    SOAPHeader soapHeader = soapEnvelope.getHeader();
    if (soapHeader == null)
      return null;

    Iterator it = soapHeader.getChildElements(qname);
    if(!it.hasNext()){
      return null;
    }

    SOAPElement element = (SOAPElement) it.next();
    return element.getValue();
  }

  public static byte[] concatenateByteArray(byte[] a1, byte[] a2) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(a1);
    output.write(a2);

    return output.toByteArray();
  } 

  public static byte[] concatenateByteArray(byte[] a1, byte[] a2, byte[] a3) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(a1);
    output.write(a2);
    output.write(a3);

    return output.toByteArray();
  } 
}
