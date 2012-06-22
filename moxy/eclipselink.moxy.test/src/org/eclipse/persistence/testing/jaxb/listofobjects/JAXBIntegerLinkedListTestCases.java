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
 *     Denise Smith  November 13, 2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBIntegerLinkedListTestCases extends JAXBIntegerArrayTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerLinkedList.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerLinkedListNoXsiType.xml";

	public JAXBIntegerLinkedListTestCases(String name) throws Exception {
		super(name);
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);

		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo();
		setTypes(types);
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		Field fld = ListofObjects.class.getField("integerLinkedList");
		return fld.getGenericType();
	}

	protected Object getControlObject() {
		LinkedList<Integer> integers = new LinkedList<Integer>();
		integers.add(new Integer("10"));
		integers.add(new Integer("20"));
		integers.add(new Integer("30"));
		integers.add(new Integer("40"));

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(integers);

		return jaxbElement;
	}
	
	public  List<InputStream> getControlSchemaFiles(){
		
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/linkedListInteger.xsd");
			
		List<InputStream> controlSchema = new ArrayList<InputStream>();
			controlSchema.add(instream);
			return controlSchema;
		}
		

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}