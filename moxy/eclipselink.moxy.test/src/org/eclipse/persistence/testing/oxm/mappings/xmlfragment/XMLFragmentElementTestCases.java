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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

/**
 *  @version $Header: XMLFragmentElementTestCases.java 21-aug-2007.10:51:04 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLFragmentElementTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/employee_element.xml";
  private final static String XML_SUB_ELEMENT = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/sub_element.xml";

  public XMLFragmentElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new XMLFragmentElementProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.firstName = "Jane";
    employee.lastName = "Doe";

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringElementContentWhitespace(true);
    try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(XML_SUB_ELEMENT));
        removeEmptyTextNodes(doc);
        employee.xmlNode = doc.getDocumentElement();
    } catch(Exception ex) {}
    return employee;
  }
}
