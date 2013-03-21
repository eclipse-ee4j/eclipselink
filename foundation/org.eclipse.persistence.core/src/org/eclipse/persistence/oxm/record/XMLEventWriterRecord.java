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
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DomToXMLEventWriter;
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
             String prefix = getPrefixForFragment(xPathFragment);
             if(prefix == null) {
                 event = xmlEventFactory.createAttribute(prefix, namespaceURI, xPathFragment.getLocalName(), value);
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
         if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
             try {
                 if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(localName)) {
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
                     event = xmlEventFactory.createAttribute(Constants.EMPTY_STRING, namespaceURI, localName, value);
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
            String namespaceURI = xPathFragment.getNamespaceURI();
             if(null == namespaceURI) {
                 Iterator attributesIterator = null;
                 if(null != attributes) {
                     attributesIterator = attributes.iterator();
                 }
                 Iterator namespaceDeclarationsIterator = null;
                 if(null != namespaceDeclarations) {
                     namespaceDeclarationsIterator = namespaceDeclarations.iterator();
                 }
                 xmlEventWriter.add(xmlEventFactory.createStartElement(Constants.EMPTY_STRING, Constants.EMPTY_STRING, xPathFragment.getLocalName(), attributesIterator, namespaceDeclarationsIterator));
                 String defaultNamespace = xmlEventWriter.getNamespaceContext().getNamespaceURI(Constants.EMPTY_STRING);
                 if(defaultNamespace != null && defaultNamespace.length() > 0 ) {
                     xmlEventWriter.setDefaultNamespace(Constants.EMPTY_STRING);
                     xmlEventWriter.add(xmlEventFactory.createNamespace(Constants.EMPTY_STRING));
                 }
             } else {
                 String prefix = getPrefixForFragment(xPathFragment);
                 if(null == prefix) {
                     prefix = Constants.EMPTY_STRING;
                 }
                 Iterator attributesIterator = null;
                 if(null != attributes) {
                     attributesIterator = attributes.iterator();
                 }
                 Iterator namespaceDeclarationsIterator = null;
                 if(null != namespaceDeclarations) {
                     namespaceDeclarationsIterator = namespaceDeclarations.iterator();
                 }
                 xmlEventWriter.add(xmlEventFactory.createStartElement(prefix, namespaceURI, xPathFragment.getLocalName(), attributesIterator, namespaceDeclarationsIterator));
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
            xmlEventWriter.add(xmlEventFactory.createCData(value));
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
            xmlEventWriter.add(xmlEventFactory.createCharacters(value));
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
            String namespaceURI = frag.getNamespaceURI();
            String localName = frag.getLocalName();
            String prefix = getPrefixForFragment(xPathFragment);
            if(null == prefix) {
                prefix = Constants.EMPTY_STRING;
            }
            xmlEventWriter.add(xmlEventFactory.createStartElement(prefix, namespaceURI, localName));
            xmlEventWriter.add(xmlEventFactory.createEndElement(prefix, namespaceURI, localName));
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
            xmlEventWriter.add(xmlEventFactory.createEndDocument());
            xmlEventWriter.flush();
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endElement(XPathFragment pathFragment, NamespaceResolver namespaceResolver) {
        if(isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        String prefix = getPrefixForFragment(pathFragment);
        if(null == prefix) {
            prefix = Constants.EMPTY_STRING;
        }
        try {
            xmlEventWriter.add(xmlEventFactory.createEndElement(prefix, xPathFragment.getNamespaceURI(), pathFragment.getLocalName()));
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void node(Node node, NamespaceResolver resolver, String uri, String name) {
        if(isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            if(node.getNodeType() == Node.DOCUMENT_NODE) {
                node = ((Document)node).getDocumentElement();
            }
            domToXMLEventWriter.writeToEventWriter(node, uri, name, xmlEventWriter);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void startDocument(String encoding, String version) {
        try {
            xmlEventWriter.add(this.xmlEventFactory.createStartDocument(encoding, version, false));
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
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
                XMLEvent namespace = xmlEventFactory.createNamespace(Constants.EMPTY_STRING, defaultNamespace);
                if(null == namespaceDeclarations) {
                    namespaceDeclarations = new ArrayList();
                }
                namespaceDeclarations.add(namespace);
            }
            if(this.namespaceResolver.hasPrefixesToNamespaces()) {
                for(Map.Entry<String, String> entry:this.namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                    XMLEvent namespace = xmlEventFactory.createNamespace(entry.getKey(), entry.getValue());
                    if(null == namespaceDeclarations) {
                        namespaceDeclarations = new ArrayList();
                    }
                    namespaceDeclarations.add(namespace);
                }
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