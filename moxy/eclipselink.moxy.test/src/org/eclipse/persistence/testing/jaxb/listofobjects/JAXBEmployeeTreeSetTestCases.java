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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBEmployeeTreeSetTestCases extends JAXBEmployeeListTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeTreeSet.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeTreeSetNoXsiType.xml";

	public JAXBEmployeeTreeSetTestCases(String name) throws Exception {
		super(name);
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Field fld = ListofObjects.class.getField("empTreeSet");

		Type[] types = new Type[1];
		types[0] = fld.getGenericType();
		setTypes(types);
	}
/*
public  List<InputStream> getControlSchemaFiles(){
		
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/treeSetEmployee.xsd");
		
		List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}
	*/
	protected Type getTypeToUnmarshalTo() throws Exception {
		
		Field fld = ListofObjects.class.getField("empTreeSet");		
		return fld.getGenericType();
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

		TreeSet<Employee> emps = new TreeSet<Employee>();

		emps.add(employee);
		emps.add(employee2);

		QName qname = new QName("rootNamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(emps);

		return jaxbElement;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
