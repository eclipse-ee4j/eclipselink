package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NamespacesOnUnmarshalOnlyTestCases extends JSONMarshalUnmarshalTestCases{
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person.json";
	private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person_no_namespaces.json";

	public NamespacesOnUnmarshalOnlyTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		setClasses(new Class[]{Person.class});
	}

	public void setUp(){
		super.setUp();
		try {
			Map<String, String> namespaceMap = new HashMap<String, String>();
			
			namespaceMap.put("ns0", "namespace0");
			namespaceMap.put("ns1", "namespace1");
			namespaceMap.put("ns2", "namespace2");
			namespaceMap.put("ns3", "namespace3");
			jsonUnmarshaller.setProperty(JAXBContext.NAMESPACES, namespaceMap);
		} catch (PropertyException e) {
			e.printStackTrace();
			fail("An error occurred during setup.");
		}
	}
	
	protected Object getControlObject() {
		Person p = new Person();
		p.setId(10);
		p.setFirstName("Jill");
		p.setLastName("MacDonald");
		
		List<String> middleNames = new ArrayList<String>();
		middleNames.add("Jane");
		middleNames.add("Janice");
		p.setMiddleNames(middleNames);
		
		Address addr = new Address();
		addr.setStreet("The Street");
		addr.setCity("Ottawa");
		p.setAddress(addr);
		
		return p;
	}
	

	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContext.ATTRIBUTE_PREFIX, "@");
		/*
		Map<String, String> namespaceMap = new HashMap<String, String>();
		
		namespaceMap.put("ns0", "namespace0");
		namespaceMap.put("ns1", "namespace1");
		namespaceMap.put("ns2", "namespace2");
		namespaceMap.put("ns3", "namespace3");
		
		
		props.put(JAXBContext.NAMESPACES, namespaceMap);*/
		return props;
	}

}
