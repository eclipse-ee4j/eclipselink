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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;

public class InheritanceRootCompositeCollectionObjectOnlyNSTestCases extends JAXBWithJSONTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollectionobjectNS.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/compositecollectionobjectNS.json";

    public InheritanceRootCompositeCollectionObjectOnlyNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {RootCompositeCollectionObjectOnly.class, BaseType.class});
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "xsd");
        namespaces.put("rootNamespace", "ns0");
        namespaces.put("someNamespace", "ns1");
        namespaces.put("anotherNamespace", "ns2");
        namespaces.put("someNamespaceLevel2", "ns3");
        namespaces.put("uri1", "ns5");
        namespaces.put("uri3", "ns6");
        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_USE_XSD_TYPES_WITH_PREFIX, true);
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
