/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - February 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ChoiceCollectionTestCases extends JAXBWithJSONTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/choicecollection.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/choicecollection.json";
	  
	public ChoiceCollectionTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[]{Wrapper.class};
		setClasses(classes);
		jaxbUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
		
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(XMLConstants.XOP_URL, XMLConstants.XOP_PREFIX);
		jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
		jaxbMarshaller.setProperty(JAXBMarshaller.NAMESPACE_PREFIX_MAPPER, namespaces);
		jaxbMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
	}

	@Override
	protected Object getControlObject() {
		Wrapper wrapper = new Wrapper();
		List theList = new ArrayList();
		JAXBElement jbe = new JAXBElement<DataHandler>(new QName("return"), DataHandler.class, MyAttachmentUnmarshaller.theDataHandler);
		theList.add(jbe);
		JAXBElement jbe2 = new JAXBElement<DataHandler>(new QName("return"), DataHandler.class, MyAttachmentUnmarshaller.theDataHandler);
		theList.add(jbe2);
		wrapper.content = theList;
		return wrapper;
	}
	
	

}
