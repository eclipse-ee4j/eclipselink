/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests multiple mappings for single field. 
 *
 */
public class MultipleMappingPerFieldTestCases extends JAXBWithJSONTestCases {
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/multiple/read.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/multiple/read.json";

    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997;

    private Calendar calendar;

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public MultipleMappingPerFieldTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{CustomQuoteRequest.class});
        calendar = new GregorianCalendar(YEAR, MONTH, DAY);
        calendar.clear(Calendar.ZONE_OFFSET);
    }

    /**
     * Create the control Object.
     */
    public Object getControlObject() {
        CustomQuoteRequest ctrlObj = new CustomQuoteRequest();
        ctrlObj.requestId = "100";
        ctrlObj.currencyPairCode = "CAD";
        ctrlObj.date = calendar;
        return ctrlObj;
    }
    
    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/multiple/oxm.xml");
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple", new StreamSource(inputStream));
        
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }

    /**
     * We expect two mappings for 'currencyPairCode'.  We will verify mapping count, 
     * user-set properties, etc.
     * 
     * Positive test.
     * 
     */
    public void testMappings() {
        XMLDescriptor xDesc = ((org.eclipse.persistence.jaxb.JAXBContext) jaxbContext).getXMLContext().getDescriptor(new QName("QuoteRequest"));
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
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/multiple/schema.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }

    /**
     * Make sure the inner class was processed,
     * 
     * Positive test.
     * 
     */
    public void testInnerClassProcessing() {
        XMLDescriptor xDesc = ((org.eclipse.persistence.jaxb.JAXBContext) jaxbContext).getXMLContext().getDescriptor(new QName("QRInnerClass"));
        assertNotNull("No descriptor was generated for CustomQuoteRequest$MyCQRInnerClass.", xDesc);
    }
}