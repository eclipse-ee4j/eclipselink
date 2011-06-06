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
* mmacivor - June 24/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DomToXMLStreamWriter {

    public void writeToStream(Node dom, XMLStreamWriter xsw) throws XMLStreamException {
        Node currentNode = dom;
        if(dom.getNodeType() == Node.DOCUMENT_NODE) {
            Document doc = (Document)dom;
            xsw.writeStartDocument(doc.getXmlEncoding(), doc.getXmlVersion());
            currentNode = doc.getDocumentElement();
        }
        if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
            writeElement((Element)currentNode, xsw);
        } else if(currentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attribute = (Attr)currentNode;
            if(attribute.getPrefix() != null && attribute.getPrefix().equals(XMLConstants.XMLNS)) {
                xsw.writeNamespace(attribute.getLocalName(), attribute.getValue());
            } else {
                if(attribute.getPrefix() == null) {
                    xsw.writeAttribute(attribute.getName(), attribute.getValue());
                } else {
                    xsw.writeAttribute(attribute.getPrefix(), attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getValue());
                }
            }
        } else if(currentNode.getNodeType() == Node.TEXT_NODE) {
                xsw.writeCharacters(((Text)currentNode).getNodeValue());
        }
        if(dom.getNodeType() == Node.DOCUMENT_NODE) {
            xsw.writeEndDocument();
        }

    }

    private void writeElement(Element elem, XMLStreamWriter xsw) throws XMLStreamException {
        if(elem.getPrefix() != null && elem.getPrefix().length() > 0) {
            String namespaceURI = xsw.getNamespaceContext().getNamespaceURI(elem.getPrefix());
            xsw.writeStartElement(elem.getPrefix(), elem.getLocalName(), elem.getNamespaceURI());
            if(!(elem.getNamespaceURI().equals(namespaceURI))) {
                xsw.writeNamespace(elem.getPrefix(), elem.getNamespaceURI());
            }
        } else {
            String localName = elem.getLocalName();
            String name = elem.getNodeName();
            if(elem.getNamespaceURI() == null || elem.getNamespaceURI().length() == 0) {
                xsw.writeStartElement(elem.getNodeName());
                String defaultNamespace = xsw.getNamespaceContext().getNamespaceURI(XMLConstants.EMPTY_STRING);
                if(defaultNamespace != null &&  defaultNamespace.length() >0) {
                    //write default namespace declaration
                    xsw.writeDefaultNamespace(XMLConstants.EMPTY_STRING);
                }
            } else {
                xsw.writeStartElement(XMLConstants.EMPTY_STRING, elem.getLocalName(), elem.getNamespaceURI());
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
                    String currentUri = xsw.getNamespaceContext().getNamespaceURI(next.getLocalName());
                    if(currentUri == null || !currentUri.equals(next.getValue())) {
                        xsw.writeNamespace(next.getLocalName(), next.getValue());
                    }
                } else {
                    nonNamespaceDeclAttrs.add(attribute);
                }
            }
        }
        for(Attr next:nonNamespaceDeclAttrs) {
            if(next.getPrefix() == null) {
                xsw.writeAttribute(next.getName(), next.getValue());
            } else {
                xsw.writeAttribute(next.getPrefix(), next.getNamespaceURI(), next.getLocalName(), next.getValue());
            }
        }
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node next = childNodes.item(i);
            if(next.getNodeType() == Node.TEXT_NODE) {
                xsw.writeCharacters(((Text)next).getNodeValue());
            } else if(next.getNodeType() == Node.CDATA_SECTION_NODE) {
                xsw.writeCData(next.getNodeValue());
            } else if(next.getNodeType() == Node.COMMENT_NODE) {
                xsw.writeComment(next.getNodeValue());
            } else if(next.getNodeType() == Node.ELEMENT_NODE) {
                writeElement((Element)next, xsw);
            }
        }
        xsw.writeEndElement();
    }
}
