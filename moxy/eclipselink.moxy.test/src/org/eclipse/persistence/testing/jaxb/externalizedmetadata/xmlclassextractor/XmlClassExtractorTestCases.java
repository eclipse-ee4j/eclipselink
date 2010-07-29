/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.textui.TestRunner;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlClassExtractor via eclipselink-oxm.xml
 * 
 */
public class XmlClassExtractorTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlclassextractor/";
    private static final String INSTANCE_DOC = PATH + "parkinglot.xml";
    private Class[] classes;
    private MySchemaOutputResolver resolver;
    private JAXBContext jCtx;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlClassExtractorTestCases(String name) {
        super(name);
    }

    /**
     * This method will be responsible for schema generation, which will create the 
     * JAXBContext we will use and validate the eclipselink metadata file.
     */
    public void setUp() throws Exception {
        super.setUp();
        classes = new Class[] { Car.class, Vehicle.class, ParkingLot.class };
        // schema generation also creates the JAXBContext
        resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 1);
        jCtx = getJAXBContext();
        assertNotNull("Setup failed: JAXBContext is null", jCtx);
    }
    
    /**
     * Returns the control ParkingLot instance.
     */
    public ParkingLot getControlObject() {
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

    /**
     * Tests unmarshal doc with ClassExtractor set.
     * 
     * Positive test.
     */
    public void testUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(INSTANCE_DOC);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + INSTANCE_DOC + "]");
        }

        // setup control ParkingLot
        ParkingLot ctrlLot = getControlObject();

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            ParkingLot lotObj = (ParkingLot) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", lotObj);
            assertTrue("Unmarshal failed:  ParkingLot objects are not equal", ctrlLot.equals(lotObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    /**
     * Tests unmarshal doc without ClassExtractor set - should cause an
     * unmarshal failure, i.e. unmarshalled object will not have Car
     * info populated.
     * 
     * Negative test.
     */
    public void testUnmarshalFailure() {
        // create the JAXBContext for this test (metadata file is validated as well)
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = createContext(classes, CONTEXT_PATH, PATH + "eclipselink-oxm-no-extractor.xml");
        } catch (JAXBException jex) {
            jex.printStackTrace();
            fail("JAXBContext creation failed");
        }
        
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(INSTANCE_DOC);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + INSTANCE_DOC + "]");
        }

        // setup control ParkingLot
        ParkingLot ctrlLot = getControlObject();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            ParkingLot lotObj = (ParkingLot) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", lotObj);
            assertFalse("Unmarshal did not fail as expected", ctrlLot.equals(lotObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor.XmlClassExtractorTestCases" };
        TestRunner.main(arguments);
    }
}