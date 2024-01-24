/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLFragmentElementTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/employee_element.xml";
  private final static String XML_SUB_ELEMENT = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/sub_element.xml";

  public XMLFragmentElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new XMLFragmentElementProject());
  }

  @Override
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

  public void testObjectToContentHandlerNull() {
      Object obj = getControlObject();
      try {
          xmlMarshaller.marshal(obj, new ContentHandler() {

              @Override
              public void setDocumentLocator(Locator locator) {
              }

              @Override
              public void startDocument() throws SAXException {
              }

              @Override
              public void endDocument() throws SAXException {
              }

              @Override
              public void startPrefixMapping(String prefix, String uri) throws SAXException {
              }

              @Override
              public void endPrefixMapping(String prefix) throws SAXException {
              }

              @Override
              public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                  if(atts.getLength() > 0) {
                      for(int i = 0; i < atts.getLength(); i++) {
                          if(atts.getURI(i) == null) {
                              throw new SAXException();
                          }
                      }
                  }
              }

              @Override
              public void endElement(String uri, String localName, String qName) throws SAXException {
              }

              @Override
              public void characters(char[] ch, int start, int length) throws SAXException {
              }

              @Override
              public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
              }

              @Override
              public void processingInstruction(String target, String data) throws SAXException {
              }

              @Override
              public void skippedEntity(String name) throws SAXException {
              }
          });
      }catch (Exception ex) {
          fail("An unexpected exception was thrown due to a null namespace on an attribute");
      }
  }


}
