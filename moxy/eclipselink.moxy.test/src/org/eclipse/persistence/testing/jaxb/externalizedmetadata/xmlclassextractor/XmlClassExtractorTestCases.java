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
 * dmccann - July 28/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * Tests XmlClassExtractor via eclipselink-oxm.xml
 * 
 */
public class XmlClassExtractorTestCases extends JAXBTestCases {
   private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlclassextractor/parkinglot.xml";

    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlClassExtractorTestCases(String name) throws Exception{
        super(name);
        setClasses(new Class[]{Car.class, Vehicle.class, ParkingLot.class });
        setControlDocument(XML_RESOURCE);
    }

    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlclassextractor/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    
    public Map getInvalidProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlclassextractor/eclipselink-oxm-no-extractor.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
   
    /**
     * Returns the control ParkingLot instance.
     */
    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        List vehicles = new ArrayList();
        vehicles.add(car);

        ParkingLot lot = new ParkingLot();
        lot.setVehicles(vehicles);
        return lot;
    }
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlclassextractor/parkinglot.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    	
    }
    

    /**
     * Tests unmarshal doc without ClassExtractor set - should cause an
     * unmarshal failure, i.e. unmarshalled object will not have Car
     * info populated.
     * 
     * Negative test.
     */
    public void testUnmarshalFailure() throws Exception{
        // create the JAXBContext for this test (metadata file is validated as well)
        JAXBContext jaxbContextInvalid = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Car.class, Vehicle.class, ParkingLot.class}, getInvalidProperties());
        Unmarshaller unmarshaller = jaxbContextInvalid.createUnmarshaller();
        try {
            InputStream iDocStream = getClass().getClassLoader().getResourceAsStream(XML_RESOURCE);

            ParkingLot lotObj = (ParkingLot) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", lotObj);
            assertFalse("Unmarshal did not fail as expected", getControlObject().equals(lotObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

  
}