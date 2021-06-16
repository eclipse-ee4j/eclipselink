/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

import junit.framework.TestCase;

public class WrapperXmlPathTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String JSON = "org/eclipse/persistence/testing/jaxb/json/wrapper/WrapperName.json";

    public WrapperXmlPathTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XmlPathCompany.class});
        setControlJSON(JSON);
    }

    public JAXBElement<XmlPathCompany> getControlObject() {
        XmlPathCompany company = new XmlPathCompany();
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

        XmlPathEmployee employee1 = new XmlPathEmployee();
        employee1.phoneNumbers.add(pnA);
        employee1.phoneNumbers.add(pnB);
        company.employees.add(employee1);

        XmlPathEmployee employee2 = new XmlPathEmployee();
        employee2.phoneNumbers.add(pnB);
        employee2.phoneNumbers.add(pnC);
        company.employees.add(employee2);

        return new JAXBElement<XmlPathCompany>(new QName(""), XmlPathCompany.class, company);
    }

    @Override
    public Class getUnmarshalClass() {
        return XmlPathCompany.class;
    }

    @Override
    public Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(3);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return properties;
    }

    public void tesMarshallerProperty() throws Exception {
        assertTrue((Boolean) jsonMarshaller.getProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME));
    }

    public void testUnmarshallerProperty() throws Exception {
        assertTrue((Boolean) jsonUnmarshaller.getProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME));
    }

}
