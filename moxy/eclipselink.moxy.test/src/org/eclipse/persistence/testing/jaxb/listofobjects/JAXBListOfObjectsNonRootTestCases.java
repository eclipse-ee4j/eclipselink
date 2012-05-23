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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBListOfObjectsNonRootTestCases extends JAXBListOfObjectsNoJSONTestCases {

	protected final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
	protected final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	protected final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
	protected final static String CONTROL_FIRST_NAME = "Bob";
	protected final static String CONTROL_LAST_NAME = "Smith";
	protected final static int CONTROL_ID = 10;

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/listofobjects.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/listofobjectsNoXsiType.xml";

	public JAXBListOfObjectsNonRootTestCases(String name) throws Exception {
		super(name);
		init();		
	}
	
	public void setUp() throws Exception{
		super.setUp();
		getXMLComparer().setIgnoreOrder(true);
	}
	
	public void tearDown(){
		super.tearDown();
		getXMLComparer().setIgnoreOrder(false);
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_RESOURCE_NO_XSI_TYPE);
		Class[] classes = new Class[2];
		classes[0] = ListofObjects.class;
		classes[1] = Employee.class;
		setClasses(classes);
	}
		
	   public  List<InputStream> getControlSchemaFiles(){
			
		   InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/listofobjects.xsd");
			
			List<InputStream> controlSchema = new ArrayList<InputStream>();		
			controlSchema.add(instream);				
			return controlSchema;
		}	  

	protected Object getControlObject() {
		ListofObjects listofObjects = new ListofObjects();

		Vector<Integer> integers = new Vector<Integer>();
		integers.add(new Integer("10"));
		integers.add(new Integer("20"));
		integers.add(new Integer("30"));
		integers.add(new Integer("40"));

		listofObjects.setIntegerList(integers);

		
		TreeSet<Integer> integerSet = new TreeSet<Integer>();
		integerSet.add(new Integer("20"));
		integerSet.add(new Integer("40"));
		integerSet.add(new Integer("60"));
		listofObjects.setIntegerSet(integerSet);
		
		Integer[] integerArray = new Integer[4];
		integerArray[0] = 10;
		integerArray[1] = 20;
		integerArray[2] = 30;
		integerArray[3] = 40;
		listofObjects.setIntegerArray(integerArray);

		int[] intArray = new int[2];
		intArray[0] = 1;
		intArray[1] = 2;
		listofObjects.intArray = intArray;
		
		Employee[] empArray = new Employee[2];
		empArray[0] = getEmployee1();
		empArray[1] = getEmployee2();
		listofObjects.setEmpArray(empArray);

		List<Employee> empList = new ArrayList<Employee>();
		empList.add(getEmployee1());
		empList.add(getEmployee2());
		listofObjects.setEmpList(empList);

		boolean[] booleans = new boolean[4];
		booleans[0] = Boolean.TRUE.booleanValue();
		booleans[1] = Boolean.TRUE.booleanValue();
		booleans[2] = Boolean.FALSE.booleanValue();
		booleans[3] = Boolean.FALSE.booleanValue();
		listofObjects.setBooleanArray(booleans);

		HashMap<String, Integer> stringIntegerMap = new HashMap<String, Integer>();	
		stringIntegerMap.put("string1", new Integer(10));
		stringIntegerMap.put("string2", new Integer(20));
		stringIntegerMap.put("string3", new Integer(30));
		listofObjects.setStringIntegerHashMap(stringIntegerMap);
		
		ConcurrentHashMap<String, Integer> stringIntegerConcurrentMap = new ConcurrentHashMap<String, Integer>();	
		stringIntegerConcurrentMap.put("string1", new Integer(10));
		stringIntegerConcurrentMap.put("string2", new Integer(20));
		stringIntegerConcurrentMap.put("string3", new Integer(30));
		listofObjects.setStringIntegerConcurrentMap(stringIntegerConcurrentMap);

		LinkedList<Integer> integersLinkedList = new LinkedList<Integer>();
		integersLinkedList.add(new Integer(5));
		integersLinkedList.add(new Integer(15));
		integersLinkedList.add(new Integer(25));
		listofObjects.setIntegerLinkedList(integersLinkedList);
		
		Hashtable<String, Employee> stringEmployeeTable = new Hashtable<String, Employee>();	
		stringEmployeeTable.put("string1", getEmployee1());
		listofObjects.setStringEmployeeHashtable(stringEmployeeTable);
		
		Stack<BigDecimal> theStack = new Stack<BigDecimal>();
		theStack.push(new BigDecimal("1"));
		theStack.push(new BigDecimal("5"));
		theStack.push(new BigDecimal("10"));
		listofObjects.setBigDecimalStack(theStack);
		
		MyList<String> test = new MyList<String>();
		test.add("aaa");
		test.add("bbb");
		test.add("ccc");
		listofObjects.setStringMyList(test);
		
		LinkedList stringQueue = new LinkedList<String>();
		stringQueue.add("ddd");
		stringQueue.add("eee");
		listofObjects.setStringQueue(stringQueue);
		
		QName qname = new QName("listOfObjectsNamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(listofObjects);
		return jaxbElement;
	}
		
	protected Type getTypeToUnmarshalTo() {
		return ListofObjects.class;
	}

	private Employee getEmployee1() {
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
		
		return employee;
	}

	private Employee getEmployee2() {
		Employee employee2 = new Employee();
		employee2.firstName = CONTROL_FIRST_NAME + "2";
		employee2.lastName = CONTROL_LAST_NAME + "2";
		employee2.setBlah("Some Other String");
		employee2.id = 100;

		ArrayList responsibilities2 = new ArrayList();
		responsibilities2.add(CONTROL_RESPONSIBILITY1);
		employee2.responsibilities = responsibilities2;
		return employee2;

	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
	
	
	public void testTypeToSchemaTypeMap(){
		HashMap<Type, javax.xml.namespace.QName> typesMap = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getTypeToSchemaType();		
		int mapSize = typesMap.size();
		assertEquals(2, mapSize);
	}
	
}
