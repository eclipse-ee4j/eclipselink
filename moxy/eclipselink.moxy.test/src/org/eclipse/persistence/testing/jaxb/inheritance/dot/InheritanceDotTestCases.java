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
//     Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.inheritance.dot;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InheritanceDotTestCases extends JAXBWithJSONTestCases{

    public InheritanceDotTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Person.class, Employee.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/inheritanceDot.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/inheritanceDot.json");
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Bob Smith";
        emp.badgeNumber = "123";
        return emp;
    }

}
