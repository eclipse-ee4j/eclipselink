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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

//import org.eclipse.persistence.testing.jaxb.employee.Employee;

public class JAXBEmployeesAndIntegersTestCases extends
		JAXBListOfObjectsTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerList.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerListNoXsiType.xml";

	public JAXBEmployeesAndIntegersTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);

		Type[] types = new Type[4];
		Field fld = ListofObjects.class.getField("empList");
		types[0] = fld.getGenericType();

		fld = ListofObjects.class.getField("integerList");
		types[1] = fld.getGenericType();

		types[2] = Employee[].class;
		types[3] = Integer[].class;

		setTypes(types);
	}

	protected Type getTypeToUnmarshalTo() {

		try{
			Field fld = ListofObjects.class.getField("integerList");
			return fld.getGenericType();
		}catch(Exception e){
			fail(e.getMessage());
		}
		return null;
	}
	
	public List< InputStream> getControlSchemaFiles(){		
		  InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/employeesAndIntegers2.xsd");					
			
			InputStream instream3 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/employeesAndIntegers3.xsd");
			
			InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/employeesAndIntegers1.xsd");
			List<InputStream> controlSchema= new ArrayList<InputStream>();
			
			controlSchema.add(instream1);
			controlSchema.add(instream2);			
			controlSchema.add(instream3);
			
			return controlSchema;
	}
	 
	 
	protected Object getControlObject() {
		List<Integer> integers = new ArrayList<Integer>();
		integers.add(new Integer("10"));
		integers.add(new Integer("20"));
		integers.add(new Integer("30"));
		integers.add(new Integer("40"));

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(integers);

		return jaxbElement;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
