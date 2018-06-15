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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlRootElementNamespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_namespace.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_namespace.json";
    private final static int CONTROL_ID = 10;
    private Marshaller jsonMarshaller;
    private Unmarshaller jsonUnmarshaller;

    public XmlRootElementNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNamespace.class;
        setClasses(classes);

        jsonMarshaller = jaxbContext.createMarshaller();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("my/namespace/is/cool", "ns0");
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);

        jsonUnmarshaller = jaxbContext.createUnmarshaller();
           jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected Marshaller getJSONMarshaller() throws Exception{
        return jsonMarshaller;
    }

   protected Unmarshaller getJSONUnmarshaller() throws Exception{
       return jsonUnmarshaller;
    }

    protected Object getControlObject() {
        EmployeeNamespace employee = new EmployeeNamespace();
        employee.id = CONTROL_ID;
        return employee;
    }
}
