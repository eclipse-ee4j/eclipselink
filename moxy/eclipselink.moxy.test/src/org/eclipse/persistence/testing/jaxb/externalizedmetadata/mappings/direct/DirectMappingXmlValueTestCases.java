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
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DirectMappingXmlValueTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/price.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/price.json";
    private static final String CURRENCY = "CAD";
    private static final double PRICE = 123.456;
	public DirectMappingXmlValueTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[]{Price.class});
	}
	
	public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/eclipselink-oxm-xml-value.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	    
	    
	public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/price.xsd");	    	
	   	controlSchemas.add(is);
	   	
	   	super.testSchemaGen(controlSchemas);
	   	
	   	InputStream srcInputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
	   	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/price.xsd");
	   	String result = validateAgainstSchema(srcInputStream, new StreamSource(schemaInputStream));
        assertTrue("Instance doc validation (price.xml) failed unxepectedly: " + result, result == null);
	}
	
	public Object getWriteControlObject() {
	    Price ctrlPrice = new Price();
        ctrlPrice.currency = CURRENCY;
        ctrlPrice.price = new BigDecimal(PRICE);
        return ctrlPrice;
	}
	
	protected Object getControlObject() {
	    Price ctrlPrice = new Price();
        ctrlPrice.currency = CURRENCY;
        return ctrlPrice;
	}
	
	public void testRoundTrip(){
		//not applicable with write only mappings
	}
}
