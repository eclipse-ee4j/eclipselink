/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

public class JAXBInheritanceSubTypeTestCases extends JAXBWithJSONTestCases {
    public JAXBInheritanceSubTypeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {SubTypeWithRootElement.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/ns/subTypeRoot.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/ns/subTypeRoot.json");

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
        SubTypeWithRootElement subType = new SubTypeWithRootElement();
        return subType;
    }
}
