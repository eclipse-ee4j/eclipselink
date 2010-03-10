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
* mmacivor - September 09/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DomToXMLEventWriter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLEventWriterRecord extends MarshalRecord {
    private Map<String, String> prefixMapping;
    private NamespaceResolver namespaceResolver;

    private XMLEventWriter xmlEventWriter;
    private XMLEventFactory xmlEventFactory;
    private DomToXMLEventWriter domToXMLEventWriter;
    private boolean isStartElementOpen = false;
    private List attributes;
    private List namespaceDeclarations;
    private String namespaceURI;
    private XPathFragment xPathFragment;

    public XMLEventWriterRecord(XMLEventWriter xmlEventWriter) {
        this.xmlEventWriter = xmlEventWriter;
        this.xmlEventFactory = XMLEventFactory.newInstance();
        this.domToXMLEventWriter = new DomToXMLEventWriter(xmlEventFactory);
    }

    public XMLEventWriter getXMLEventWriter() {
        return xmlEventWriter;
    }

    public void setXMLEventWriter(XMLEventWriter anXMLEventWriter) {
        this.xmlEventWriter = anXMLEventWriter;
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
         String namespaceURI = xPathFragment.getNamespaceURI();
         XMLEvent event;
         if(namespaceURI == null) {
             event = xmlEventFactory.createAttribute(xPathFragment.getLocalName(), value);
         } else {
             String prefix = xPathFragment.getPrefix();
             if(prefix == null) {
                 event = xmlEventFactory.createAttribute(xPathFragment.getPrefix(), namespaceURI, xPathFragment.getLocalName(), value);
             } else {
                 if(xmlEventWriter.getNamespaceContext().getNamespaceURI(prefix) == null || !xmlEventWriter.getNamespaceContext().getNamespaceURI(prefix).equals(namespaceURI)) {
                     event = xmlEventFactory.createNamespace(prefix, namespaceURI);
                     try {
                    	 this.xmlEventWriter.setPrefix(prefix, namespaceURI);
                     } catch(XMLStreamException e) {
                         throw XMLMarshalException.marshalException(e);
                     }
                 }
                 event = xmlEventFactory.createAttribute(prefix, namespaceURI, xPathFragment.getLocalName(), value);
             }
         }
         if(event.isNamespace()) {
             if(null == this.namespaceDeclarations) {
                 this.namespaceDeclarations = new ArrayList();
             }
             this.namespaceDeclarations.add(event);
         } else {
             if(null == attributes) {
                 attributes = new ArrayList();
             }
             this.attributes.add(event);
         }
    }

    public void attribute(String namespaceURI, String localName, String name, String value) {
         XMLEvent event;
         if(namespaceURI != null && namespaceURI.equals(XMLConstants.XMLNS_URL)) {
        	 try {
	             if(localName.equals(XMLConstants.XMLNS)) {
	                 event = xmlEventFactory.createNamespace(value);
	                 xmlEventWriter.setDefaultNamespace(value);
	             }  else {
	                  event = xmlEventFactory.createNamespace(localName, value);
	                  xmlEventWriter.setPrefix(localName, value);
	             }
        	 } catch(XMLStreamException e) {
                 throw XMLMarshalException.marshalException(e);
        	 }
         } else {
             NamespaceContext ctx = xmlEventWriter.getNamespaceContext();
             if(namespaceURI == null || namespaceURI.length() == 0) {
                 event = xmlEventFactory.createAttribute(localName, value);
             } else {
                 int index = name.indexOf(':');
                 if(index == -1) {
                     event = xmlEventFactory.createAttribute(XMLConstants.EMPTY_STRING, namespaceURI, localName, value);
                 } else {
                     String prefix = name.substring(0, index);
                     event = xmlEventFactory.createAttribute(prefix, namespaceURI, localName, value);
                 }
             }
         }
         if(event.isNamespace()) {
             if(null == this.namespaceDeclarations) {
                 this.namespaceDeclarations = new ArrayList();
             }
             this.namespaceDeclarations.add(event);
         } else {
             if(null == this.attributes) {
                 this.attributes = new ArrayList();
             }
             this.attributes.add(event);
         }
    }


    private void openAndCloseStartElement() {
        try {
             if(namespaceURI == null) {
                 Iterator attributesIterator = null;
                 if(null != attributes) {
                     attributesIterator = attributes.iterator();
                 }
                 Iterator namespaceDeclarationsIterator = null;
                 if(null != namespaceDeclarations) {
                     namespaceDeclarationsIterator = namespaceDeclarations.iterator();
                 }
                 xmlEventWriter.add(xmlEventFactory.createStartElement(XMLConstants.EMPTY_STRING, XMLConstants.EMPTY_STRING, xPathFragment.getLocalName(), attributesIterator, namespaceDeclarationsIterator));
                 String defaultNamespace = xmlEventWriter.getNamespaceContext().getNamespaceURI(XMLConstants.EMPTY_STRING);
                 if(defaultNamespace != null && defaultNamespace.length() > 0 ) {
                     this.xmlEventWriter.setDefaultNamespace(XMLConstants.EMPTY_STRING);
                     this.xmlEventWriter.add(xmlEventFactory.createNamespace(XMLConstants.EMPTY_STRING));
                 }
             } else {
                 String prefix = xPathFragment.getPrefix();
                 if(prefix == null) {
                     prefix = XMLConstants.EMPTY_STRING;
                 }
                 Iterator attributesIterator = null;
                 if(null != attributes) {
                     attributesIterator = attributes.iterator();
                 }
                 Iterator namespaceDeclarationsIterator = null;
                 if(null != namespaceDeclarations) {
                     namespaceDeclarationsIterator = namespaceDeclarations.iterator();
                 }
                 XMLEvent startElement = this.xmlEventFactory.createStartElement(prefix, namespaceURI, xPathFragment.getLocalName(), attributesIterator, namespaceDeclarationsIterator);
                 xmlEventWriter.add(startElement);
             }
        } catch(XMLStreamException ex) {
            throw XMLMarshalException.marshalException(ex);
        }
    }

    public void cdata(String value) {
        try {
            if(isStartElementOpen) {
                openAndCloseStartElement();
                isStartElementOpen = false;
            }
            XMLEvent cdataEvent = this.xmlEventFactory.createCData(value);
            this.xmlEventWriter.add(cdataEvent);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
       }
    }

    public void characters(String value) {
        try {
            if(isStartElementOpen) {
                openAndCloseStartElement();
                isStartElementOpen = false;
            }
            XMLEvent charactersEvent = this.xmlEventFactory.createCharacters(value);
            this.xmlEventWriter.add(charactersEvent);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void closeStartElement() {
    }

    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        if (isStartElementOpen) {
            openAndCloseStartElement();
        }
        isStartElementOpen = true;
        this.namespaceURI = xPathFragment.getNamespaceURI();
        this.xPathFragment = xPathFragment;
        this.attributes = null;
        this.namespaceDeclarations = null;
        writePrefixMappings();
    }

    public void element(XPathFragment frag) {
        try {
            if(isStartElementOpen) {
                openAndCloseStartElement();
                isStartElementOpen = false;
            }
            String prefix = frag.getPrefix();
            if(null == prefix) {
                prefix = XMLConstants.EMPTY_STRING;
            }
            XMLEvent startElement = this.xmlEventFactory.createStartElement(prefix, frag.getNamespaceURI(), frag.getLocalName());
            this.xmlEventWriter.add(startElement);
            this.xmlEventWriter.add(this.xmlEventFactory.createEndElement(prefix, frag.getNamespaceURI(), frag.getLocalName()));
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endDocument() {
        try {
            if(isStartElementOpen) {
                openAndCloseStartElement();
                isStartElementOpen = false;
            }
            XMLEvent endDoc = this.xmlEventFactory.createEndDocument();
            this.xmlEventWriter.add(endDoc);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endElement(XPathFragment pathFragment, NamespaceResolver namespaceResolver) {
        if(isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        String namespaceURI = pathFragment.getNamespaceURI();
        String prefix = pathFragment.getPrefix();
        if(prefix == null) {
            prefix = XMLConstants.EMPTY_STRING;
        }
        try {
            XMLEvent endElement = this.xmlEventFactory.createEndElement(prefix, namespaceURI, pathFragment.getLocalName());
            this.xmlEventWriter.add(endElement);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void node(Node node, NamespaceResolver resolver) {
        if(isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            if(node.getNodeType() == Node.DOCUMENT_NODE) {
                node = ((Document)node).getDocumentElement();
            }
            domToXMLEventWriter.writeToEventWriter(node, this.xmlEventWriter);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void startDocument(String encoding, String version) {
        try {
            XMLEvent startDoc = this.xmlEventFactory.createStartDocument(encoding, version, false);
            this.xmlEventWriter.add(startDoc);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    private String getString(String string) {
        return string;
    }

    public void startPrefixMapping(String prefix, String namespaceUri) {
        if(null == this.prefixMapping) {
            this.prefixMapping = new HashMap<String, String>();
        }
        this.prefixMapping.put(prefix, namespaceUri);
    }

    private void writePrefixMappings() {
        if(null != namespaceResolver) {
            String defaultNamespace = namespaceResolver.getDefaultNamespaceURI();
            if(defaultNamespace != null) {
                XMLEvent namespace = xmlEventFactory.createNamespace(XMLConstants.EMPTY_STRING, defaultNamespace);
                if(null == namespaceDeclarations) {
                    namespaceDeclarations = new ArrayList();
                }
                namespaceDeclarations.add(namespace);
            }
            for(Map.Entry<String, String> entry:this.namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                XMLEvent namespace = xmlEventFactory.createNamespace(entry.getKey(), entry.getValue());
                if(null == namespaceDeclarations) {
                    namespaceDeclarations = new ArrayList();
                }
                namespaceDeclarations.add(namespace);
            }
            namespaceResolver = null;
        }
        if(null != prefixMapping) {
            for(Map.Entry<String, String> entry:this.prefixMapping.entrySet()) {
                XMLEvent namespace = xmlEventFactory.createNamespace(entry.getKey(), entry.getValue());
                if(null == namespaceDeclarations) {
                    namespaceDeclarations = new ArrayList();
                }
                namespaceDeclarations.add(namespace);
           }
            prefixMapping = null;
        }

    }

    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
    }

    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

}