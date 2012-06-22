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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class JAXBStringEmployeeHashtableTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/stringEmployeeHashtable.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/stringEmployeeMapNoXsiType.xml";
	protected final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
	protected final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	protected final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
	protected final static String CONTROL_FIRST_NAME = "Bob";
	protected final static String CONTROL_LAST_NAME = "Smith";
	protected final static int CONTROL_ID = 10;

	public JAXBStringEmployeeHashtableTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo();
		setTypes(types);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		getXMLComparer().setIgnoreOrder(true);
	}
	
	public void tearDown(){
		super.tearDown();
		getXMLComparer().setIgnoreOrder(false);
	}	 
   
	public List< InputStream> getControlSchemaFiles(){		
		InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/stringEmployeeHashtable.xsd");
		InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/stringEmployeeHashtable2.xsd");
		List<InputStream> controlSchema= new ArrayList<InputStream>();
				
		controlSchema.add(instream1);
		controlSchema.add(instream2);
				
        return controlSchema;
    }	

protected Object getControlObject() {
	ArrayList responsibilities = new ArrayList();
	responsibilities.add(CONTROL_RESPONSIBILITY1);
	responsibilities.add(CONTROL_RESPONSIBILITY2);
	responsibilities.add(CONTROL_RESPONSIBILITY3);

	Employee employee = new Employee();
	employee.firstName = CONTROL_FIRST_NAME;
	employee.lastName = CONTROL_LAST_NAME;

	Calendar cal = Calendar.getInstance();
	cal.clear();
	cal.set(2005, 04, 24, 16, 06, 53);

	employee.id = CONTROL_ID;

	employee.responsibilities = responsibilities;

	employee.setBlah("Some String");

	Employee employee2 = new Employee();
	employee2.firstName = CONTROL_FIRST_NAME + "2";
	employee2.lastName = CONTROL_LAST_NAME + "2";
	employee2.setBlah("Some Other String");
	employee2.id = 100;

	ArrayList responsibilities2 = new ArrayList();
	responsibilities2.add(CONTROL_RESPONSIBILITY1);
	employee2.responsibilities = responsibilities2;	
	
	Hashtable<String, Employee> emps = new Hashtable<String, Employee>();
	emps.put(employee.firstName, employee);
	emps.put(employee2.firstName, employee2);

	QName qname = new QName("rootNamespace", "root");
	JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
	jaxbElement.setValue(emps);

	return jaxbElement;
}

protected Type getTypeToUnmarshalTo() throws Exception {	
	Field fld = ListofObjects.class.getField("stringEmployeeHashtable");
	Type fieldType =  fld.getGenericType();
	return fieldType;
}

protected String getNoXsiTypeControlResourceName() {
	return XML_RESOURCE_NO_XSI_TYPE;
}
}
