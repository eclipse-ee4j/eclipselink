/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 27/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlElements via eclipselink-oxm.xml
 *
 */
public class XmlElementsTestCases extends JAXBWithJSONTestCases{
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/";
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/foo.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/foo.json";
     /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlElementsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Foo.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }
    
    
	 public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/eclipselink-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
   
	    public void testSchemaGen() throws Exception{
	    	List controlSchemas = new ArrayList();
	    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/schema.xsd");	    	
	    	controlSchemas.add(is);	    	
	    	super.testSchemaGen(controlSchemas);
	    	
	    }

	    public void testInstanceDocValidation() {
	    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/schema.xsd");        
	        StreamSource schemaSource = new StreamSource(schema); 
	                
	        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
	        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
	        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
	    }
	    
		@Override
		protected Object getControlObject() {
			// setup control objects
	        Foo foo = new Foo();
	        List theItems = new ArrayList();
	        theItems.add(new Float(2.5));
	        theItems.add(new Integer(1));
	        foo.items = theItems;
	        return foo;
		}   


    /**
     * Tests @XmlElements schema generation via eclipselink-oxm.xml.  Here, an
     * xml-element-wrapper and xml-idref are used.
     * 
     * Positive test.
     * @throws Exception 
     */
		
    public void testXmlElementsWithIdRefSchemaGen() throws Exception {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/eclipselink-oxm-idref.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
	    List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/schema_idref.xsd");	    	
    	controlSchemas.add(is);	 

    	JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Bar.class, Address.class, Phone.class }, properties);
 
    	 MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
         ctx.generateSchema(outputResolver);

         List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
         compareSchemas(controlSchemas, generatedSchemas);
	
    }

    /**
     * Tests @XmlElements schema generation via eclipselink-oxm.xml.  Here, an
     * xml-idref is used, but one of the elements in not an XmlID.
     * 
     * Negative test.
     */
    public void testXmlElementsWithInvalidIdRef() {
        String metadataFile = PATH + "eclipselink-oxm-idref-invalid.xml";
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        // create context
        boolean exception = false;
        JAXBContext jCtx = null;
        try {
            jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Bar.class, Address.class, Foo.class }, properties);
        } catch (JAXBException e1) {
            exception = true;
        }
        assertTrue("The expected exception did not occur.", exception);
    }


    /**
     * Test setting the container class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
  
        XMLDescriptor xDesc = ((JAXBContext)jaxbContext).getXMLContext().getDescriptor(new QName("foo"));
        assertNotNull("No descriptor was generated for Foo.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("items");
        assertNotNull("No mapping exists on Foo for attribute [items].", mapping);
        assertTrue("Expected an XMLChoiceCollectionMapping for attribute [items], but was [" + mapping.toString() +"].", mapping instanceof XMLChoiceCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }
}
