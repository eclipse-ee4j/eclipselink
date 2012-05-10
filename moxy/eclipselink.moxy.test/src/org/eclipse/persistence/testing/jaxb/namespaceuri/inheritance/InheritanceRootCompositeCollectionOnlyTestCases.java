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
 *     Oracle - December 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;


public class InheritanceRootCompositeCollectionOnlyTestCases extends JAXBWithJSONTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollection.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollection.json";
    private static final String  JSON_RESOURCE_NO_NS = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollection_nons.json";

    public InheritanceRootCompositeCollectionOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {RootCompositeCollectionOnly.class});
        
    	Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("uri1", "ns5");
    	namespaces.put("rootNamespace", "ns0");
    	namespaces.put("someNamespaceLevel2", "ns3");
    	namespaces.put("anotherNamespace", "ns2");
    	namespaces.put("uri3", "ns4");
    	namespaces.put("someNamespace", "ns1");
    	namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL, "xsi");
    	jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected JAXBMarshaller getJSONMarshaller() throws Exception{
    	JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
    	jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    	Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("uri1", "ns5");
    	namespaces.put("rootNamespace", "ns0");
    	namespaces.put("someNamespaceLevel2", "ns3");
    	namespaces.put("anotherNamespace", "ns2");
    	namespaces.put("uri3", "ns4");
    	namespaces.put("someNamespace", "ns1");
    	namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL, "xsi");
    	
    	jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    	return jsonMarshaller;
    }
    
    protected Object getControlObject() {
    	RootCompositeCollectionOnly root = new RootCompositeCollectionOnly();
		SubType subType = new SubType();
		subType.subTypeProp = 10;
		
		SubTypeLevel2 subTypeLevel2 = new SubTypeLevel2();
		subTypeLevel2.baseProp = "boo";
		
		AnotherSubType anotherSubType = new AnotherSubType();
		AnotherPackageSubType anotherPackageSubType = new AnotherPackageSubType();
		List baseTypes = new ArrayList();
		baseTypes.add(subType);
		baseTypes.add(anotherSubType);
		baseTypes.add(subTypeLevel2);
		baseTypes.add(subType);
		baseTypes.add(anotherPackageSubType);
		root.baseTypeList = baseTypes;
				
		return root;
    }    
    
	public void testJSONNoNamespacesSet() throws Exception {
		JAXBMarshaller m = (JAXBMarshaller) jaxbContext.createMarshaller();
		JAXBUnmarshaller u = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();
		m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		StringWriter sw = new StringWriter();
		
		m.marshal(getWriteControlObject(), sw);
        compareStrings("**testJSONMarshalToStringWriter-NoNamespacesSet**", sw.toString(), JSON_RESOURCE_NO_NS);

        StringReader sr = new StringReader(sw.toString());
        Object o = u.unmarshal(sr);
        assertEquals(getReadControlObject(), o);
	}

}