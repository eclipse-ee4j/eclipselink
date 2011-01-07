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
 * dmccann - August 24/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.Employee;
import org.w3c.dom.Document;

/**
 * Tests inheritance configuration via XmlDiscriminatorNode & XmlDiscriminatorValue. 
 *
 */
public class XmlDiscriminatorTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmldiscriminator/";
    private static final String OXM_DOC = PATH + "vehicle-oxm.xml";
    private static final String XSD_DOC = PATH + "vehicle.xsd";
    private static final String INSTANCE_DOC = PATH + "vehicle.xml";
    private static final String WRITE_DOC = PATH + "vehicle-write.xml";
    private Class[] classes;
    private MySchemaOutputResolver resolver;
    private JAXBContext jCtx;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlDiscriminatorTestCases(String name) {
        super(name);
    }
    
    /**
     * This method will be responsible for schema generation, which will create the 
     * JAXBContext we will use and validate the eclipselink metadata file.
     */
    public void setUp() throws Exception {
        super.setUp();
        classes = new Class[] { Car.class, Vehicle.class };
        // schema generation also creates the JAXBContext
        resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_DOC, 1);
        jCtx = getJAXBContext();
        assertNotNull("Setup failed: JAXBContext is null", jCtx);
    }
    
    private JAXBElement getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 26;
        car.model = "Mustang GT";
        car.manufacturer = "Ford";
        car.topSpeed = 354;
        return new JAXBElement(new QName("vehicle-data"), Vehicle.class, car);
    }
    
    /**
     * Validate schema generation.
     * 
     */
    public void testSchemaGen() {
        // validate vehicle schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_DOC));
    }
    
    public void testUnmarshalCar() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(INSTANCE_DOC);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + INSTANCE_DOC + "]");
        }

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Object jElt = unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", jElt);
            assertTrue("Unmarshalled object is not a JAXBElement.", jElt instanceof JAXBElement);
            JAXBElement ctrlElt = getControlObject();
            JAXBElement jaxbElt = (JAXBElement) jElt;
            assertTrue("JAXBElement names are not equal;  expected ["+ctrlElt.getName()+"] but was ["+jaxbElt.getName()+"].", ctrlElt.getName().equals(jaxbElt.getName()));
            assertTrue("JAXBElement declared types are not equal; expected ["+ctrlElt.getDeclaredType()+"] but was ["+jaxbElt.getDeclaredType()+"].", ctrlElt.getDeclaredType() == jaxbElt.getDeclaredType());
            assertTrue("JAXBElement values are not equal; expected ["+ctrlElt.getValue()+"] but was ["+jaxbElt.getValue()+"].", ctrlElt.getValue().equals(jaxbElt.getValue()));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    public void testMarshalCar() {
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(WRITE_DOC);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + WRITE_DOC + "].");
        }

        Marshaller marshaller = jCtx.createMarshaller();
        try {
            //marshaller.marshal(getControlObject(), System.out);
            marshaller.marshal(getControlObject(), testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));

        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator.XmlDiscriminatorTestCases" };
        TestRunner.main(arguments);
    }
}
