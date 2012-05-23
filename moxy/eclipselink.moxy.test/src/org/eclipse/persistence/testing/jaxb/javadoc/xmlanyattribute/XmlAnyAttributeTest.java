/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyattribute;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyAttributeTest extends JAXBWithJSONTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyattribute/xmlanyattribute.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlanyattribute/xmlanyattribute.json";
	
	public XmlAnyAttributeTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = XmlAnyAttributeModel.class;
		setClasses(classes);
		
		jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("www.example.com","ns0");
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
	}

	public JAXBMarshaller getJSONMarshaller() throws Exception{
		JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
		jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		jsonMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("www.example.com","ns0");
		
		jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
		return jsonMarshaller;
	}
	
	protected Object getControlObject() {

		XmlAnyAttributeModel model = new XmlAnyAttributeModel();
		model.setTitle("A mixture of elements");

		HashMap<QName,Object> amap = new HashMap<QName,Object>();
		amap.put(new QName("www.example.com", "any"), "blah");
		model.any = amap;
        return model;
	}


}
