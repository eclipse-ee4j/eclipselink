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
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.Employee;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class ConflictingClassesTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClasses.xml";
	protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClassesNoXsiType.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClasses.json";
	protected final static String JSON_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClassesNoXsiType.json";
	
	public ConflictingClassesTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setWriteControlDocument(XML_RESOURCE_NO_XSI_TYPE);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_RESOURCE_NO_XSI_TYPE);
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[2];
	        
	    	TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("someUri","testTagName1"));		
	        tmi.setElementScope(ElementScope.Global);		
	        tmi.setType(Employee.class);	        
	        typeMappingInfos[0] = tmi;	        
	        
	        TypeMappingInfo tmi2 = new TypeMappingInfo();
	        tmi2.setXmlTagName(new QName("someUri","testTagName2"));		
	        tmi2.setElementScope(ElementScope.Global);		
	        tmi2.setType(Employee.class);	        
	        typeMappingInfos[1] = tmi2;
	    }
		return typeMappingInfos;		
	}

	
	protected Object getControlObject() {
		
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Employee.class, null);
		Employee emp = new Employee();
		emp.firstName ="theFirstName";
		emp.lastName = "theLastName";
		
		jaxbElement.setValue(emp);

		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClasses.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	
}
