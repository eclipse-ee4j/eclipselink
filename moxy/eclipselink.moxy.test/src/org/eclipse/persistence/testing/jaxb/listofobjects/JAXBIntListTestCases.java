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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBIntListTestCases extends JAXBIntegerListTestCases {
	
	private Type typeToUnmarshalTo;
	
	public JAXBIntListTestCases(String name) throws Exception {
		super(name);
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		if(typeToUnmarshalTo == null){
		
		Type listOfInts = new ParameterizedType() {
		      Type[] typeArgs = {int.class};
		      public Type[] getActualTypeArguments() { return typeArgs;}
		      public Type getOwnerType() { return null; }
		      public Type getRawType() { return List.class; }      
		    };
		    typeToUnmarshalTo = listOfInts;
		}
		return typeToUnmarshalTo;
	}

	protected Object getControlObject() {
		ArrayList<Integer> integers = new ArrayList<Integer>();
	     integers.add(10);
		integers.add(20);
		integers.add(30);
		integers.add(40);

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(integers);

		return jaxbElement;
	}
	
}
