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
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


public class JAXBIntegerArrayTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerArray.xml";
	protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerArrayNoXsiType.xml";

	public JAXBIntegerArrayTestCases(String name) throws Exception {
		super(name);
		init();
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		return Integer[].class;
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Integer[].class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		Integer[] integers = new Integer[4];
		integers[0] = 10;
		integers[1] = 20;
		integers[2] = 30;
		integers[3] = 40;

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,null);
		jaxbElement.setValue(integers);

		return jaxbElement;
	}

	    
    public List< InputStream> getControlSchemaFiles(){	
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/integerArray.xsd");
		
	    List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}
    
	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
