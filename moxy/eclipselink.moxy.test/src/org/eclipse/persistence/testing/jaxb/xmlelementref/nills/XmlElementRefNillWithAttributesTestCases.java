/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.Employee.Task;


public class XmlElementRefNillWithAttributesTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-nill.xml";

    public XmlElementRefNillWithAttributesTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[] {ObjectFactory.class, Employee.class};
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        Employee e = new Employee();
        Address a = new Address();
        a.city = "Prague";
        e.address = new ObjectFactory().createAddress(a);

        e.tasks = new ArrayList<JAXBElement<Task>>(2);
        Task t = new Task();
        t.id = 123;
        e.tasks.add(new ObjectFactory().createTask(t));
        t = new Task();
        t.id = 321;
        e.tasks.add(new ObjectFactory().createTask(t));

        return e;
    }

}
