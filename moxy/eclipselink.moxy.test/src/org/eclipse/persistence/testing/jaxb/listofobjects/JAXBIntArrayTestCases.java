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
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.employee.Employee;

public class JAXBIntArrayTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/intArray.xml";
	protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/intArrayNoXsiType.xml";

	public JAXBIntArrayTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = int[].class;
		setClasses(classes);

	}
	    
    public List< InputStream> getControlSchemaFiles(){			 		   
	 	InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/intArray.xsd");
		
		List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}

	protected Type getTypeToUnmarshalTo() {
		return int[].class;
	}

	protected Object getControlObject() {
		int[] ints = new int[4];
		ints[0] = 10;
		ints[1] = 20;
		ints[2] = 30;
		ints[3] = 40;

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(ints);

		return jaxbElement;
	}

	protected void comparePrimitiveArrays(Object controlValue, Object testValue) {
		int[] controlArray = (int[]) controlValue;
		int[] testArray = (int[]) testValue;

		assertEquals(controlArray.length, testArray.length);
		for (int i = 0; i < controlArray.length; i++) {
			assertEquals(controlArray[i], testArray[i]);
		}

		// fail("NEED TO COMPARE PRIMITIVE ARRAYS");
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
