package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyElementLaxMixedEmptyTestCases extends JAXBWithJSONTestCases{
	   private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixedEmpty.xml";
	   private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixedEmpty.json";
	    
	    public XmlAnyElementLaxMixedEmptyTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
	        Class[] classes = new Class[2];
	        classes[0] = EmployeeLaxMixed.class;
	        classes[1] = Address.class;
	        setClasses(classes);
	        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
	        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
	    }

	    protected Object getControlObject() {
	    	EmployeeLaxMixed employee = new EmployeeLaxMixed();
	        
	        employee.name = "John Doe";
	        employee.homeAddress  = new Address();
	        employee.homeAddress.street = "123 Fake Street";
	        employee.homeAddress.city = "Ottawa";
	        employee.homeAddress.country = "Canada";

	        return employee;
	    }
}
