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
 * dmccann - November 22/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests multiple mappings for single field. 
 *
 */
public class MultipleMappingPerFieldTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/multiple/";
    private static final String XSD_DOC = PATH + "schema.xsd";
    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997;
    private JAXBContext jCtx;

    private Calendar calendar;

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public MultipleMappingPerFieldTestCases(String name) {
        super(name);

        calendar = new GregorianCalendar(YEAR, MONTH, DAY);
        calendar.clear(Calendar.ZONE_OFFSET);
    }

    /**
     * Create the control Object.
     */
    private CustomQuoteRequest getControlObject() {
        CustomQuoteRequest ctrlObj = new CustomQuoteRequest();
        ctrlObj.requestId = "100";
        ctrlObj.currencyPairCode = "CAD";
        ctrlObj.date = calendar;
        return ctrlObj;
    }
    
    /**
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        Map<String, File> properties = new HashMap<String, File>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(PATH + "oxm.xml"));
        jCtx = JAXBContextFactory.createContext(new Class[] { CustomQuoteRequest.class }, properties);
    }

    /**
     * We expect two mappings for 'currencyPairCode'.  We will verify mapping count, 
     * user-set properties, etc.
     * 
     * Positive test.
     * 
     */
    public void testMappings() {
        XMLDescriptor xDesc = ((org.eclipse.persistence.jaxb.JAXBContext) jCtx).getXMLContext().getDescriptor(new QName("QuoteRequest"));
        assertNotNull("No descriptor was generated for CustomQuoteRequest.", xDesc);
        int currencyPairCodeCount = 0;
        int dateCount = 0;
        Vector<DatabaseMapping> mappings = xDesc.getMappings();
        for (int i=0; i < mappings.size(); i++) {
            DatabaseMapping mapping = mappings.get(i);
            if (mapping.getAttributeName().equals("currencyPairCode")) {
                currencyPairCodeCount++;
                
                // check user-set properties
                Map props = mapping.getProperties();
                assertNotNull("No user-defined properties exist on the mapping for [currencyPairCode]", props);
                assertTrue("Expected [2] user-defined properties, but there were [" + props.size() + "]", props.size() == 2);
                if (mapping.getField().getName().equals("QuoteReq/Instrmt/@Sym")) {
                    // verify value-types
                    assertTrue("Expected value-type [String] for key [1] but was [" + props.get("1").getClass().getName() + "]", props.get("1") instanceof String);
                    assertTrue("Expected value-type [Integer] for key [2] but was [" + props.get("2").getClass().getName() + "]", props.get("2") instanceof Integer);
                    // verify values
                    assertTrue("Expected property value [A] for key [" + 1 + "] but was [" + props.get("1") + "]", "A".equals(props.get("1")));
                    assertTrue("Expected property value [66] for key [" + 2 + "] but was [" + props.get("2") + "]", 66 == (Integer) props.get("2"));
                } else {
                    // assume "QuoteReq/Leg/@Sym"
                    // verify value-types
                    assertTrue("Expected value-type [String] for key [2] but was [" + props.get("1").getClass().getName() + "]", props.get("1") instanceof String);
                    assertTrue("Expected value-type [Double] for key [1] but was [" + props.get("2").getClass().getName() + "]", props.get("2") instanceof Double);
                    // verify values
                    assertTrue("Expected property value [B] for key [" + 1 + "] but was [" + props.get("1") + "]", "B".equals(props.get("1")));
                    assertTrue("Expected property value [9.9] for key [" + 2 + "] but was [" + props.get("2") + "]", 9.9 == (Double) props.get("2"));
                }
            } else if (mapping.getAttributeName().equals("date")) {
                dateCount++;
            }
        }
        assertTrue("Expected [2] mappings for attribute [currencyPairCode], but was [" + currencyPairCodeCount + "]", currencyPairCodeCount == 2);
        assertTrue("Expected [3] mappings for attribute [date], but was [" + dateCount + "]", dateCount == 3);
    }
    
    /**
     * Tests schema generation.
     * 
     * Positive test.
     */
    public void testSchemaGen() throws Exception {
        MySchemaOutputResolver employeeResolver = new MySchemaOutputResolver(); 
        jCtx.generateSchema(employeeResolver);
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_DOC));
    }

    /**
     * Tests unmarshal operation.  
     * 
     * Positive test.
     */
    public void testUnmarshal() {
        // load instance doc
        String src = PATH + "read.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        CustomQuoteRequest ctrlEmp = getControlObject();

        try {
            CustomQuoteRequest empObj = (CustomQuoteRequest) jCtx.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests marshal operation.  
     * 
     * Positive test.
     */
    public void testMarshal() {
        // setup control document
        String src = PATH + "write.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            CustomQuoteRequest ctrlEmp = getControlObject();
            Marshaller marshaller = jCtx.createMarshaller();
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}