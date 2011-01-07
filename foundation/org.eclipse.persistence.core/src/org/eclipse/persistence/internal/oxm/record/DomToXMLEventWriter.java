/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - September 9/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DomToXMLEventWriter {
    private XMLEventFactory xmlEventFactory;
    
    public DomToXMLEventWriter() {
        this.xmlEventFactory = XMLEventFactory.newInstance();
    }
    
    public DomToXMLEventWriter(XMLEventFactory xmlEventFactory) {
        this.xmlEventFactory = xmlEventFactory;
    }
    
    public void writeToEventWriter(Node dom, XMLEventWriter xew) throws XMLStreamException {
        Node currentNode = dom;
        if(dom.getNodeType() == Node.DOCUMENT_NODE) {
            Document doc = (Document)dom;
            xew.add(xmlEventFactory.createStartDocument(doc.getXmlEncoding(), doc.getXmlVersion()));
            currentNode = doc.getDocumentElement();
        }
        if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
            writeElement((Element)currentNode, xew);
        } else if(currentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attribute = (Attr)currentNode;
            if(attribute.getPrefix() != null && attribute.getPrefix().equals(XMLConstants.XMLNS)) {
                xew.add(xmlEventFactory.createNamespace(attribute.getLocalName(), attribute.getValue()));
            } else {
                if(attribute.getPrefix() == null) {
                    xew.add(xmlEventFactory.createAttribute(attribute.getName(), attribute.getValue()));
                } else {
                    xew.add(xmlEventFactory.createAttribute(attribute.getPrefix(), attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getValue()));
                }
            }
        } else if(currentNode.getNodeType() == Node.TEXT_NODE) {
                xew.add(xmlEventFactory.createCharacters(((Text)currentNode).getNodeValue()));
        }
        if(dom.getNodeType() == Node.DOCUMENT_NODE) {
            xew.add(xmlEventFactory.createEndDocument());
        }
    }
    
    private void writeElement(Element elem, XMLEventWriter xew) throws XMLStreamException {
        if(elem.getPrefix() != null && elem.getPrefix().length() > 0) {
            String namespaceURI = xew.getNamespaceContext().getNamespaceURI(elem.getPrefix());
            xew.add(xmlEventFactory.createStartElement(elem.getPrefix(), elem.getNamespaceURI(), elem.getLocalName()));
            if(!(elem.getNamespaceURI().equals(namespaceURI))) {
                xew.add(xmlEventFactory.createNamespace(elem.getPrefix(), elem.getNamespaceURI()));
            }
        } else {
            String localName = elem.getLocalName();
            String name = elem.getNodeName();
            if(elem.getNamespaceURI() == null || elem.getNamespaceURI().length() == 0) {
                String defaultNamespace = xew.getNamespaceContext().getNamespaceURI(XMLConstants.EMPTY_STRING);
                xew.add(xmlEventFactory.createStartElement("", "", elem.getNodeName()));
                if(defaultNamespace != null &&  defaultNamespace.length() >0) {
                    //write default namespace declaration
                    xew.add(xmlEventFactory.createNamespace(XMLConstants.EMPTY_STRING));
                }
            } else {
                xew.add(xmlEventFactory.createStartElement(XMLConstants.EMPTY_STRING, elem.getNamespaceURI(), elem.getLocalName()));
            }
        }
        NodeList childNodes = elem.getChildNodes();
        NamedNodeMap attrs = elem.getAttributes();

        ArrayList<Attr> nonNamespaceDeclAttrs = new ArrayList<Attr>();
        //write out any namespace decls first
        for(int i = 0; i < attrs.getLength(); i++) {
            Attr next = (Attr)attrs.item(i);
            if(next.getNodeType() == Node.ATTRIBUTE_NODE) {
                Attr attribute = next;
                if(next.getPrefix() != null && next.getPrefix().equals(XMLConstants.XMLNS)) {
                    String currentUri = xew.getNamespaceContext().getNamespaceURI(next.getLocalName());
                    if(currentUri == null || !currentUri.equals(next.getValue())) {
                        xew.add(xmlEventFactory.createNamespace(next.getLocalName(), next.getValue()));
                    }
                } else {
                    nonNamespaceDeclAttrs.add(attribute);
                }
            }
        }
        for(Attr next:nonNamespaceDeclAttrs) {
            if(next.getPrefix() == null) {
                xew.add(xmlEventFactory.createAttribute(next.getName(), next.getValue()));
            } else {
                xew.add(xmlEventFactory.createAttribute(next.getPrefix(), next.getNamespaceURI(), next.getLocalName(), next.getValue()));
            }
        }
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node next = childNodes.item(i);
            if(next.getNodeType() == Node.TEXT_NODE) {
                xew.add(xmlEventFactory.createCharacters(((Text)next).getNodeValue()));
            } else if(next.getNodeType() == Node.CDATA_SECTION_NODE) {
                xew.add(xmlEventFactory.createCData(next.getNodeValue()));
            } else if(next.getNodeType() == Node.COMMENT_NODE) {
                xew.add(xmlEventFactory.createComment(next.getNodeValue()));
            } else if(next.getNodeType() == Node.ELEMENT_NODE) {
                writeElement((Element)next, xew);
            }
        }
        if(elem.getPrefix() != null && elem.getPrefix().length() > 0) {
            xew.add(xmlEventFactory.createEndElement(elem.getPrefix(), elem.getNamespaceURI(), elem.getLocalName()));
        } else if(elem.getNamespaceURI() != null && elem.getNamespaceURI().length() > 0) {
            xew.add(xmlEventFactory.createEndElement("", elem.getNamespaceURI(), elem.getLocalName()));
        } else {
            xew.add(xmlEventFactory.createEndElement("", "", elem.getNodeName()));
        }
    }
}
