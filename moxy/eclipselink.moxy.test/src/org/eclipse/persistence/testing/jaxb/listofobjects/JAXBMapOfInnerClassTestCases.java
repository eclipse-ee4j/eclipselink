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
 *     Denise Smith  December 15, 2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.listofobjects.ClassWithInnerClass.MyInner;

public class JAXBMapOfInnerClassTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassMap.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassMap.json";
	protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassMap.xml";

	public TreeMap<MyInner, MyInner> myMap;
	
	public JAXBMapOfInnerClassTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	protected Type getTypeToUnmarshalTo() throws Exception {
		return getClass().getField("myMap").getGenericType();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo(); 
		setTypes(types);	
	}

	protected Object getControlObject() {
		MyInner myInner1 = new MyInner();
		myInner1.innerName = "aaa";
		
		MyInner myInner2= new MyInner();
		myInner2.innerName = "bbb";
		
		Map<MyInner, MyInner> theMap = new TreeMap<MyInner, MyInner>();
		theMap.put(myInner1, myInner2);
		theMap.put(myInner2, myInner1);
		
		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,theMap);

		return jaxbElement;
	}

	public List<InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/innerClassMap.xsd");		
	    List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
	
    public Map getProperties() {
        Map props = new HashMap();
        props.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, "listOfObjectsNamespace");
        return props;
    }	
}
