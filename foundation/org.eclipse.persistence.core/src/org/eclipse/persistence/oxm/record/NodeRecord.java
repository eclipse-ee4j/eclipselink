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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.record;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.MarshalRecordContentHandler;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a Node.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * NodeRecord nodeRecord = new NodeRecord();<br>
 * nodeRecord.setDOM(myNode);<br>
 * xmlMarshaller.marshal(myObject, nodeRecord);<br>
 * </code></p>
 * <p>If the marshal(Node) method is called on XMLMarshaller, then the Writer is
 * automatically wrapped in a NodeRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller.marshal(myObject, myNode);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class NodeRecord extends MarshalRecord {
    private Document document;
    private Node node;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public NodeRecord() {
        super();
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        document = xmlPlatform.createDocument();
        node = document;
    }

    /**
     * INTERNAL:
     * Create a record with the root element name.
     */
    public NodeRecord(String rootElementName) {
        this(rootElementName, (NamespaceResolver)null);
    }

    /**
     * INTERNAL:
     * Create a record with the root element name get the namespace URI from the namespaceResolver.
     */
    public NodeRecord(String rootElementName, NamespaceResolver namespaceResolver) {
        this();
        String rootElementNamespaceURI = resolveNamespace(namespaceResolver, rootElementName);
        Node rootElement = document.createElementNS(rootElementNamespaceURI, rootElementName);
        document.appendChild(rootElement);
        setDOM(rootElement);
    }

    /**
     * INTERNAL:
     * Create a record with the local root element name, that is a child of the parent.
     */
    public NodeRecord(String localRootElementName, Node parent) {
        this(localRootElementName, (NamespaceResolver)null, parent);
    }

    /**
     * INTERNAL:
     * Create a record with the local root element name, that is a child of the parent.
     * Lookup the namespace URI from the namespaceResolver.
     */
    public NodeRecord(String localRootElementName, NamespaceResolver namespaceResolver, Node parent) {
        this();
        Document document;
        if (parent.getNodeType() == Node.DOCUMENT_NODE) {
            document = (Document)parent;
        } else {
            document = parent.getOwnerDocument();
        }

        String localRootElementNamespaceURI = resolveNamespace(namespaceResolver, localRootElementName);
        Element child = document.createElementNS(localRootElementNamespaceURI, localRootElementName);
        parent.appendChild(child);
        setDOM(child);
    }

    /**
     * INTERNAL:
     * Create a record with the element.
     */
    public NodeRecord(Node node) {
        setDOM(node);
    }

    public String getLocalName() {
        return node.getLocalName();
    }

    public String getNamespaceURI() {
        return node.getNamespaceURI();
    }

    public void clear() {
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Return the Node that the object will be marshalled to.
     * @return The marshal target.
     */
    public Element getDOM() {
        return (Element) node;
    }

    /**
     * Set the Node that the object will be marshalled to.
     * @param writer The marshal target.
     */
    public void setDOM(Node dom) {
        int nodeType = dom.getNodeType();
        if (Node.DOCUMENT_NODE == nodeType) {
            document = (Document)dom;
            node = dom;
        } else if (Node.ELEMENT_NODE == nodeType || Node.DOCUMENT_FRAGMENT_NODE == nodeType) {
            document = dom.getOwnerDocument();
            node = dom;
            getNamespaceResolver().setDOM(dom);
        } else {
            throw XMLMarshalException.marshalException(null);
        }
    }

    public String transformToXML() {
        return null;
    }

    /**
     * INTERNAL:
     */
    public void startDocument(String encoding, String version) {}

    /**
     * INTERNAL:
     */
    public void endDocument() {}

    public void node(Node node, NamespaceResolver namespaceResolver) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (namespaceResolver != null) {
                resolverPfx = namespaceResolver.resolveNamespaceURI(attr.getNamespaceURI());
            }
            // If the namespace resolver contains a prefix for the attribute's URI,
            // use it instead of what is set on the attribute
            if (resolverPfx != null) {
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, resolverPfx+ XMLConstants.COLON +attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(XMLConstants.XMLNS_URL, XMLConstants.EMPTY_STRING,XMLConstants.XMLNS + XMLConstants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            characters(node.getNodeValue());
        } else {
            NodeRecordContentHandler mrcHdlr = new NodeRecordContentHandler(this, namespaceResolver);
            XMLFragmentReader xfRdr = new XMLFragmentReader(namespaceResolver);
            xfRdr.setContentHandler(mrcHdlr);
            try {
                xfRdr.parse(node);
            } catch (SAXException sex) {
                // Do nothing.
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        Element element = document.createElementNS(xPathFragment.getNamespaceURI(), getNameForFragment(xPathFragment));
        node = node.appendChild(element);
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        Element element = document.createElementNS(frag.getNamespaceURI(), getNameForFragment(frag));
        node.appendChild(element);
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            getDOM().setAttributeNS(xPathFragment.getNamespaceURI(), getNameForFragment(xPathFragment), value);
        }
    }

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            getDOM().setAttributeNS(namespaceURI, qName, value);
        }
    }

    /**
     * INTERNAL:
     */
    public void closeStartElement() {
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        node = node.getParentNode();
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        if (value.length() > 0) {
            node.appendChild(document.createTextNode(value));
        }
    }

    public void cdata(String value) {
        CDATASection cdata = document.createCDATASection(value);
        node.appendChild(cdata);
    }

    /**
     * INTERNAL:
     * Return the namespace uri for the prefix of the given local name
     */
    private String resolveNamespace(NamespaceResolver namespaceResolver, String localName) {
        int colonIndex = localName.indexOf(':');
        if (colonIndex < 0) {
            // handle target/default namespace
            if (namespaceResolver != null) {
                return namespaceResolver.resolveNamespacePrefix(XMLConstants.EMPTY_STRING);
            }
            return null;
        } else {
            if (namespaceResolver == null) {
                //throw an exception if the name has a : in it but the namespaceresolver is null
                throw XMLMarshalException.namespaceResolverNotSpecified(localName);
            }
            String prefix = localName.substring(0, colonIndex);
            String uri = namespaceResolver.resolveNamespacePrefix(prefix);
            if (uri == null) {
                //throw an exception if the prefix is not found in the namespaceresolver
                throw XMLMarshalException.namespaceNotFound(prefix);
            }
            return uri;
        }
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * This class will typically be used in conjunction with an XMLFragmentReader.
     * The XMLFragmentReader will walk a given XMLFragment node and report events
     * to this class - the event's data is then used to create required attributes
     * and elements which are appended to the the enclosing class' document.
     *
     * @see org.eclipse.persistence.internal.oxm.record.XMLFragmentReader
     */
    protected class NodeRecordContentHandler extends MarshalRecordContentHandler {
        Map<String, String> prefixMappings;

        public NodeRecordContentHandler(NodeRecord nRec, NamespaceResolver resolver) {
            super(nRec, resolver);
            prefixMappings = new HashMap<String, String>();
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            Element element;
            if (namespaceURI == null) {
                element = document.createElement(qName);
            } else {
                element = document.createElementNS(namespaceURI, qName);
            }

            node = node.appendChild(element);
            // Handle attributes
            for (int i = 0; i < atts.getLength(); i++) {
                marshalRecord.attribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
            }
            // Handle prefix mappings
            if (!prefixMappings.isEmpty()) {
                for (Iterator<Map.Entry<String, String>> entries = prefixMappings.entrySet().iterator(); entries.hasNext();) {
                    Map.Entry<String, String> entry = entries.next();
                    String namespaceDeclarationPrefix = entry.getKey();
                    if(null == namespaceDeclarationPrefix || 0 == namespaceDeclarationPrefix.length()) {
                        String namespaceDeclarationURI = entry.getValue();
                        if(null == namespaceDeclarationURI) {
                            element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS , XMLConstants.EMPTY_STRING);
                        } else {
                            element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS , namespaceDeclarationURI);
                        }
                    } else {
                        element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + entry.getKey(), entry.getValue());
                    }
                }
                prefixMappings.clear();
            }
            marshalRecord.closeStartElement();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            String namespaceUri = getNamespaceResolver().resolveNamespacePrefix(prefix);
            if(namespaceUri == null || !namespaceUri.equals(uri)) {
                prefixMappings.put(prefix, uri);
            }
        }
    }

}