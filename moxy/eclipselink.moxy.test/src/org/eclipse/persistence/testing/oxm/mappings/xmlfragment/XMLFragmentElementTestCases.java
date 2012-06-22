/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

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
  
  public void testObjectToContentHandlerNull() {
      Object obj = getControlObject();
      try {
          xmlMarshaller.marshal(obj, new ContentHandler() {

              public void setDocumentLocator(Locator locator) {
              }

              public void startDocument() throws SAXException {
              }

              public void endDocument() throws SAXException {
              }

              public void startPrefixMapping(String prefix, String uri) throws SAXException {
              }

              public void endPrefixMapping(String prefix) throws SAXException {
              }

              public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                  if(atts.getLength() > 0) {
                      for(int i = 0; i < atts.getLength(); i++) {
                          if(atts.getURI(i) == null) {
                              throw new SAXException();
                          }
                      }
                  }
              }

              public void endElement(String uri, String localName, String qName) throws SAXException {
              }

              public void characters(char[] ch, int start, int length) throws SAXException {
              }

              public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
              }

              public void processingInstruction(String target, String data) throws SAXException {
              }

              public void skippedEntity(String name) throws SAXException {
              }
          });
      }catch (Exception ex) {
          fail("An unexpected exception was thrown due to a null namespace on an attribute");
      }
  }
  
}
