/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.someotherpackage.SomeLameClass;

/**
 * Tests XmlJavaTypeAdapter via eclipselink-oxm.xml
 * 
 */
public class XmlAdapterTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/";
    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997;
    private static Calendar CALENDAR = new GregorianCalendar(YEAR, MONTH, DAY);
    private final static int ID = 66;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAdapterTestCases(String name) {
        super(name);
    }

    /**
     * Tests property level @XmlJavaTypeAdapter via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlJavaTypeAdapterOnProperty() {
        String metadataFile = PATH + "property/" + "eclipselink-oxm.xml";
        Class[] classesToProcess = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.MyClass.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, CONTEXT_PATH+ ".property", metadataFile, 1);      
        String controlSchema = PATH + "schema.xsd";

        // validate schema
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // validate instance doc against schema
        String src = PATH + "myclass.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new File(src)));
            assertTrue("Unmarshal operation failed - objects are not equal", obj.equals(getControlObjectProperty()));

            // test marshal
            src = tmpdir + "output.xml";
            File outputFile = new File(src);
            marshaller.marshal(obj, outputFile);
            result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
            assertTrue("Schema validation after marshal failed unxepectedly: " + result, result == null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    /**
     * Tests class level @XmlJavaTypeAdapter via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlJavaTypeAdapterOnClass() {
        String metadataFile = PATH + "/classlevel/" + "eclipselink-oxm.xml";
        
        Class[] classesToProcess = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, CONTEXT_PATH+ ".classlevel", metadataFile, 1);
         
        String controlSchema = PATH + "classlevel/schema.xsd";

        // validate schema
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // validate instance doc against schema
        String src = PATH + "classlevel/myclass.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new File(src)));
            assertTrue("Unmarshal operation failed - objects are not equal", obj.equals(getControlObjectClass()));

            // test marshal
            src = tmpdir + "output.xml";
            File outputFile = new File(src);
            marshaller.marshal(obj, outputFile);
            result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
            assertTrue("Schema validation after marshal failed unxepectedly: " + result, result == null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    /**
     * Tests package level @XmlJavaTypeAdapter via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlJavaTypeAdapterOnPackage() {
        String metadataFile = PATH + "/packagelevel/" + "eclipselink-oxm.xml";        
        Class[] classesToProcess = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass.class, SomeLameClass.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, CONTEXT_PATH+ ".packagelevel", metadataFile, 1);
  
        String controlSchema = PATH + "packagelevel/schema.xsd";

        // validate schema
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // validate instance doc against schema
        String src = PATH + "myclass.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new File(src)));
            assertTrue("Unmarshal operation failed - objects are not equal", obj.equals(getControlObjectPackage()));

            // test marshal
            src = tmpdir + "output.xml";
            File outputFile = new File(src);
            marshaller.marshal(obj, outputFile);
            result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
            assertTrue("Schema validation after marshal failed unxepectedly: " + result, result == null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    protected Object getControlObjectClass() {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass sc = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass();
        MyCalendar mCal = new MyCalendar();
        mCal.day = DAY;
        mCal.month = MONTH;
        mCal.year = YEAR;
        sc.myCal = mCal;
        sc.id = ID;
        return sc;
    }

    protected Object getControlObjectPackage() {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass sc = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass();
        sc.cal = CALENDAR;
        sc.id = ID;
        return sc;
    }

    protected Object getControlObjectProperty() {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.MyClass sc = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.MyClass();
        sc.cal = CALENDAR;
        sc.id = ID;
        return sc;
    }
}