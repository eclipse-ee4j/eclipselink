/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import java.util.Map;
import java.util.Stack;
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
import org.w3c.dom.Text;
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
    private Stack nodes;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public NodeRecord() {
        super();
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        document = xmlPlatform.createDocument();
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
        if (parent instanceof Document) {
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
    public NodeRecord(Element element) {
        this();
        setDOM(element);
    }

    /**
     * INTERNAL:
     * Create a record with the element.
     */
    public NodeRecord(Document document) {
        this();
        setDOM(document.getDocumentElement());
    }

    public String getLocalName() {
        return getNode().getLocalName();
    }

    public String getNamespaceURI() {
        return getNode().getNamespaceURI();
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
        return (Element)nodes.peek();
    }

    private Node getNode() {
        return (Node)nodes.peek();
    }

    /**
     * Set the Node that the object will be marshalled to.
     * @param writer The marshal target.
     */
    public void setDOM(Node dom) {
        nodes = new Stack();
        if (dom.getNodeType() == Node.DOCUMENT_NODE) {
            document = (Document)dom;
        } else if ((dom.getNodeType() == Node.ELEMENT_NODE) || (dom.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)) {
            document = dom.getOwnerDocument();
            nodes.push(dom);
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
                attribute(attr.getNamespaceURI(), "", resolverPfx+":"+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), "", attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(XMLConstants.XMLNS_URL, "",XMLConstants.XMLNS + ":" + attr.getPrefix(), attr.getNamespaceURI());
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
        String namespaceURI = resolveNamespacePrefix(xPathFragment, namespaceResolver);
        Element element = getDocument().createElementNS(namespaceURI, xPathFragment.getShortName());
        try {
            getNode().appendChild(element);
            nodes.push(element);
        } catch (Exception e) {
            document.appendChild(element);
            setDOM(element);
            nodes.push(element);
        }
    }

    /**
     * INTERNAL:
     */
    public void element(String namespaceURI, String localName, String qName) {
        Element element = getDocument().createElementNS(namespaceURI, qName);
        getNode().appendChild(element);
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        String namespaceURI = resolveNamespacePrefix(xPathFragment, namespaceResolver);
        if (getNode().getNodeType() == Node.ELEMENT_NODE) {
            getDOM().setAttributeNS(namespaceURI, xPathFragment.getShortName(), value);
        }
    }

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        if (getNode().getNodeType() == Node.ELEMENT_NODE) {
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
        nodes.pop();
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        if (value.length() > 0) {
            Text text = getDocument().createTextNode(value);
            getNode().appendChild(text);
        }
    }
    
    public void cdata(String value) {
        CDATASection cdata = getDocument().createCDATASection(value);
        getNode().appendChild(cdata);
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
                return namespaceResolver.resolveNamespacePrefix("");
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
                element = getDocument().createElement(qName);
            } else {
                element = getDocument().createElementNS(namespaceURI, qName);
            }
            
            try {
                getNode().appendChild(element);
                nodes.push(element);
            } catch (Exception e) {
                document.appendChild(element);
                setDOM(element);
                nodes.push(element);
            }
            // Handle attributes
            for (int i = 0; i < atts.getLength(); i++) {
                marshalRecord.attribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
            }
            // Handle prefix mappings
            if (!prefixMappings.isEmpty()) {
                for (java.util.Iterator<String> keys = prefixMappings.keySet().iterator(); keys.hasNext();) {
                    String prefix = keys.next();
                    element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + prefix, prefixMappings.get(prefix));
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
