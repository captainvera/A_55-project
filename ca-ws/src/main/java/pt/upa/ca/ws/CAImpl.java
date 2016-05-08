package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {

	public String sayHello(String name) {
		return "Hello " + name + "!";
	}

}
