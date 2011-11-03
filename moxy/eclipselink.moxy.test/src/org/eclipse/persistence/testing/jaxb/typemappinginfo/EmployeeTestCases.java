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
 *     Denise Smith  January 12, 2009 - 2.0.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.w3c.dom.Document;

public class EmployeeTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/employee.xml";
    
	
	public EmployeeTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
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
	        typeMappingInfos[0] = tmi;	        
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
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/employee.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	

	
	  protected Map<String, Object> getProperties() throws Exception{
	        String pkg = "someUri";
		        
	        HashMap<String, Source> overrides = new HashMap<String, Source>();
	        overrides.put("org.eclipse.persistence.testing.jaxb.typemappinginfo", getXmlSchemaOxm(pkg));
	        Map<String, Object> properties = new HashMap<String, Object>();
	        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return properties;
	    }
		    
	    private Source getXmlSchemaOxm(String defaultTns) throws Exception {
	        String oxm = 
	        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
		        "<xml-schema namespace='" + defaultTns + "'/>" + 
		        "<java-types>" +
		        "<java-type name='org.eclipse.persistence.testing.jaxb.typemappinginfo.Employee'>" +
		        "</java-type>" +
		        "</java-types>" + 
	        "</xml-bindings>";
		    Document doc = parser.parse(new ByteArrayInputStream(oxm.getBytes()));        
		    return new DOMSource(doc.getDocumentElement());
		}
}
