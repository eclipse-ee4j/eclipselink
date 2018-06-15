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
//     Denise Smith - May 2012
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBInheritanceSubTypeNoParentRootTestCases extends JAXBWithJSONTestCases {
    public JAXBInheritanceSubTypeNoParentRootTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {SubTypeWithRootElementNoParentRoot.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/ns/subTypeNoParentRoot.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/ns/subTypeNoParentRoot.json");

        Map<String, String> namespaces= new HashMap<String, String>();
        namespaces.put("rootNamespace","ns0");
        namespaces.put("someNamespace","ns1");
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,"xsi");

        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public JAXBMarshaller getJSONMarshaller() throws Exception{
        Map<String, String> namespaces= new HashMap<String, String>();
        namespaces.put("rootNamespace","ns0");
        namespaces.put("someNamespace","ns1");
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,"xsi");

        JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        return jsonMarshaller;
    }

    public Object getControlObject() {
        SubTypeWithRootElementNoParentRoot subType = new SubTypeWithRootElementNoParentRoot();
        return subType;
    }

}
