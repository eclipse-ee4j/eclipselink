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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;


public class InheritanceRootChoiceOnlyTestCases extends JAXBWithJSONTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/choicecollection.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/choicecollection.json";

    public InheritanceRootChoiceOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {RootChoiceOnly.class});
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("rootNamespace","ns0");
        namespaces.put("uri1", "ns5");

        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected Object getControlObject() {
    	RootChoiceOnly root = new RootChoiceOnly();
		SubType subType = new SubType();
		subType.subTypeProp = 10;
		
		SubTypeLevel2 subTypeLevel2 = new SubTypeLevel2();
		subTypeLevel2.baseProp = "boo";
		
		AnotherPackageSubType anotherPackageSubType = new AnotherPackageSubType();
		
		List choiceList = new ArrayList();
		choiceList.add(new String("choice string test2"));
		choiceList.add(anotherPackageSubType);
		choiceList.add(new Integer(400));
		choiceList.add(subTypeLevel2);
		choiceList.add(new String("choice string test"));
		choiceList.add(new Integer(500));
		root.choiceList = choiceList;
		return root;
    }
}