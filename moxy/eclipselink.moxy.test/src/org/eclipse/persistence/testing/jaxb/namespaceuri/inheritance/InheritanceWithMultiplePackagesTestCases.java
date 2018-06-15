/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - December 2011
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;

public class InheritanceWithMultiplePackagesTestCases extends JAXBWithJSONTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/complex.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/complex.json";

    public InheritanceWithMultiplePackagesTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {RootComplex.class});
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("rootNamespace", "ns0");
        namespaces.put("someNamespace", "ns1");
        namespaces.put("anotherNamespace","ns2");
        namespaces.put("someNamespaceLevel2", "ns3");
        namespaces.put("uri1", "ns5");
        namespaces.put("uri3", "ns6");
        return namespaces;
    }

    protected Object getControlObject() {
        RootComplex root = new RootComplex();
        SubType subType = new SubType();
        subType.subTypeProp = 10;
        root.baseTypeThing = subType;

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

        List objectList = new ArrayList(baseTypes);
        objectList.add(new String("string test"));
        objectList.add(new Integer(500));
        root.objectList = objectList;

        List anyObjectList = new ArrayList(baseTypes);
        anyObjectList.add(new String("string test2"));

        List choiceList = new ArrayList();
        choiceList.add(anotherPackageSubType);
        choiceList.add(subTypeLevel2);
        choiceList.add(new String("choice string test"));
        choiceList.add(new Integer(500));
        root.choiceList = choiceList;
        return root;
    }

}
