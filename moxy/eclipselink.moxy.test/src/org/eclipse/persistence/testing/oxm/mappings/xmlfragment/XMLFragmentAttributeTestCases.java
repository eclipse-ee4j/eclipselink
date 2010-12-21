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
 *  @version $Header: XMLFragmentElementTestCases.java 11-sep-2006.12:59:03 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLFragmentAttributeTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/employee_attribute.xml";

  public XMLFragmentAttributeTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new XMLFragmentAttributeProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.firstName = "Jane";
    employee.lastName = "Doe";

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Attr node = doc.createAttribute("attribute");
        node.setValue("attributevalue");
        
        employee.xmlNode = node;
    } catch(Exception ex) {}
    return employee;
  }
}
