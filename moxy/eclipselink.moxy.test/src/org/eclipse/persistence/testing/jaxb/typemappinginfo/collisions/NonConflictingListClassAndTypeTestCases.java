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
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class NonConflictingListClassAndTypeTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/nonConflictingLists.xml";
    	
	public List<String> testField;
	
	public NonConflictingListClassAndTypeTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[2];
	        
	    	TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("","testTagName1"));		
	        tmi.setElementScope(ElementScope.Global);		
	        tmi.setType(getClass().getField("testField").getGenericType());	        
	        typeMappingInfos[0] = tmi;	        
	        
	        TypeMappingInfo tmi2 = new TypeMappingInfo();
	        tmi2.setXmlTagName(new QName("","testTagName2"));		
	        tmi2.setElementScope(ElementScope.Global);		
	        tmi2.setType(List.class);
	        typeMappingInfos[1] = tmi2;
	    }
		return typeMappingInfos;		
	}

	
	protected Object getControlObject() {
		QName qname = new QName("", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		List<String> theList = new ArrayList<String>();
		theList.add("one");
		theList.add("two");
		theList.add("three");
		jaxbElement.setValue(theList);
		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/nonConflictingLists.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
			
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	
	public void testDescriptorsSize(){
		Vector descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();
		assertEquals(2, descriptors.size());
	}
}
