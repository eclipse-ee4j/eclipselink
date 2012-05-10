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
 *     Denise Smith  November 16, 2009 - Initial implementation
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

public class JAXBIntegerMyListTestCases extends JAXBIntegerArrayTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerMyList.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerMyList.json";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerMyListNoXsiType.xml";

	public MyList<Integer> integerMyList;
	
	public JAXBIntegerMyListTestCases(String name) throws Exception {
		super(name);
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);

		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo();
		setTypes(types);
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		Field fld = getClass().getField("integerMyList");
		return fld.getGenericType();
	}

	protected Object getControlObject() {
		MyList<Integer> integers = new MyList<Integer>();
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
		
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/integerMyList.xsd");
			
		List<InputStream> controlSchema = new ArrayList<InputStream>();
			controlSchema.add(instream);
			return controlSchema;
		}
		

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}

}
