package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.threadsafety;

import java.io.StringReader;

import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;



public class ReferenceUnmarshaller implements Runnable {
	private XMLUnmarshaller unmarshaller;
	private String resource;
	private String controlStreet;
	public ReferenceUnmarshaller(XMLUnmarshaller unmarshaller, String resource, String controlStreet) {
		this.unmarshaller = unmarshaller;
		this.resource = resource;
		this.controlStreet = controlStreet;
	}
	
	public void run() {
		for(int i = 0; i < 2000; i++) {
			StringReader reader = new StringReader(resource);
			Root root = (Root)unmarshaller.unmarshal(reader);
			if(!(controlStreet.equals(((Employee)root.employee).address.street))) {
				throw new RuntimeException("Incorrect Address Resolved");
			}
		}
	}
	

}
