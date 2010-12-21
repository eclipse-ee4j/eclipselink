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
* Denise Smith - Sept 28/2009 - 2.0 
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects.externalizedmetadata;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;

public class JAXBMultipleMapsTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/multipleMaps.xml";
	
	public Map<String, Integer> mapField1;	
	
	private Type[] types;
	
	public JAXBMultipleMapsTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		types = new Type[5];
		types[0] = getTypeToUnmarshalTo();
				  
		Type mapType2 = new ParameterizedType() {
		Type[] typeArgs = { Calendar.class, Float.class };
		 public Type[] getActualTypeArguments() { return typeArgs;}
		 public Type getOwnerType() { return null; }
		 public Type getRawType() { return Map.class; }      
		};
		types[1] = mapType2;		
	
		Type mapType3 = new ParameterizedType() {
			Type[] typeArgs = { Person.class, Job.class };
			 public Type[] getActualTypeArguments() { return typeArgs;}
			 public Type getOwnerType() { return null; }
			 public Type getRawType() { return Map.class; }      
			};
			types[2] = mapType3;

			Type listType = new ParameterizedType() {
				Type[] typeArgs = { Person.class};
				 public Type[] getActualTypeArguments() { return typeArgs;}
				 public Type getOwnerType() { return null; }
				 public Type getRawType() { return List.class; }      
				};
				types[3] = listType;
				
			Type listType2 = new ParameterizedType() {
				Type[] typeArgs = { String.class};
				 public Type[] getActualTypeArguments() { return typeArgs;}
				 public Type getOwnerType() { return null; }
				 public Type getRawType() { return List.class; }      
				};
			types[4] = listType2;					
		
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
	
	public List<InputStream> getControlSchemaFiles() {
		InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/multipleMaps.xsd");
		InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/multipleMaps2.xsd");

		List<InputStream> controlSchema= new ArrayList<InputStream>();
        controlSchema.add(instream1);
		controlSchema.add(instream2);
		return controlSchema;
		
	}
   

    protected Object getControlObject() {
    	    	
    	Map<String, Integer> theMap = new HashMap<String, Integer>();
    	theMap.put("aaa", new Integer(1));
    	theMap.put("bbb", new Integer(2));

    	QName qname = new QName("root");
    	JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
    	jaxbElement.setValue(theMap);
    	return jaxbElement;	 
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
	    Field fld = getClass().getField("mapField1");
	    Type fieldType =  fld.getGenericType();
	    return fieldType;
    }

    protected String getNoXsiTypeControlResourceName() {    	
	    return XML_RESOURCE;
	}

	public void testTypeToSchemaTypeMap(){
		HashMap<Type, javax.xml.namespace.QName> typesMap = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getTypeToSchemaType();		
		int mapSize = typesMap.size();
		assertEquals(7, mapSize);
		
		assertNotNull("Type was not found in TypeToSchemaType map.", typesMap.get(types[0]));
		assertNotNull("Type was not found in TypeToSchemaType map.", typesMap.get(types[1]));
		assertNotNull("Type was not found in TypeToSchemaType map.", typesMap.get(types[2]));
		assertNotNull("Type was not found in TypeToSchemaType map.", typesMap.get(types[3]));
		assertNotNull("Type was not found in TypeToSchemaType map.", typesMap.get(types[4]));		
	}

}
