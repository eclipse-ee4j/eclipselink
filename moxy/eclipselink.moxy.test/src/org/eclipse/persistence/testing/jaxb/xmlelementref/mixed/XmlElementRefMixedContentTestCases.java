/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - May 31, 2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.mixed;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.ComplexType;
import org.eclipse.persistence.testing.jaxb.xmlelementref.ComplexTypeObjectFactory;

public class XmlElementRefMixedContentTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/mixed/mixed.xml";

    public XmlElementRefMixedContentTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
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
}
