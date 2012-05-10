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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;

public class InheritanceRootCompositeCollectionObjectOnlyTestCases extends JAXBWithJSONTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollectionobject.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollectionobject.json";

    public InheritanceRootCompositeCollectionObjectOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {RootCompositeCollectionObjectOnly.class, BaseType.class});  
    }

    protected Object getControlObject() {
    	RootCompositeCollectionObjectOnly root = new RootCompositeCollectionObjectOnly();
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
		
		List objectList = new ArrayList(baseTypes);
		objectList.add(new String("string test"));
		objectList.add(new Integer(500));
		root.objectList = objectList;
				
		return root;
    }

}