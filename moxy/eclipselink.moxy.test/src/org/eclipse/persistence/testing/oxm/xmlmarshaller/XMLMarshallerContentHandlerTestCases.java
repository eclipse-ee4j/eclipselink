/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLValidator;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XMLMarshallerContentHandlerTestCases extends OXTestCase {
    private XMLContext xmlContext;
    private XMLValidator xmlValidator;
    private XMLMarshallerTestProject project;

    public XMLMarshallerContentHandlerTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        project = new XMLMarshallerTestProject();
        xmlContext = new XMLContext(project);
        xmlValidator = xmlContext.createValidator();
    }

    public void testMarshalToContentHandler() throws Exception {
        Employee emp = new Employee();
        emp.setID(123);
        emp.setEmailAddress(new EmailAddress());
        try {
            xmlContext.createMarshaller().marshal(emp, new ContentHandler() {
                @Override
                public void startPrefixMapping(String prefix, String uri) throws SAXException {
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                    if(uri == null) {
                        throw new NullPointerException();
                    }
                }
            
                @Override
                public void startDocument() throws SAXException {
                }
                
                @Override
                public void skippedEntity(String name) throws SAXException {
                }
                
                @Override
                public void setDocumentLocator(Locator locator) {
                }
                
                @Override
                public void processingInstruction(String target, String data) throws SAXException {
                }
                
                @Override
                public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                }
                
                @Override
                public void endPrefixMapping(String prefix) throws SAXException {
                }
                
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if(uri == null) {
                        throw new NullPointerException();
                    }
                }
                
                @Override
                public void endDocument() throws SAXException {
                }
                
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                }
            }); 
        } catch(Exception ex) {
            fail("Exception caught when marshalling to content handler " + ex.getMessage());
        }
    }
}
