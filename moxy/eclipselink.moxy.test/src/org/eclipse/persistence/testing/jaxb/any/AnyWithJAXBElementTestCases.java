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
 * Denise Smith - September 15 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.any;

import java.io.InputStream;
import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AnyWithJAXBElementTestCases extends JAXBTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/any/any.xml";

	public AnyWithJAXBElementTestCases(String name) throws Exception {
            super(name);
	    setControlDocument(XML_RESOURCE);        
            Class[] classes = new Class[2];
            classes[0] = ObjectFactory.class;
            classes[1] = Root.class;
            setClasses(classes);
	}

	protected Object getControlObject() {
		Root theRoot = new Root();
		QName qname = new QName("namespace","dateLocalName");
	
		XMLGregorianCalendar theCal = null;
		try {
			theCal = DatatypeFactory.newInstance().newXMLGregorianCalendar("2002-04-29");
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("An error occurred creating the control object");
		}
		JAXBElement jbe = new JAXBElement(qname, XMLGregorianCalendar.class, theCal);
		
		theRoot.setAny(jbe);
		return theRoot;
	}

	public static void main(String[] args){
		String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.any.AnyWithJAXBElementTestCases"};
		TestRunner.main(arguments);
	}
	
}
