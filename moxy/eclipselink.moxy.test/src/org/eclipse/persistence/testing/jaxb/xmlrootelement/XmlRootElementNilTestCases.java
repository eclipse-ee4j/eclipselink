/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - April 8th/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlRootElementNilTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil_write.json";

    public XmlRootElementNilTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);    
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNoNamespace.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeNoNamespace employee = new EmployeeNoNamespace();
        return employee;
    }
}
