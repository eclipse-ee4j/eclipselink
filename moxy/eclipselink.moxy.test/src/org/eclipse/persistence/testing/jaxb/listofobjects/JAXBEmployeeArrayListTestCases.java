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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JAXBEmployeeArrayListTestCases extends JAXBEmployeeListTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeArrayList.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeArrayListNoXsiType.xml";

	public JAXBEmployeeArrayListTestCases(String name) throws Exception {
		super(name);
	}

	public List<InputStream> getControlSchemaFiles(){
		
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/arrayListEmployee.xsd");
			
		List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}
		
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Type[] types = new Type[1];
		types[0] = getTypeToUnmarshalTo();
		setTypes(types);
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		Field fld = ListofObjects.class.getField("empArrayList");
		return fld.getGenericType();
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}
}
