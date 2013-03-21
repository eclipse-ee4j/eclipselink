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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a
 * ContentHandler.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * ContentHandlerRecord contentHandlerRecord = new ContentHandlerRecord();<br>
 * marshalRecord.setContentHandler(myContentHandler);<br>
 * xmlMarshaller.marshal(myObject, contentHandlerRecord);<br>
 * </code></p>
 * <p>If the marshal(ContentHandler) method is called on XMLMarshaller, then the
 * ContentHanlder is automatically wrapped in a ContentHandlerRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller.marshal(myObject, contentHandler);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class ContentHandlerRecord extends MarshalRecord {
    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;
    private XPathFragment xPathFragment;
    private AttributesImpl attributes;
    List<String> currentLevelPrefixMappings;
    private List<List<String>> prefixMappings;

    public ContentHandlerRecord() {
        prefixMappings = new ArrayList<List<String>>();
        currentLevelPrefixMappings = null;
        attributes = new AttributesImpl();
    }

    // bug#5035551 - content handler record will act more like writer 
    // record in that startElement is called with any attributes that
    // are to be written to the element.  So, instead of calling 
    // openStartElement > attribute > closeStartElement, we'll gather
    // any required attributes and make a single call to openAndCloseStartElement.
    // This is necessary as the contentHandler.startElement() call results in
    // a completed element the we cannot add attributes to after the fact.
    protected boolean isStartElementOpen = false;

    /**
     * Return the ContentHandler that the object will be marshalled to.
     * @return The marshal target.
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /**
     * Set the ContentHandler that the object will be marshalled to.
     * @param contentHandler The marshal target.
     */
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    /**
     * Set the LexicalHandler to receive CDATA related events
     */
    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    /**
     * INTERNAL:
     */
    public void startDocument(String encoding, String version) {
        try {
            contentHandler.startDocument();
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
        try {
            contentHandler.endDocument();
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    @Override
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    public void startPrefixMapping(String prefix, String namespaceURI) {
        try {
            contentHandler.startPrefixMapping(prefix, namespaceURI);
            if(null == currentLevelPrefixMappings) {
                currentLevelPrefixMappings = new ArrayList<String>();
            }
            currentLevelPrefixMappings.add(prefix);
            
            
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     * Add the namespace declarations to the XML document.
     * @param namespaceResolver The NamespaceResolver contains the namespace
     * prefix and URI pairings that need to be declared.
     */
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
        if (namespaceResolver == null) {
            return;
        }
        String namespaceURI = namespaceResolver.getDefaultNamespaceURI();
        if(null != namespaceURI) {        	
        	defaultNamespaceDeclaration(namespaceURI);
        }

        if(namespaceResolver.hasPrefixesToNamespaces()) {
            for(Entry<String, String> entry: namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                String namespacePrefix = entry.getKey();                
                namespaceDeclaration(namespacePrefix,  entry.getValue());
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void endPrefixMapping(String prefix) {
        try {
            contentHandler.endPrefixMapping(prefix);
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     * 
     * Create a start element tag - this call results in a complete start element, 
     * i.e. closeStartElement() does not need to be called after a call to this 
     * method.
     * 
     */
    private void openAndCloseStartElement() {
        try {
            String namespaceUri = xPathFragment.getNamespaceURI();
            if(namespaceUri == null) {
                namespaceUri = Constants.EMPTY_STRING;
            }
            contentHandler.startElement(namespaceUri, xPathFragment.getLocalName(), getNameForFragment(xPathFragment), attributes);
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        currentLevelPrefixMappings = null;
        prefixMappings.add(currentLevelPrefixMappings);
                
        if (isStartElementOpen) {
            openAndCloseStartElement();
        }
        this.isStartElementOpen = true;
        this.xPathFragment = xPathFragment;
        this.attributes.clear();
        
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        if (isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            this.attributes.clear();
            String namespaceURI = frag.getNamespaceURI();
            if(namespaceURI == null) {
                namespaceURI = Constants.EMPTY_STRING;
            }
            String localName = frag.getLocalName();
            String shortName = getNameForFragment(frag);
            contentHandler.startElement(namespaceURI, localName, shortName, attributes);
            contentHandler.endElement(namespaceURI, localName, shortName);
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        String namespaceURI = resolveNamespacePrefix(xPathFragment, namespaceResolver);
        attribute(namespaceURI, xPathFragment.getLocalName(), getNameForFragment(xPathFragment), value);
    }

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        if(namespaceURI == javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI) {
            if(localName == javax.xml.XMLConstants.XMLNS_ATTRIBUTE) {
                localName = "";
            }
            this.startPrefixMapping(localName, value);
        }        
        attributes.addAttribute(namespaceURI, localName, qName, Constants.CDATA, value);
    }

    /**
     * INTERNAL:
     */
    public void closeStartElement() {
        // do nothing - the openAndCloseStartElement call results in a 
        // complete start element
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        if (isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            String uri = xPathFragment.getNamespaceURI();
            if(uri == null) {
                uri = Constants.EMPTY_STRING;
            }
            contentHandler.endElement(uri, xPathFragment.getLocalName(), getNameForFragment(xPathFragment));
            List<String> currentLevelPrefixMappings = prefixMappings.remove(prefixMappings.size()-1);
            if(null != currentLevelPrefixMappings) {
                for(String prefix : currentLevelPrefixMappings) {
                    contentHandler.endPrefixMapping(prefix);
                }
            }
            isStartElementOpen = false;
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        if (isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            char[] characters = value.toCharArray();
            contentHandler.characters(characters, 0, characters.length);
        } catch (SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void cdata(String value) {
        //No specific support for CDATA in a ContentHandler. Just treat as regular
        //Character data as a SAX parser would.
        if (isStartElementOpen) {
            openAndCloseStartElement();
            isStartElementOpen = false;
        }
        try {
            if(lexicalHandler != null) {
                lexicalHandler.startCDATA();
            }
            characters(value);
            if(lexicalHandler != null) {
                lexicalHandler.endCDATA();
            }
        } catch(SAXException ex) {
            throw XMLMarshalException.marshalException(ex);
        }
    }

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
    public void node(Node node, NamespaceResolver namespaceResolver, String uri, String name) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (getNamespaceResolver() != null) {
                resolverPfx = this.getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
            } 
            String namespaceURI = attr.getNamespaceURI();
            String localName = attr.getLocalName();
            if(localName == null) {
                localName = Constants.EMPTY_STRING;
            }
            // If the namespace resolver contains a prefix for the attribute's URI,
            // use it instead of what is set on the attribute
            if (resolverPfx != null) {
                attribute(namespaceURI, localName, resolverPfx+Constants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(namespaceURI, localName, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (namespaceURI != null) {
                    attribute(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName , javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else {
            if (isStartElementOpen) {
                openAndCloseStartElement();
                isStartElementOpen = false;
            }
            if (node.getNodeType() == Node.TEXT_NODE) {
                characters(node.getNodeValue());
            } else {
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(contentHandler);
                try {
                    xfragReader.parse(node, uri, name);
                } catch (SAXException sex) {
                    throw XMLMarshalException.marshalException(sex);
                }
            }
        }
    }

    public String resolveNamespacePrefix(XPathFragment frag, NamespaceResolver resolver) {
        String resolved = frag.getNamespaceURI();
        if (resolved == null) {
            return Constants.EMPTY_STRING;
        }
        return resolved;
    }

    public String resolveNamespacePrefix(String s) {
        String resolved = super.resolveNamespacePrefix(s);
        if (resolved == null) {
            return Constants.EMPTY_STRING;
        }
        return resolved;
    }

}