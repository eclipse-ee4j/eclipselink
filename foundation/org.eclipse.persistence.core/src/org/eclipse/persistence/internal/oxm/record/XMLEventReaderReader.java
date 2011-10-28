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
*     mmacivor - September 14/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Convert and XMLEventReader into SAX events. 
 */
public class XMLEventReaderReader extends XMLReaderAdapter {

    private int depth = 0;
    //Required because the EndElement fails to properly won't give the list of namespaces going out of
    //scope in some STAX implementations
    private Map<Integer, List<Namespace>> namespaces;

    public XMLEventReaderReader() {
        this.namespaces = new HashMap<Integer, List<Namespace>>();
    }

    public XMLEventReaderReader(XMLUnmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
        this.namespaces = new HashMap<Integer, List<Namespace>>();
    }

    @Override
    public void parse(InputSource input) throws SAXException {
        if(null == contentHandler) {
            return;
        }
        if(input instanceof XMLEventReaderInputSource) {
            XMLEventReader xmlEventReader = ((XMLEventReaderInputSource) input).getXmlEventReader();
            parse(xmlEventReader);
        }
    }

    private void parse(XMLEventReader xmlEventReader) throws SAXException {
        try {
            contentHandler.startDocument();
            parseEvent(xmlEventReader.nextEvent());
            while(depth > 0) {
                parseEvent(xmlEventReader.nextEvent());
            }
            contentHandler.endDocument();
        } catch(XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void parseEvent(XMLEvent xmlEvent) throws SAXException {
        switch (xmlEvent.getEventType()) {
            case XMLEvent.ATTRIBUTE:  {
                break;
            }
            case XMLEvent.CDATA: {
                Characters characters = xmlEvent.asCharacters();
                if(null == lexicalHandler) {
                    contentHandler.characters(characters.getData().toCharArray(), 0, characters.getData().length());
                } else {
                    lexicalHandler.startCDATA();
                    contentHandler.characters(characters.getData().toCharArray(), 0, characters.getData().length());
                    lexicalHandler.endCDATA();
                }
                break;
            }
            case XMLEvent.CHARACTERS: {
                char[] characters = xmlEvent.asCharacters().getData().toCharArray();
                contentHandler.characters(characters, 0, characters.length);
                break;
            }
            case XMLEvent.COMMENT: {
                if(null != lexicalHandler) {
                    char[] comment = ((Comment) xmlEvent).getText().toCharArray();
                    lexicalHandler.comment(comment, 0, comment.length);
                }
                break;
            }
            case XMLEvent.DTD: {
                break;
            }
            case XMLEvent.END_DOCUMENT: {
                depth--;
                return;
            }
            case XMLEvent.END_ELEMENT: {
                List<Namespace> declaredNs = this.namespaces.get(depth);
                depth--;
                EndElement endElement = xmlEvent.asEndElement();

                QName name = endElement.getName();
                String prefix = endElement.getName().getPrefix();
                if(null == prefix || prefix.length() == 0) {
                    contentHandler.endElement(name.getNamespaceURI(), name.getLocalPart(), name.getLocalPart());
                } else {
                    contentHandler.endElement(name.getNamespaceURI(), name.getLocalPart(), prefix + XMLConstants.COLON + name.getLocalPart());
                }
                if(declaredNs != null) {
                    for(Namespace next : declaredNs) {
                        contentHandler.endPrefixMapping(next.getPrefix());
                    }
                }
                break;
            }
            case XMLEvent.ENTITY_DECLARATION: {
                break;
            }
            case XMLEvent.ENTITY_REFERENCE: {
                break;
            }
            case XMLEvent.NAMESPACE: {
                break;
            }
            case XMLEvent.NOTATION_DECLARATION: {
                break;
            }
            case XMLEvent.PROCESSING_INSTRUCTION: {
                ProcessingInstruction pi = (ProcessingInstruction)xmlEvent;
                contentHandler.processingInstruction(pi.getTarget(), pi.getData());
                break;
            }
            case XMLEvent.SPACE: {
                char[] characters = xmlEvent.asCharacters().getData().toCharArray(); 
                contentHandler.characters(characters, 0, characters.length);
                break;
            }
            case XMLEvent.START_DOCUMENT: {
                depth++;
                break;
            }
            case XMLEvent.START_ELEMENT: {
                depth++;
                StartElement startElement = xmlEvent.asStartElement();
                Iterator namespaces = startElement.getNamespaces();
                List<Namespace> declaredNs = null;
                if(namespaces.hasNext()) {
                    declaredNs = new ArrayList<Namespace>();
                }
                while(namespaces.hasNext()) {
                    Namespace next = (Namespace)namespaces.next();
                    contentHandler.startPrefixMapping(next.getPrefix(), next.getNamespaceURI());
                    declaredNs.add(next);
                }
                if(declaredNs != null) {
                    this.namespaces.put(depth, declaredNs);
                }
                QName qName = startElement.getName();
                String prefix = qName.getPrefix();
                if(null == prefix || prefix.length() == 0) {
                    contentHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), qName.getLocalPart(), new IndexedAttributeList(startElement.getAttributes(), startElement.getNamespaces()));
                } else {
                    contentHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), prefix + XMLConstants.COLON + qName.getLocalPart(), new IndexedAttributeList(startElement.getAttributes(), startElement.getNamespaces()));
                }
                break;
            }
        }
    }

    private static class IndexedAttributeList implements Attributes {

        private List<Attribute> attributes;

        public IndexedAttributeList(Iterator attrs, Iterator namespaces) {
            if(attrs.hasNext() || namespaces.hasNext()) {
                this.attributes = new ArrayList<Attribute>();

                while(namespaces.hasNext()) {
                    Namespace next = (Namespace)namespaces.next();
                    String uri = XMLConstants.XMLNS_URL; 
                    String localName = next.getPrefix(); 
                    String qName; 
                    if(null == localName || localName.length() == 0) { 
                        localName = XMLConstants.XMLNS; 
                        qName = XMLConstants.XMLNS; 
                    } else { 
                        qName = XMLConstants.XMLNS + XMLConstants.COLON + localName; 
                    } 
                    String value = next.getNamespaceURI(); 
                    attributes.add(new Attribute(uri, localName, qName, value)); 
                } 

                while(attrs.hasNext()) {
                    javax.xml.stream.events.Attribute next = (javax.xml.stream.events.Attribute)attrs.next();
                    String uri = next.getName().getNamespaceURI();
                    String localName = next.getName().getLocalPart();
                    String prefix = next.getName().getPrefix();
                    String qName;
                    if(null == prefix || prefix.length() == 0) {
                        qName = localName;
                    } else {
                        qName = prefix + XMLConstants.COLON + localName;
                    }
                    String value = next.getValue();
                    attributes.add(new Attribute(uri, localName, qName, value));
                }
            } else {
                attributes = Collections.EMPTY_LIST;
            }

        }

        public int getIndex(String qName) {
            if(null == qName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes) {
                if(qName.equals(attribute.getName())) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public int getIndex(String uri, String localName) {
            if(null == localName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes) {
                QName testQName = new QName(uri, localName);
                if(attribute.getQName().equals(testQName)) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public int getLength() {
            return attributes.size();
        }

        public String getLocalName(int index) {
            return attributes.get(index).getQName().getLocalPart();
        }

        public String getQName(int index) {
            return attributes.get(index).getName();
        }

        public String getType(int index) {
            return XMLConstants.CDATA;
        }

        public String getType(String name) {
            return XMLConstants.CDATA;
        }

        public String getType(String uri, String localName) {
            return XMLConstants.CDATA;
        }

        public String getURI(int index) {
            return attributes.get(index).getQName().getNamespaceURI();
        }

        public String getValue(int index) {
            return attributes.get(index).getValue();
        }

        public String getValue(String qName) {
            int index = getIndex(qName);
            if(-1 == index) {
                return null;
            } 
            return attributes.get(index).getValue();
        }

        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            if(-1 == index) {
                return null;
            }
            return attributes.get(index).getValue();
        }

    }

    private static class Attribute {

        private QName qName;
        private String name;
        private String value;

        public Attribute(String uri, String localName, String name, String value) {
            this.qName = new QName(uri, localName);
            this.name = name;
            this.value = value;
        }

        public QName getQName() {
            return qName;
        }

        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }

    }

}