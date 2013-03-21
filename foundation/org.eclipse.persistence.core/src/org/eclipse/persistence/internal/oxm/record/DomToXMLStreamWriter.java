/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DomToXMLStreamWriter{

    public void writeToStream(Node dom, String newUri, String newName, XMLStreamWriter xsw) throws XMLStreamException {
        Node currentNode = dom;
        if(dom.getNodeType() == Node.DOCUMENT_NODE) {
            Document doc = (Document)dom;
            xsw.writeStartDocument(doc.getXmlEncoding(), doc.getXmlVersion());
            currentNode = doc.getDocumentElement();
        }
        if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
            writeElement((Element)currentNode, newUri, newName, xsw);
        } else if(currentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attribute = (Attr)currentNode;
            if(attribute.getPrefix() != null && attribute.getPrefix().equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
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

    private void writeElement(Element elem, String newNamespace, String newName, XMLStreamWriter xsw) throws XMLStreamException {
        String prefix = null;
        String namespace = null;
        String localName = null;
        String nodeName = null;
        if(newName != null){
            namespace = newNamespace;
            localName = newName;
            nodeName = newName;
            if(newNamespace != null && newNamespace.length() > 0) {
                NamespaceResolver tempNR = new NamespaceResolver();
                tempNR.setDOM(elem);
                
                prefix = tempNR.resolveNamespaceURI(namespace);
                
                if(prefix == null || prefix.length() == 0){
                    String defaultNamespace = elem.getAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
                    if(defaultNamespace == null){
                        prefix = tempNR.generatePrefix();    
                    }else if(defaultNamespace != namespace){
                        prefix = tempNR.generatePrefix();
                    }else{
                        prefix = Constants.EMPTY_STRING;
                    }                    
                }         
               
            }
        }else{
            prefix = elem.getPrefix();         
            namespace = elem.getNamespaceURI();   
            localName = elem.getLocalName();
            nodeName = elem.getNodeName();
        }
             
        if(prefix != null && prefix.length() > 0) {
           String namespaceURI = xsw.getNamespaceContext().getNamespaceURI(prefix);
            xsw.writeStartElement(prefix, localName, namespace);
            if(!(namespace.equals(namespaceURI))) {
                xsw.writeNamespace(prefix, namespace);
           }
        } else {
            if(namespace == null || namespace.length() == 0) {
                xsw.writeStartElement(nodeName);                
                String defaultNamespace = xsw.getNamespaceContext().getNamespaceURI(Constants.EMPTY_STRING);
                if(defaultNamespace != null &&  defaultNamespace.length() >0) {
                    //write default namespace declaration
                    xsw.writeDefaultNamespace(Constants.EMPTY_STRING);
                }
            } else {
                xsw.writeStartElement(Constants.EMPTY_STRING, localName, namespace);
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
                if(next.getPrefix() != null && next.getPrefix().equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                    String currentUri = xsw.getNamespaceContext().getNamespaceURI(next.getLocalName());
                    if(currentUri == null || !currentUri.equals(next.getValue())) {
                        xsw.writeNamespace(next.getLocalName(), next.getValue());
                    }
                } else {
                	if (next.getName().equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)){
                            //Part of bug fix 398446 modified fix for Bug 387464. 
                		xsw.writeDefaultNamespace(next.getValue());
                	}else{
                		nonNamespaceDeclAttrs.add(attribute);
                	}
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
                writeElement((Element)next,null, null, xsw);
            }
        }
        xsw.writeEndElement();
    }
    
    protected String getPrefix (NamespaceContext nc, Element elem, String uri){
        NamespaceResolver tempResovler = new NamespaceResolver();
        tempResovler.setDOM(elem);
        
        String prefix = tempResovler.resolveNamespaceURI(uri);
        if(prefix == null || prefix.length() == 0){
            prefix = tempResovler.generatePrefix();
        }
        return prefix;
    }
}
