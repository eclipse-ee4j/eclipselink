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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

public class JAXBListNameCollisionEmployeeTestCases extends
JAXBListOfObjectsNoJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeListNameCollision.xml";
    protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeListNameCollisionNoXsiType.xml";

    protected final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
    protected final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
    protected final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
    protected final static String CONTROL_FIRST_NAME = "Bob";
    protected final static String CONTROL_LAST_NAME = "Smith";
    protected final static int CONTROL_ID = 10;

    public JAXBListNameCollisionEmployeeTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[2];

        Field fld = ListofObjects.class.getField("empList");
        Type[] types = new Type[2];
        types[0] = fld.getGenericType();
        types[1] = org.eclipse.persistence.testing.jaxb.employee.Employee[].class;
        setTypes(types);
    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
        employee.firstName = CONTROL_FIRST_NAME;
        employee.lastName = CONTROL_LAST_NAME;

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2005, 04, 24, 16, 06, 53);

        employee.id = CONTROL_ID;

        employee.responsibilities = responsibilities;

        employee.setBlah("Some String");

        Employee employee2 = new Employee();
        employee2.firstName = CONTROL_FIRST_NAME + "2";
        employee2.lastName = CONTROL_LAST_NAME + "2";
        employee2.setBlah("Some Other String");
        employee2.id = 100;

        ArrayList responsibilities2 = new ArrayList();
        responsibilities2.add(CONTROL_RESPONSIBILITY1);
        employee2.responsibilities = responsibilities2;

        List<Employee> emps = new ArrayList<Employee>();
        emps.add(employee);
        emps.add(employee2);

        QName qname = new QName("rootNamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(emps);

        return jaxbElement;
    }

    public List< InputStream> getControlSchemaFiles(){
        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/employeeCollision2.xsd");
        InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/employeeCollision1.xsd");
        List<InputStream> controlSchema= new ArrayList<InputStream>();

        controlSchema.add(instream1);
        controlSchema.add(instream2);

        return controlSchema;
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = ListofObjects.class.getField("empList");
        return fld.getGenericType();
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }

    public Map getProperties() {
        Map props = new HashMap();

        // test override of 'old' context factory property - if the override
        // fails we will get errors
        props.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, "listOfObjectsNamespaceWRONG");
        props.put(JAXBContextProperties.DEFAULT_TARGET_NAMESPACE, "listOfObjectsNamespace");
        return props;
    }
}
