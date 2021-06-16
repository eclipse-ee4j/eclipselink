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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement;

import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

import org.w3c.dom.Document;


public class DirectCollectionWithoutGroupingElementIdentifiedByNameNullItemTestCases extends XMLMappingTestCases
{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementNull.xml";
    private final static String XML_WRITE_CONTROL_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementNullItemWriteControl.xml";

  private final static int CONTROL_ID = 123;
  private final static String CONTROL_RESPONSIBILITY1 = "do the dishes";


    public DirectCollectionWithoutGroupingElementIdentifiedByNameNullItemTestCases(String name) throws Exception
    {
        super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new DirectCollectionWithoutGroupingElementIdentifiedByNameProject());
    }

    protected Object getControlObject() {
        Vector responsibilities = new Vector();

        responsibilities.addElement(null);
        responsibilities.addElement(CONTROL_RESPONSIBILITY1);
        responsibilities.addElement(null);

    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
    return employee;
  }

    public Document getWriteControlDocument() throws Exception{
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_WRITE_CONTROL_RESOURCE);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document returnDoc = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        return returnDoc;
    }

    /*
     * On the read the null collection will come in as empty
     */
    public Object getReadControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
        Vector responsibilities = new Vector();
        responsibilities.addElement(CONTROL_RESPONSIBILITY1);
        employee.setResponsibilities(responsibilities);

    return employee;
    }

    public void testObjectToContentHandler() throws Exception {
        // DO NOTHING BECAUSE CONTENT HANDLER CAN NOT READ COMMENTS
    }
}
