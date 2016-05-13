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

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

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

      bis.close();
      sbis.close();

      return null;
    }catch(Exception e){
      System.out.println("Failed retrieving certificate from CA because: ");
      e.printStackTrace();
      return null;
    }
  }

  public static PublicKey getCAPublicKey(String keyStoreFilePath, String keyStorePassword) throws Exception {
    KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword.toCharArray());
    Certificate ca_cert = keystore.getCertificate("ca"); 
    return ca_cert.getPublicKey();
  }

  public static PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,
      String keyAlias, char[] keyPassword) throws Exception {

    KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);
    PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);

    return key;
  }

  public static PublicKey getPublicKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,
      String keyAlias, char[] keyPassword) throws Exception {

    KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);
    PublicKey key = (PublicKey) keystore.getKey(keyAlias, keyPassword);

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

  public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)
    throws Exception {

    // verify the signature with the public key
    Signature sig = Signature.getInstance("SHA1WithRSA");
    sig.initVerify(publicKey);
    sig.update(bytes);
    try {
      return sig.verify(cipherDigest);
    } catch (SignatureException se) {
      System.err.println("Caught exception while verifying signature " + se);
      return false;
    }
  }

	public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
      System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
}
