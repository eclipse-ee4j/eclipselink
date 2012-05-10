/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 24/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests inheritance configuration via XmlDiscriminatorNode & XmlDiscriminatorValue. 
 *
 */
public class XmlDiscriminatorTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle-write.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle.json";
    private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle-write.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlDiscriminatorTestCases(String name)throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[] { Car.class, Vehicle.class });
    }
   
    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 26;
        car.model = "Mustang GT";
        car.manufacturer = "Ford";
        car.topSpeed = 354;
        return new JAXBElement(new QName("vehicle-data"), Vehicle.class, car);
    }
    

	public Map getProperties(){
	    InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/vehicle.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    }
  
}
