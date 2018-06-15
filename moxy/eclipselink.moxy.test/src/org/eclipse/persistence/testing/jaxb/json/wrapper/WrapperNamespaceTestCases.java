/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

import junit.framework.TestCase;

public class WrapperNamespaceTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String JSON = "org/eclipse/persistence/testing/jaxb/json/wrapper/WrapperNamespaces.json";

    public WrapperNamespaceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Company.class});
        setControlJSON(JSON);
    }

    public JAXBElement<Company> getControlObject() {
        Company company = new Company();
        company.strings.add("FOO");
        company.strings.add("BAR");

        PhoneNumber pnA = new PhoneNumber();
        pnA.id = "A";
        company.phoneNumbers.add(pnA);

        PhoneNumber pnB = new PhoneNumber();
        pnB.id = "B";
        company.phoneNumbers.add(pnB);

        PhoneNumber pnC = new PhoneNumber();
        pnC.id = "C";
        company.phoneNumbers.add(pnC);

        Employee employee1 = new Employee();
        employee1.phoneNumbers.add(pnA);
        employee1.phoneNumbers.add(pnB);
        company.employees.add(employee1);

        Employee employee2 = new Employee();
        employee2.phoneNumbers.add(pnB);
        employee2.phoneNumbers.add(pnC);
        company.employees.add(employee2);

        return new JAXBElement<Company>(new QName(""), Company.class, company);
    }

    @Override
    public Class getUnmarshalClass() {
        return Company.class;
    }

    @Override
    public Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(3);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return properties;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> namespaceMapper = new HashMap<String, String>(2);
        namespaceMapper.put("urn:FOO", "foo");
        namespaceMapper.put("urn:BAR", "bar");
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaceMapper);
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaceMapper);
    }
}
