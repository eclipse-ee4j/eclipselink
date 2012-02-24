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
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue;

//Example 2

import java.io.StringWriter;

import javax.xml.bind.MarshalException;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueSimpleContentTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlvalue/xmlvaluesimplecontent.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlvalue/xmlvaluesimplecontent.json";

    public XmlValueSimpleContentTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = InternationalPrice.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(JAXBMarshaller.JSON_VALUE_WRAPPER, "valuewrapper");
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_VALUE_WRAPPER, "valuewrapper");
    }

    protected Object getControlObject() {
    	InternationalPrice p = new InternationalPrice();
        p.setPrice(123.99);
        p.currency = "CANADIAN DOLLAR";
        return p;
    }
    
    public void testJSONNoValuePropException() throws Exception{
    	jaxbMarshaller = jaxbContext.createMarshaller();
    	jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
    	try{
    	    jaxbMarshaller.marshal(getControlObject(), new StringWriter());
    	}catch(MarshalException e){
    		assertTrue(e.getLinkedException() instanceof JAXBException);
    		assertTrue(((JAXBException)e.getLinkedException()).getErrorCode() == JAXBException.JSON_VALUE_WRAPPER_REQUIRED);
    		return;
    	}
    	fail("An exception should have occurred");
    }
}
