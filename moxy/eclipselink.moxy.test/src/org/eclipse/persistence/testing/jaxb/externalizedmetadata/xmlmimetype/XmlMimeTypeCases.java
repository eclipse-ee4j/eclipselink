/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 12/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlMimeType via eclipselink-oxm.xml
 *
 */
public class XmlMimeTypeCases extends JAXBWithJSONTestCases {    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmimetype/att-types.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmimetype/att-types.json";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlMimeTypeCases(String name) throws Exception {
        super(name);        
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { AttTypes.class });
        jaxbUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());

        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");      
        MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
        jaxbMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());

    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmimetype/eclipselink-oxm.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}    

	public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmimetype/schema.xsd");    	
    	controlSchemas.add(is);    	
    	
    	super.testSchemaGen(controlSchemas);
 
    }

	protected Object getControlObject() {
		AttTypes attTypes = new AttTypes();
		String s = "THISISATEXTSTRINGFORTHISDATAHANDLER";
    	byte[] bytes = s.getBytes();
		attTypes.b = bytes;	       
		return attTypes;
	}
  
}
