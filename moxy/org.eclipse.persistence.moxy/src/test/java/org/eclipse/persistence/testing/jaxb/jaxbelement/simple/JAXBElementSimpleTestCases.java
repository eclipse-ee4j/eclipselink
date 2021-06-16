/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;
import org.w3c.dom.Document;

public class JAXBElementSimpleTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/employee.json";
    private final static String CONTROL_ELEMENT_NAME = "employee-name";
    private final static Class targetClass = String.class;

    public JAXBElementSimpleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTargetClass(targetClass);
    }


    public Class getUnmarshalClass(){
        return targetClass;
    }


    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {
        return new JAXBElement(new QName(CONTROL_NAMESPACE_URI, CONTROL_ELEMENT_NAME), targetClass, CONTROL_PERSON_NAME);
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbelement/simple/employee-write.xml");
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeControlDocument;
    }
}
