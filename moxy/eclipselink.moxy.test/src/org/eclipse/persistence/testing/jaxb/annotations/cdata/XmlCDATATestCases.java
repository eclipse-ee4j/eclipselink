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
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.cdata;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;


public class XmlCDATATestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlcdata/employee.xml";
    public XmlCDATATestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument(XML_RESOURCE);
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.xmlData = "<root><child>A string wrapped in cdata</child></root>";
        return emp;
    }
    @Override
    public void testObjectToContentHandler() {
        //CDATA sections don't work with content handlers
    }
}
