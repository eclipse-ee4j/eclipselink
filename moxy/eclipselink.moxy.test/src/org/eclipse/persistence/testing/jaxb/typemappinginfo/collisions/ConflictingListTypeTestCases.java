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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.Employee;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;
import org.w3c.dom.Element;

public class ConflictingListTypeTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingListTypes.xml";
    	
	public List<Employee> testField;
	public List<String> testSimpleField;
	
	@XmlList
	public Object xmlListAnnotationField;
	
	public ConflictingListTypeTestCases(String name) throws Exception {
		super(name);
		setupParser();
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[6];
	        
	    	TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("someUri","testTagName1"));		
	        tmi.setElementScope(ElementScope.Global);		
	        tmi.setType(getClass().getField("testField").getGenericType());	        
	        typeMappingInfos[0] = tmi;	        
	        
	        TypeMappingInfo tmi2 = new TypeMappingInfo();
	        tmi2.setXmlTagName(new QName("someUri","testTagName2"));		
	        tmi2.setElementScope(ElementScope.Global);		
	        tmi2.setType(getClass().getField("testField").getGenericType());
	        typeMappingInfos[1] = tmi2;
	        
	        TypeMappingInfo tmi3 = new TypeMappingInfo();
	        tmi3.setXmlTagName(new QName("someUri","testTagName3"));		
	        tmi3.setElementScope(ElementScope.Global);		
	        tmi3.setType(List.class);
	        Element xmlElement = getXmlElement("<xml-element type='org.eclipse.persistence.testing.jaxb.typemappinginfo.Employee' />");	        
	        tmi3.setXmlElement(xmlElement);
	        typeMappingInfos[2] = tmi3;
	        
	        TypeMappingInfo tmi4 = new TypeMappingInfo();
	        tmi4.setXmlTagName(new QName("someUri","testTagName4"));		
	        tmi4.setElementScope(ElementScope.Global);		
	        tmi4.setType(getClass().getField("testSimpleField").getGenericType());
	        typeMappingInfos[3] = tmi4;
	        
	        TypeMappingInfo tmi5 = new TypeMappingInfo();
	        tmi5.setXmlTagName(new QName("someUri","testTagName5"));		
	        tmi5.setElementScope(ElementScope.Global);		
	        tmi5.setType(getClass().getField("testSimpleField").getGenericType());
	        Element xmlElement2 = getXmlElement("<xml-element xml-list='true'/>");	        
	        tmi5.setXmlElement(xmlElement2);	        	        
	        typeMappingInfos[4] = tmi5;
	        
	        TypeMappingInfo tmi6 = new TypeMappingInfo();
	        tmi6.setXmlTagName(new QName("someUri","testTagName6"));		
	        tmi6.setElementScope(ElementScope.Global);		
	        tmi6.setType(getClass().getField("testSimpleField").getGenericType());
	        Annotation[] annotations = new Annotation[1];	        
	        annotations[0] = getClass().getField("xmlListAnnotationField").getAnnotations()[0];
	        tmi6.setAnnotations(annotations);	        	        
	        typeMappingInfos[5] = tmi6;
	    }
		return typeMappingInfos;		
	}
	
	protected Object getControlObject() {
		
		List<Employee> emps = new ArrayList<Employee>();
		
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		Employee emp = new Employee();
		emp.firstName ="theFirstName";
		emp.lastName = "theLastName";
		
		emps.add(emp);
		
		jaxbElement.setValue(emps);

		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingListTypes.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		
		InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingListTypes2.xsd");
		controlSchema.put("", instream2);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

	public void testDescriptorsSize(){
		Vector descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();
		assertEquals(4, descriptors.size());
	}

}
