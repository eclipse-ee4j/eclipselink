/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PrefixMapperMapTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/prefixmapper/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/prefixmapper/employee.json";
    public PrefixMapperMapTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Employee.class, Person.class});
        Map<String, String> map = new HashMap<String, String>();

        map.put("someuri", "newPrefix");
        map.put("extraUri", "ns0");
        map.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
        map.put("my.uri", "somePrefix");

        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, map);

        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, map);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "John";
        emp.lastName = "Doe";
        emp.address = "123 Fake Street";
        emp.employeeId = 4321;

        emp.manager = new Employee();
        emp.manager.firstName = "Bob";
        emp.manager.lastName = "Jones";
        emp.manager.address = "234 Some Street";
        ((Employee)emp.manager).employeeId = 9876;
        return emp;
    }

}
