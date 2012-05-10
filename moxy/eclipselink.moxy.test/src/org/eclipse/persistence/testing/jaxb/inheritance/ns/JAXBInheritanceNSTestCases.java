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
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBInheritanceNSTestCases extends JAXBWithJSONTestCases {
	public JAXBInheritanceNSTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] {Root.class, SubType.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNS.xml");
		setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNS.json");
		
		Map<String, String> namespaces= new HashMap<String, String>();
		namespaces.put("rootNamespace","ns0");
		namespaces.put("someNamespace","ns1");
		namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL,"xsi");
		
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
	}
	public JAXBMarshaller getJSONMarshaller() throws Exception{
		Map<String, String> namespaces= new HashMap<String, String>();
		namespaces.put("rootNamespace","ns0");
		namespaces.put("someNamespace","ns1");
		namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL,"xsi");
		
		JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
		jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
		jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		return jsonMarshaller;
	}
	
	public Object getControlObject() {
		Root root = new Root();
		SubType subType = new SubType();	
		root.baseTypeThing = subType;
		return root;
	}
	
	public void testJSONNoNamespacesSet() throws Exception {
		String controlFile = "org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNSNoNamespaces.json";
		JAXBMarshaller m = (JAXBMarshaller) jaxbContext.createMarshaller();
		JAXBUnmarshaller u = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();
		m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		StringWriter sw = new StringWriter();
		
		m.marshal(getWriteControlObject(), sw);
        compareStrings("**testJSONMarshalToStringWriter-NoNamespacesSet**", sw.toString(), controlFile, shouldRemoveEmptyTextNodesFromControlDoc());

        StringReader sr = new StringReader(sw.toString());
        Object o = u.unmarshal(sr);
        assertEquals(getReadControlObject(), o);
	}
}
