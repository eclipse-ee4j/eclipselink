/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement;

import java.io.InputStream;

import org.w3c.dom.Document;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee;

public class DirectToXMLElementIdentifiedByNameNegativeTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/xmlelement/DirectToXMLElementIdentifiedByNameNegative.xml";
  private final static String WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/xmlelement/DirectToXMLElementIdentifiedByNameNegativeWriting.xml";
  private final static int CONTROL_ID = 0;
  private final static String CONTROL_FIRST_NAME = null;
  private final static String CONTROL_LAST_NAME = null;

  public DirectToXMLElementIdentifiedByNameNegativeTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new DirectToXMLElementIdentifiedByNameProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
    employee.setFirstName(CONTROL_FIRST_NAME);
    employee.setLastName(CONTROL_LAST_NAME);
    return employee;
  }

  protected Document getWriteControlDocument() throws Exception {
      InputStream inputStream = ClassLoader.getSystemResourceAsStream(WRITE_RESOURCE);
      controlDocument = parser.parse(inputStream);
      removeEmptyTextNodes(controlDocument);
      inputStream.close();
      return controlDocument;
  }

}
