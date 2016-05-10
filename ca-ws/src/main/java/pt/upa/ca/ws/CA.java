package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService
public interface CA {
	String requestCertificate(String name);
}
