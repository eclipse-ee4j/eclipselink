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
 *     Denise Smith - May 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeNullValueTestCases extends JAXBWithJSONTestCases{
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootNull.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootNull.json";
	
	public XmlVariableNodeNullValueTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{Root.class});
	}
	
	public boolean isUnmarshalTest(){
        return false;
    }
	
	@Override
	protected Object getControlObject() {
		Root r = new Root();
		r.name = "theRootName";
		r.things = new ArrayList<Thing>();
		Thing thing1 = new Thing();
		thing1.thingName = null;
		thing1.thingValue = "thingavalue";
		
		Thing thing2 = new Thing();
		thing2.thingName = "thingb";
		thing2.thingValue = "thingbvalue";
		
		Thing thing3 = new Thing();
		thing3.thingName = "thingc";
		thing3.thingValue = "thingcvalue";
		r.things.add(thing1);
		r.things.add(thing2);
		r.things.add(thing3);
		return r;
	}

	public void assertMarshalException(Exception exception) throws Exception {
	    Throwable nested = exception.getCause();// getLinkedException();
        assertTrue(nested instanceof XMLMarshalException);
        if(((XMLMarshalException)nested).getErrorCode() == XMLMarshalException.NULL_VALUE_NOT_ALLOWED_FOR_VARIABLE){
            return;
        }    
        fail("Expected exception did not occur.");
    }
}
