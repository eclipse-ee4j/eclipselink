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
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class ConflictingMapsTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingMaps.xml";
    	
	public Map<Object, Object> testField1;
	public Map<Object, String> testField2;
	public Map<String, Object> testField3;
	public Map<Integer, Integer> testField4;
	public Map<Integer, Integer> testField5;
	
	public ConflictingMapsTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[7];
	        
	    	TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("","testTagName1"));		
	        tmi.setElementScope(ElementScope.Global);		
	        tmi.setType(getClass().getField("testField1").getGenericType());	        
	        typeMappingInfos[0] = tmi;	        
	        
	        TypeMappingInfo tmi2 = new TypeMappingInfo();
	        tmi2.setXmlTagName(new QName("","testTagName2"));		
	        tmi2.setElementScope(ElementScope.Global);		
	        tmi2.setType(getClass().getField("testField2").getGenericType());
	        typeMappingInfos[1] = tmi2;
	        
	        TypeMappingInfo tmi3 = new TypeMappingInfo();
	        tmi3.setXmlTagName(new QName("","testTagName3"));		
	        tmi3.setElementScope(ElementScope.Global);		
	        tmi3.setType(getClass().getField("testField3").getGenericType());
	        typeMappingInfos[2] = tmi3;
	        
	        TypeMappingInfo tmi4 = new TypeMappingInfo();
	        tmi4.setXmlTagName(new QName("","testTagName4"));		
	        tmi4.setElementScope(ElementScope.Global);		
	        tmi4.setType(getClass().getField("testField4").getGenericType());
	        typeMappingInfos[3] = tmi4;
	        
	        TypeMappingInfo tmi5 = new TypeMappingInfo();
	        tmi5.setXmlTagName(new QName("","testTagName5"));		
	        tmi5.setElementScope(ElementScope.Global);		
	        tmi5.setType(getClass().getField("testField4").getGenericType());
	        typeMappingInfos[4] = tmi5;
	        
	        TypeMappingInfo tmi6 = new TypeMappingInfo();
	        tmi6.setXmlTagName(new QName("","testTagName6"));		
	        tmi6.setElementScope(ElementScope.Global);		
	        tmi6.setType(Map.class);
	        typeMappingInfos[5] = tmi6;
	        	        
	        TypeMappingInfo tmi7 = new TypeMappingInfo();
	        tmi7.setXmlTagName(new QName("","testTagName7"));		
	        tmi7.setElementScope(ElementScope.Global);		
	        tmi7.setType(HashMap.class);
	        typeMappingInfos[6] = tmi7;
	    }
		return typeMappingInfos;		
	}

	
	protected Object getControlObject() {
		
		Map<Object, Object> theMap = new HashMap<Object, Object>();
		
		QName qname = new QName("", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		theMap.put("abc", 123);
		jaxbElement.setValue(theMap);

		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingMaps.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
			
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	
	public void testDescriptorsSize(){
		List descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();
		assertEquals(10, descriptors.size());
	}
}
