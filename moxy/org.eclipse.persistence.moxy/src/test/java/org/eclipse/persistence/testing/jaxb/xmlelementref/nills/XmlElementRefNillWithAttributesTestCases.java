/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import java.io.File;
import java.util.ArrayList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
