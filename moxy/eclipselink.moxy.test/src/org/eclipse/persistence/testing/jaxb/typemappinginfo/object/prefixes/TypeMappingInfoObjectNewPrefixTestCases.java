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
 *    2.3.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.object.prefixes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class TypeMappingInfoObjectNewPrefixTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/object/employeeNewPrefixes.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/object/employeeNewPrefixes.json";
	
	public TypeMappingInfoObjectNewPrefixTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		
		setTypeMappingInfos(getTypeMappingInfos());
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("differentURI", "ns0");
		namespaces.put("someuri", "ns1");
		namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL, "xsi");
		jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
		
	}

	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	
	    	typeMappingInfos = new TypeMappingInfo[2];

	        TypeMappingInfo tpi = new TypeMappingInfo();
	        tpi.setXmlTagName(new QName("differentURI","response"));       
	        tpi.setElementScope(ElementScope.Global);
	        tpi.setType(Object.class);
	        typeMappingInfos[0] = tpi;        

	        TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setType(Employee.class);
	        tmi.setElementScope(ElementScope.Global);
	        typeMappingInfos[1] = tmi;
	    	
	    	
	    }
		return typeMappingInfos;		
	}
	

   protected Object getControlObject() {
		
		QName qname = new QName("differentURI","response");
		
		Employee emp = new Employee();
        emp.id = "123";
        emp.name="aaa";

        JAXBElement<Object> elem = new JAXBElement<Object>(qname, Object.class, emp);
		return elem;
	}

   public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/object/employeeNewPrefixes.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someuri", instream);
		InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/object/employeeNewPrefixes2.xsd");
		controlSchema.put("differentURI", instream2);
		return controlSchema;
	}


}
