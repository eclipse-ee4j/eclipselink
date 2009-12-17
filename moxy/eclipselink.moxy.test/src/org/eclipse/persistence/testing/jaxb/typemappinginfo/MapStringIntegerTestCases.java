/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - December 16, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentUnmarshaller;

public class MapStringIntegerTestCases extends TypeMappingInfoTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/stringIntegerHashMap.xml";
	
	public static Map<String, Integer> myMap = new HashMap<String, Integer>();
	
	public MapStringIntegerTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setTypeMappingInfos(getTypeMappingInfos());		
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[1];	    	     
	        typeMappingInfos[0] = getTypeMappingInfo();	        
	    }
		return typeMappingInfos;		
	}
	
	protected TypeMappingInfo getTypeMappingInfo()throws Exception {
		
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setXmlTagName(new QName("","root"));		
		tmi.setElementScope(ElementScope.Local);
		Type t = getClass().getField("myMap").getGenericType();
		tmi.setType(t);		
				
		return tmi;
	}
	
	public Object getReadControlObject() {
		QName qname = new QName("", "root");
		JAXBElement jbe = new JAXBElement(qname, Object.class, null);
		jbe.setValue(getControlObject());
	    return jbe;
	}

	protected Object getControlObject() {
		HashMap<String, Integer> theMap = new HashMap<String, Integer>();
		theMap.put("thekey", new Integer(10));
				
		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(theMap);

		return theMap;
	}

	public Map<String, InputStream> getControlSchemaFiles() {
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/stringIntegerHashMap.xsd");
			
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
		return controlSchema;
	}
	

}
