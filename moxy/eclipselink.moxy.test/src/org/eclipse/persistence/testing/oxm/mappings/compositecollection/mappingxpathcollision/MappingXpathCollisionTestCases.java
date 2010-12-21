/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.mappingxpathcollision;

import java.io.InputStream;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmployeeWithUserID;
import org.w3c.dom.Document;

public class MappingXpathCollisionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbyname/EmployeeWithUserID.xml";

    public MappingXpathCollisionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new MappingXpathCollisionProject());
    }

    protected Object getControlObject() {
        EmployeeWithUserID employee = new EmployeeWithUserID();
        employee.setUserID("empUserID");

        employee.setName("empName");
        return employee;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.mappingxpathcollision.MappingXpathCollisionTestCases" };
        junit.textui.TestRunner.main(arguments);
    }

    protected Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbyname/EmployeeWithUserIDWrite.xml");
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        inputStream.close();
        return doc;
    }
}
