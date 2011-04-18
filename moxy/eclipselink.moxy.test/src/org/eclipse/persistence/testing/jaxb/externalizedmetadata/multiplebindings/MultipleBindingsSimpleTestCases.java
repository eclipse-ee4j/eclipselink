package org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Employee;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Person;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.Address;
import org.w3c.dom.Document;

public class MultipleBindingsSimpleTestCases extends JAXBTestCases{
	  protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/simplemultiplebindings.xml";
	    public MultipleBindingsSimpleTestCases(String name) throws Exception {
	        super(name);		
	    }
		
	    public void setUp() throws Exception {
	        setControlDocument(XML_RESOURCE);
	        super.setUp();
	        Type[] types = new Type[2];
	        types[0] = Person.class;
	        types[1] = Employee.class;	        
	        setTypes(types);
	    }

	    public void init() throws Exception {	
	    	Type[] types = new Type[2];
		    types[0] = Person.class;
		    types[1] = Employee.class;	
	        setTypes(types);
	    }
		
	    protected Object getControlObject() {
	    	Employee emp = new Employee();
	    	emp.age = 35;
	    	emp.name = "Bob Jones";
	    	emp.address = "123 theStreet";	    
	        return emp;
	    }
		
	    public void testSchemaGen() throws Exception {
	        List<InputStream> controlSchemas = new ArrayList<InputStream>();		
	        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/simplemultiplebindings.xsd");
	        //InputStream is2= ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/simplemultiplebindings2.xsd");
	        controlSchemas.add(is);
	        //controlSchemas.add(is2);
	        super.testSchemaGen(controlSchemas);	        
	    }
		
	    protected Map getProperties2() throws Exception{
			
	        Map overrides = new HashMap();		
	        String bindings1 = "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"> " + 
	        "<xml-schema attribute-form-default=\"QUALIFIED\" element-form-default=\"UNQUALIFIED\" namespace=\"\"/>" +
	                            "<java-types>" +
	                                "<java-type name=\"org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Employee\">" + 
	                                "</java-type>" +
	                                "</java-types>" + 
	                             "</xml-bindings>";
	        
	        String bindings2 = "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"> " +
	        "<xml-schema attribute-form-default=\"UNQUALIFIED\" element-form-default=\"UNQUALIFIED\" namespace=\"\"/>" +
	        "<java-types>" +
	        "<java-type name=\"org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Employee\">" + 
	            "<java-attributes>" + 
	                "<xml-element java-attribute=\"name\" name=\"employee_name\" />" +
	                "<xml-element java-attribute=\"age\"/>" +
	            "</java-attributes>" +
	        "</java-type>" +
	        "<java-type name=\"org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Person\">" +
	           "<xml-type name=\"person-type\"/>" +
	           "<java-attributes>" + 
	              "<xml-attribute java-attribute=\"id\"/>" +
	           "</java-attributes>" + 
	         "</java-type>" +
	        "</java-types>" + 
	     "</xml-bindings>";
			
	        DOMSource src1 = null;
	        DOMSource src2 = null;
	        try {		      
	            Document doc = parser.parse(new ByteArrayInputStream(bindings1.getBytes()));
	            src1 = new DOMSource(doc.getDocumentElement());
	            Document doc2 = parser.parse(new ByteArrayInputStream(bindings2.getBytes()));	            
	            src2 = new DOMSource(doc2.getDocumentElement());	            
		    } catch (Exception e) {		    	
		        e.printStackTrace();
		        fail("An error occurred during setup");
	        }
		    
		    ArrayList<Object> bindingsList = new ArrayList();
	        bindingsList.add(src1);
	        bindingsList.add(src2);
			    
	        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple", bindingsList);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }	
	    
	    protected Map getProperties() throws Exception{
	    	Map overrides = new HashMap();		
		    
	        InputStream iStream = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/simple-oxm1.xml");
	        InputStream iStream2 = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/simple-oxm2.xml");
	   	    	
		    ArrayList<Object> bindingsList = new ArrayList();
	        bindingsList.add(new StreamSource(iStream));
	        bindingsList.add(new StreamSource(iStream2));
			    
	        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple", bindingsList);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }
}
