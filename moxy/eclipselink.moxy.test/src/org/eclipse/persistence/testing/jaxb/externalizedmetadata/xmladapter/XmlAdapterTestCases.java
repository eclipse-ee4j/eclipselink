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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary.Customer;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.someotherpackage.SomeLameClass;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref.Bar;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref.Foo;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref.FooBar;
import org.w3c.dom.Document;

/**
 * Tests XmlJavaTypeAdapter via eclipselink-oxm.xml
 * 
 */
public class XmlAdapterTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/";

    private static final String PROP_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property";
    private static final String PROP_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/property/";
    
    private static final String PKG_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel";
    private static final String PKG_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/";
    
    private static final String CLS_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel";
    private static final String CLS_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/classlevel/";

    private static final String HEX_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary";
    private static final String HEX_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/hexbinary/";

    private static final String ELT_REF_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref";
    private static final String ELT_REF_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/xmlelementref/";

    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997;
    private static Calendar CALENDAR = new GregorianCalendar(YEAR, MONTH, DAY);
    private final static int ID = 66;
    public final static String BAR_ITEM = "66";
    public final static String FOOBAR_ITEM = "99";
    Class[] propArray;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAdapterTestCases(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        propArray = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.MyClass.class };
    }

    /**
     * Tests property level @XmlJavaTypeAdapter via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlJavaTypeAdapterOnProperty() {
        String metadataFile = PROP_PATH + "eclipselink-oxm.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(propArray, PROP_CONTEXT_PATH, metadataFile, 1);      
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
        String metadataFile = CLS_PATH + "eclipselink-oxm.xml";
        
        Class[] classesToProcess = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, CLS_CONTEXT_PATH, metadataFile, 1);
         
        String controlSchema = CLS_PATH + "schema.xsd";

        // validate schema
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // validate instance doc against schema
        String src = CLS_PATH + "myclass.xml";
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
        String metadataFile = PKG_PATH + "eclipselink-oxm.xml";        
        Class[] classesToProcess = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass.class, SomeLameClass.class };
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(classesToProcess, PKG_CONTEXT_PATH, metadataFile, 1);
  
        String controlSchema = PKG_PATH + "schema.xsd";

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
    
    public void testHexBinary() {
        String src = HEX_PATH + "hexbinary.xml";
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(HEX_PATH + "hexbinary-oxm.xml"));
        JAXBContext ctx = null;
        try {
            ctx = JAXBContextFactory.createContext(new Class[]{ Customer.class }, properties);
        } catch (JAXBException jaxbex) {
            fail("JAXBContext creation failed");
        }
        try {
            Object result = ctx.createUnmarshaller().unmarshal(new File(src));
            assertTrue("Unmarshal failed - objects are not equal", getControlCustomer().equals(result));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during unmarshal.");
        } 
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(getControlCustomer(), testDoc);
            assertTrue("The Customer did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during marshal.");
        }
    }

    public void testAdapterWithElementRef() {
        String src = ELT_REF_PATH + "foo.xml";
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(ELT_REF_PATH + "foo-oxm.xml"));
        JAXBContext ctx = null;
        try {
            ctx = JAXBContextFactory.createContext(new Class[]{ Foo.class, Bar.class }, properties);
        } catch (JAXBException jaxbex) {
            fail("JAXBContext creation failed");
        }
        try {
            Object result = ctx.createUnmarshaller().unmarshal(new File(src));
            assertTrue("Unmarshal failed - objects are not equal", getControlFoo().equals(result));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during unmarshal.");
        } 
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(getControlFoo(), testDoc);
            //marshaller.marshal(getControlFoo(), System.out);
            assertTrue("Foo did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during marshal.");
        }
    }
    
    public void testAdapterWithElementRefs() {
        String src = ELT_REF_PATH + "foobar.xml";
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(ELT_REF_PATH + "foobar-oxm.xml"));
        JAXBContext ctx = null;
        try {
            ctx = JAXBContextFactory.createContext(new Class[]{ Foo.class, Bar.class, FooBar.class }, properties);
        } catch (JAXBException jaxbex) {
            fail("JAXBContext creation failed");
        }
        try {
            Object result = ctx.createUnmarshaller().unmarshal(new File(src));
            assertTrue("Unmarshal failed - objects are not equal", getControlFoobar().equals(result));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during unmarshal.");
        } 
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(getControlFoobar(), testDoc);
            //marshaller.marshal(getControlFoobar(), System.out);
            assertTrue("Foo did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during marshal.");
        }
    }

    protected Object getControlCustomer() {
        byte[] bytes = new byte[] {30,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4};
        Customer customer = new Customer();
        customer.hexBytes = bytes;
        customer.base64Bytes = bytes;
        return customer;
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
    
    protected Object getControlFoo() {
        Foo foo = new Foo();
        foo.item = BAR_ITEM;
        return foo;
    }

    protected Object getControlFoobar() {
        Foo foo = new Foo();
        foo.item = FOOBAR_ITEM;
        return foo;
    }
}