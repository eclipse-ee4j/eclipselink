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
*     mmacivor - September 14/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

/**
 * Convert and XMLEventReader into SAX events.
 */
public class XMLEventReaderReader extends XMLReaderAdapter {

    private int depth = 0;
    //Required because the EndElement fails to properly won't give the list of namespaces going out of
    //scope in some STAX implementations
    private Map<Integer, List<Namespace>> namespaces;
    private XMLEventReaderAttributes indexedAttributeList;
    private XMLEvent lastEvent;

    public XMLEventReaderReader() {
        this.namespaces = new HashMap<Integer, List<Namespace>>();
        this.indexedAttributeList = new XMLEventReaderAttributes();
    }

    public XMLEventReaderReader(Unmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
        this.namespaces = new HashMap<Integer, List<Namespace>>();
        this.indexedAttributeList = new XMLEventReaderAttributes();
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

    public Locator getLocator(){
    	if(locator == null){
    		locator = new  EventReaderLocator();    		
    	}
    	((EventReaderLocator)locator).setEvent(lastEvent);
    	return locator;
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
                    contentHandler.endElement(name.getNamespaceURI(), name.getLocalPart(), prefix + Constants.COLON + name.getLocalPart());
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
                lastEvent = xmlEvent;

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
                indexedAttributeList.setIterators(startElement.getAttributes(), startElement.getNamespaces());
                if(null == prefix || prefix.length() == 0) {
                    contentHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), qName.getLocalPart(), indexedAttributeList);
                } else {
                    contentHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), prefix + Constants.COLON + qName.getLocalPart(), indexedAttributeList);
                }
                break;
            }
        }
    }

    private static class XMLEventReaderAttributes extends IndexedAttributeList {

        private Iterator namespaces;
        private Iterator attrs;

        public void setIterators(Iterator attrs, Iterator namespaces) {
            reset();
            this.namespaces = namespaces;
            this.attrs = attrs;
        }

        @Override
        protected Attribute[] attributes() {
            if(null == attributes) {
            if(attrs.hasNext() || namespaces.hasNext()) {

                ArrayList<Attribute> attributesList = new ArrayList<Attribute>();

                while(namespaces.hasNext()) {
                    Namespace next = (Namespace)namespaces.next();
                    String uri = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
                    String localName = next.getPrefix();
                    String qName;
                    if(null == localName || localName.length() == 0) {
                        localName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
                        qName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
                    } else {
                        qName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + localName;
                    }
                    String value = next.getNamespaceURI();
                    attributesList.add(new Attribute(uri, localName, qName, value));
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
                        qName = prefix + Constants.COLON + localName;
                    }
                    String value = next.getValue();
                    attributesList.add(new Attribute(uri, localName, qName, value));
                }

                attributes = attributesList.toArray(new Attribute[attributesList.size()]);
            }else{
                attributes = NO_ATTRIBUTES;

                }
            }
            return attributes;
        }

    }

    /**
     * <p>An implementation of Locator, with location data provided by an existing XMLEvent.</p>
     *
     * @see org.xml.sax.Locator
     * @see javax.xml.stream.events.XMLEvent
     */
    private class EventReaderLocator implements Locator2 {

        private XMLEvent event;

        /**
         * Instantiates a new EventReaderLocator.
         */
        public EventReaderLocator() {
        }

        /**
         * Set the XMLEvent for this EventReaderLocator.
         *
         * @param e the XMLEvent object from which to copy location information.
         */
        public void setEvent(XMLEvent e) {
            this.event = e;
        }

        /**
         * Returns the public ID of this Locator.
         */
        public String getPublicId() {
            if (this.event == null) {
                return null;
            }
            return this.event.getLocation().getPublicId();
        }

        /**
         * Returns the system ID of this Locator.
         */
        public String getSystemId() {
            if (this.event == null) {
                return null;
            }
            return this.event.getLocation().getSystemId();
        }

        /**
         * Returns the line number of this Locator.
         */
        public int getLineNumber() {
            if (this.event == null) {
                return -1;
            }
            return this.event.getLocation().getLineNumber();
        }

        /**
         * Returns the column number of this Locator.
         */
        public int getColumnNumber() {
            if (this.event == null) {
                return -1;
            }
            return this.event.getLocation().getColumnNumber();
        }

        public String getXMLVersion() {
            return null;
        }

        public String getEncoding() {
            return null;
        }

    }
    
}