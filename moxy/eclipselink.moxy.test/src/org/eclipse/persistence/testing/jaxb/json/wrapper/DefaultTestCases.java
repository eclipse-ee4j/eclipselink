/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class DefaultTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String JSON = "org/eclipse/persistence/testing/jaxb/json/wrapper/WrapperNameDefault.json";

    public DefaultTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON);
        setClasses(new Class[] {Company.class});
    }

    @Override
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
        Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        return properties;
    }

    public void tesMarshallerProperty() throws Exception {
        assertFalse((Boolean) jsonMarshaller.getProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME));
    }

    public void testUnmarshallerProperty() throws Exception {
        assertFalse((Boolean) jsonUnmarshaller.getProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME));
    }

}
