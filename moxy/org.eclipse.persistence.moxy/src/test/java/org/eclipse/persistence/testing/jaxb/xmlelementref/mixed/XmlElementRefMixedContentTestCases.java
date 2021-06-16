/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - May 31, 2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.mixed;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementRefMixedContentTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/mixed/mixed.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/mixed/mixed.json";

    public XmlElementRefMixedContentTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Employee.class;
        classes[1] = Task.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.tasks = new ArrayList<Object>();
        emp.tasks.add("This is Mixed Content");
        Task task = new Task();
        task.theTask = "Task 1";
        emp.tasks.add(task);
        task = new Task();
        task.theTask = "Task 2";
        emp.tasks.add(task);

        return emp;
     }

    protected Object getJSONReadControlObject() {
        Employee emp = new Employee();
        emp.tasks = new ArrayList<Object>();
        Task task = new Task();
        task.theTask = "Task 1";
        emp.tasks.add(task);
        task = new Task();
        task.theTask = "Task 2";
        emp.tasks.add(task);
        emp.tasks.add("This is Mixed Content");
        return emp;
     }
}
