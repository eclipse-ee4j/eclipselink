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
 *     Denise Smith - January 26th, 2010 - 2.0.1
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class GenericArrayTypeTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/genericArrayType.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/genericArrayType.json";

	public List<byte[]> testField;
	public List<String> testStringField;
	
	public GenericArrayTypeTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	 
	        typeMappingInfos = new TypeMappingInfo[3];
	        TypeMappingInfo tpi = new TypeMappingInfo();
	        tpi.setXmlTagName(new QName("","testTagname"));		
	        tpi.setElementScope(ElementScope.Global);		
	        tpi.setType(getClass().getField("testField").getGenericType());	        
	        typeMappingInfos[0] = tpi;	    
	        
	        TypeMappingInfo tmi2 = new TypeMappingInfo();
	        tmi2.setXmlTagName(new QName("","testTagname2"));		
	        tmi2.setElementScope(ElementScope.Global);		
	        tmi2.setType(getClass().getField("testField").getGenericType());	        
	        typeMappingInfos[1] = tmi2;	     
	        
	        TypeMappingInfo tmi3 = new TypeMappingInfo();
	        tmi3.setXmlTagName(new QName("","testTagname3"));		
	        tmi3.setElementScope(ElementScope.Global);		
	        tmi3.setType(getClass().getField("testStringField").getGenericType());	        
	        typeMappingInfos[2] = tmi3;	  
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
			    
	    byte[] bytes3 = new byte[]{0,1,2,3};
        byte[] bytes4 = new byte[]{1,2,3,4};
					
		List<byte[]> bytesList = new ArrayList<byte[]>();
		bytesList.add(bytes3);
		bytesList.add(bytes4);
		
		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(bytesList);

		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/genericArrayType.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

}
