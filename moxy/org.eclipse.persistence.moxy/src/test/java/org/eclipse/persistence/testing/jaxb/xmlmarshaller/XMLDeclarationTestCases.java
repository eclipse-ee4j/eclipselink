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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLDeclarationTestCases extends MarshallerFragmentTestCases{

    public XMLDeclarationTestCases(String name) {
        super(name);
    }

     public void setUp() throws Exception {
            //set up XMLMarshaller
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder parser = builderFactory.newDocumentBuilder();

            String contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
            marshaller = jaxbContext.createMarshaller();

            originalSetting = true;
            marshaller.setProperty("org.glassfish.jaxb.xmlDeclaration", Boolean.FALSE);

            //set up controlObject
            controlObject = new Employee();
            controlObject.setName(CONTROL_EMPLOYEE_NAME);

            //set up control Document
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            controlDocument = parser.parse(inputStream);
            removeEmptyTextNodes(controlDocument);
            removeCopyrightNode(controlDocument);
        }
}
