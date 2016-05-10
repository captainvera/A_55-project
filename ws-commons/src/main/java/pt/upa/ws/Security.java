package pt.upa.ws;

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import pt.upa.ca.ws.cli.CAClient;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public class Security {
  public static Certificate getCertificateFromCA(String name){
    try{
      CAClient ca = new CAClient(); 

      /**
       * Get certificate from CA
       */

      ca.connect();
      String content = ca.requestCertificate(name);
     
      /**
       * Decode certificate from base64
       */

      byte[] data = parseBase64Binary(content);

      ByteArrayInputStream sbis;
      sbis = new ByteArrayInputStream(data);

      BufferedInputStream bis = new BufferedInputStream(sbis);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");

      /**
       * Generate X509 Certificate
       */
      if (bis.available() > 0) {
        Certificate cert = cf.generateCertificate(bis);
        return cert;
      }

      /**
       * TODO: CERT CACHE
       */

      bis.close();
      sbis.close();

      return null;
    }catch(Exception e){
      System.out.println("Failed retrieving certificate from CA because: ");
      e.printStackTrace();
      return null;
    }
  }
}
