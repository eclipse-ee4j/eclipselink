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
 *     Denise Smith  January 12, 2009 - 2.0.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.w3c.dom.Document;

public class EmployeeAdapterTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/employeeAdapter.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/employeeAdapter.json";
	
	 @XmlJavaTypeAdapter(EmployeeToStringAdapter.class)
	  public Object javaTypeAdapterField;
	
	public EmployeeAdapterTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
	}
		
	public void setUp() throws Exception{
		super.setUp();
		setTypeMappingInfos(getTypeMappingInfos());		
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[1];
	        TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("someUri","testTagName"));		
	        tmi.setElementScope(ElementScope.Global);
	        tmi.setNillable(true);
	        tmi.setType(Employee.class);
	        
	        Annotation[] annotations = new Annotation[1];
	        annotations[0] = getClass().getField("javaTypeAdapterField").getAnnotations()[0];
            tmi.setAnnotations(annotations);
            
	        typeMappingInfos[0] = tmi;	        
	    }
		return typeMappingInfos;		
	}

	
	protected Object getControlObject() {
		
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Employee.class, null);
		Employee emp = new Employee();
		emp.firstName ="theFirstName";
		
		jaxbElement.setValue(emp);

		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/employeeAdapter.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	

}
