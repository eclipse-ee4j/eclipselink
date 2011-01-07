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
 *     Denise Smith  November 13, 2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBBigDecimalStackTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStack.xml";
	protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStackNoXsiType.xml";

	public Stack<BigDecimal> test;
	
	public JAXBBigDecimalStackTestCases(String name) throws Exception {
		super(name);
		init();
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		Field fld = getClass().getField("test");
		return fld.getGenericType();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo();
		setTypes(types);	
	}

	protected Object getControlObject() {
		Stack<BigDecimal> bigDecimals = new Stack<BigDecimal>();
		bigDecimals.push(new BigDecimal("2"));
		bigDecimals.push(new BigDecimal("4"));
		bigDecimals.push(new BigDecimal("6"));
		bigDecimals.push(new BigDecimal("8"));
		
		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,null);
		jaxbElement.setValue(bigDecimals);

		return jaxbElement;
	}

	    
    public List< InputStream> getControlSchemaFiles(){	
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStack.xsd");
		
	    List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}
    
	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
