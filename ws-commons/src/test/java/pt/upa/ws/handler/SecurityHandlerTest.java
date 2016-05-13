package pt.upa.ws.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;
import example.ws.handler.AbstractHandlerTest;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.ws.Context;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 *  Handler test suite
 */
public class SecurityHandlerTest extends AbstractHandlerTest {

    // tests
    String inboundMSG = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><pt.upa:nonce xmlns:pt.upa=\"http://pt.upa\">-1210824834517559170</pt.upa:nonce><pt.upa:signature xmlns:pt.upa=\"http://pt.upa\">XcIjnWnDZdPyHvBKJwQlpP/hRGZWbg6zBRSHMkG7CegV0pBVeVhCQg7Xh5jWP2JoiKvmrcaV5L2+dufdZ+YaMInCfU00qxpfeM03HBqAg3d2o3wuaGieqKJzHq+IOjOknLGIjikKG4HstAD9dbPuak6nffwjamZyypLRa9pctlfZByXv/A5966NtS6Tg9esJukKymasvXoxtItR1jCH5nhDCfIzB5Li6ZORgU80CaC3bsGkpPH1FUHCjCIEaJdEBP7+gvjl81z2/dxw6HKVxETXNrmfxFd0DwACxKQ0D/RzGDgOn1D6aZMLE2/2GzBThSxoeIDbQdJ0intEtlfg8Eg==</pt.upa:signature><pt.upa:identifier xmlns:pt.upa=\"http://pt.upa\">Broker</pt.upa:identifier></SOAP-ENV:Header><S:Body><ns2:requestJob xmlns:ns2=\"http://ws.transporter.upa.pt/\"><origin>Leiria</origin><destination>Lisboa</destination><price>10</price></ns2:requestJob></S:Body></S:Envelope>";

    String outboundMSG = "";

    String brokerCert = "-----BEGIN CERTIFICATE-----\nMIIDOzCCAiMCCQCZuUwK1PgC4DANBgkqhkiG9w0BAQsFADBXMRswGQYDVQQDDBJE\naXN0cmlidXRlZFN5c3RlbXMxDDAKBgNVBAsMA0RFSTEMMAoGA1UECgwDSVNUMQ8w\nDQYDVQQHDAZMaXNib24xCzAJBgNVBAYTAlBUMB4XDTE2MDUxMTE1Mzc1OVoXDTE2\nMDgwOTE1Mzc1OVowaDELMAkGA1UEBhMCUFQxDzANBgNVBAgTBkxpc2JvbjEPMA0G\nA1UEBxMGTGlzYm9uMQwwCgYDVQQKEwNJU1QxDDAKBgNVBAsTA0RFSTEbMBkGA1UE\nAxMSRGlzdHJpYnV0ZWRTeXN0ZW1zMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\nCgKCAQEAxkocnzzgBQBPT+YFH4aNnepjmbsxpg4DNOPmViJnGt7IkwSiCwPyWo6F\njDUnyXscSmT8p4Sj6Jhz6I6BlAkT7yil+7Jau6PSYY6DQS3jN4SaefUtFKKGF/5b\ngsaN+OL34Bq9fpV8V7k2J3kXebIl45R420xgijTVGrwWSLj0DIOUQAxhItNZQE4U\ndl0nX34haf4bb3+LgRuSenWdN35KKskgJwRb7XmJNgRZrklOuhohLda0hnt7vW2T\nKIfuL90+YSoojl5kgGpi/TWcaa4dMT+MyhVK9XSw7g07sAbVmwfNOEepX2Q+NrT4\nkaRCNaCHrphBiuZzudr3DhiozhXXgQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQB/\nsF2zu8aqv/stXlHFbmhKexQbZaYnjXQtZrzJAjx5njzBdnkZyf7KWno7U0bL+s/A\n2TIL31hxjR01k7594uskxh3qtiODQwKECVaMI8TiCuW1OegnDbYRX5U4svtGnibs\nyWQLVpG5JPXDFd0h4R2gOK+02rsdrRTniF/9LjC8IlrPBAatgMOnTBGeEd6GOXOs\nMUcvJbJlwRC73lhrSz62HepttBqAbi1Opvg59sUFtcgAiZOQr/Am3Dbvj4HyBYJZ\nWLIKBtxm3J/yIslQjtMLuS8Taf3GIvhSUKNVDY3IJtYH+I7aGLD1MVKSmPyK0L4S\nYjOHcuts0amdINz5Tb/I\n-----END CERTIFICATE-----";

    @Test
    public void testSecurityHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext,
        @Mocked final CAClient caClient)
        throws Exception {

        Context.WS_KEYSTORE_FILE = "../broker-ws/keys/Broker.jks";

        final String soapText = inboundMSG; 
        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;

            new CAClient();
            caClient.connect();
            
            caClient.requestCertificate("Broker");
            result = printBase64Binary(brokerCert.getBytes());
        }};

        SecurityHandler handler = new SecurityHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        assertTrue(handleResult);
    }

    @Test(expected = RuntimeException.class)
    public void tamperBody(
        @Mocked final SOAPMessageContext soapMessageContext,
        @Mocked final CAClient caClient)
        throws Exception {

        Context.WS_KEYSTORE_FILE = "../broker-ws/keys/Broker.jks";

        final String soapText = inboundMSG; 
        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        new StrictExpectations() {{
            soapMessageContext.getMessage();
            result = soapMessage;

            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;

            new CAClient();
            caClient.connect();
            
            caClient.requestCertificate("Broker");
            result = printBase64Binary(brokerCert.getBytes());
        }};

        SecurityHandler handler = new SecurityHandler();
        PenetrationTestHandler pt = new PenetrationTestHandler();
        pt.tamperBody(soapMessageContext);
        boolean handleResult = handler.handleMessage(soapMessageContext);
    }
}
