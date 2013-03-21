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
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *  Internal:
 *  <p><b>Purpose:</b> An implementation of XMLReader for parsing XMLFragment Nodes 
 *  into SAX events.
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Walk the XMLFragment node's DOM tree and report sax events to the provided 
 *  content handler</li>
 *  <li>Report lexical events to the lexical handler if it's provided</li>
 *  
 */
public class XMLFragmentReader extends DOMReader {
    protected NamespaceResolver nsresolver;
    protected List<NamespaceResolver> nsresolverList;
    protected Map<Element, NamespaceResolver> tmpresolverMap;
    
    public XMLFragmentReader(NamespaceResolver namespaceResolver) {
        nsresolverList = new ArrayList();
        if(null != namespaceResolver) {
            nsresolverList.add(namespaceResolver);
        }
        tmpresolverMap = new HashMap<Element, NamespaceResolver>();
    }
    
    public void parse (Node node, String uri, String name) throws SAXException {
        if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
        	handleChildNodes(node.getChildNodes());
        } else {
        	super.parse(node, uri, name);
        }
    }
    protected void reportElementEvents(Element elem) throws SAXException {
        super.reportElementEvents(elem);
        // Clean up any temporary namespace resolvers created while processing this element
        cleanupNamespaceResolvers(elem);
    }

    /**
     * Trigger an endDocument event on the contenthandler.
     */
    protected void endDocument() throws SAXException {
        // NOT APPLICABLE FOR FRAGMENTS - DO NOTHING
    }

    /**
     * Trigger a startDocument event on the contenthandler.
     */
    protected void startDocument() throws SAXException {
        // NOT APPLICABLE FOR FRAGMENTS - DO NOTHING
    }
    
    /**
     * Handle XMLNS prefixed attribute such that they are not written out
     * during the content handler's attribute event.
     * 
     * @param prefix
     * @param localName
     * @param value
     */
    protected void handleXMLNSPrefixedAttribute(Element elem, Attr attr) {
        String uri = resolveNamespacePrefix(elem.getPrefix());
        if (uri == null || !uri.equals(attr.getLocalName())) {
            NamespaceResolver tmpresolver = getTempResolver(elem);
            tmpresolver.put(attr.getLocalName(), attr.getValue());
            if (!nsresolverList.contains(tmpresolver)) {
                nsresolverList.add(tmpresolver);
            }
        }
    }
    
    /**
     * Handle prefixed attribute - may need to declare the namespace 
     * URI locally.
     * 
     */
    protected void handlePrefixedAttribute(Element elem) throws SAXException {
        // Need to determine if the URI for the prefix needs to be 
        // declared locally:
        // If the prefix or URI are not in the resolver, or the URI
        // associated with the prefix (in the resolver) is different
        // than node's URI, write out the node's URI locally
        String prefix = elem.getPrefix();
        if(prefix == null) {
           prefix = Constants.EMPTY_STRING;
        }
        String uri = resolveNamespacePrefix(prefix);
        if(prefix == Constants.EMPTY_STRING && uri == null) {
            return;
        }
        if (uri == null || !uri.equals(elem.getNamespaceURI())) {
            NamespaceResolver tmpresolver = getTempResolver(elem);
            tmpresolver.put(prefix, elem.getNamespaceURI());
            if (!nsresolverList.contains(tmpresolver)) {
                nsresolverList.add(tmpresolver);
            }
            getContentHandler().startPrefixMapping(prefix, elem.getNamespaceURI());
        }
    }
    
    /**
     * If there is a temporary namespace resolver for a given element,
     * each entry contains a prefix that requires an endPrefixMapping
     * event to be triggered
     */
    protected void endPrefixMappings(Element elem) throws SAXException {
        NamespaceResolver tmpresolver = getTempResolver(elem);
        if (tmpresolver != null) {
            Enumeration<String> prefixes = tmpresolver.getPrefixes();  
            while (prefixes.hasMoreElements()) {
                getContentHandler().endPrefixMapping(prefixes.nextElement());
            }
        }
    }

    /**
     * Returns the namespace resolver in the map of temporary namespace
     * resolvers for a given element
     * 
     * @param elem
     * @return the namespace resolver in the map for elem, or a new 
     * resolver if none exists
     */
    protected NamespaceResolver getTempResolver(Element elem) {
        NamespaceResolver tmpresolver = tmpresolverMap.get(elem);
        if (tmpresolver == null) {
            tmpresolver = new NamespaceResolver();
            tmpresolverMap.put(elem, tmpresolver);
        }
        return tmpresolver;
    }
    
    /**
     * Remove any temporary namespace resolvers created while processing
     * a given element.
     * 
     * @param elem
     */
    protected void cleanupNamespaceResolvers(Element elem) {
        NamespaceResolver tmpresolver = tmpresolverMap.get(elem);
        if (tmpresolver != null) {
            tmpresolverMap.remove(elem);
            nsresolverList.remove(tmpresolver);
        }
    }

    /**
     * Convenience method that iterates over each namespace resolver 
     * in the resolver list until it locates a uri for 'prefix' or
     * the final resolver is reached w/o success.
     * @param prefix
     * @return true if a URI exists in one of the resolvers in the 
     * list, false otherwise
     */
    protected String resolveNamespacePrefix(String prefix) {
        String uri = null;
        if (null == prefix) {
            for (int i = nsresolverList.size() - 1; i >= 0; i--) {
                NamespaceResolver next = nsresolverList.get(i);
                uri = next.getDefaultNamespaceURI();
                if ((uri != null) && uri.length() > 0) {
                    break;
                }
            }
        } else {
            for (int i = nsresolverList.size() - 1; i >= 0; i--) {
                NamespaceResolver next = nsresolverList.get(i);
                uri = next.resolveNamespacePrefix(prefix);
                if ((uri != null) && uri.length() > 0) {
                    break;
                }
            }
        }
        return uri;
    }  
    /**
     * Process namespace declarations on parent elements if not the root.
     * For each parent node from current to root place puch each onto a 
     * stack, then pop each off, calling startPrefixMapping for each 
     * XMLNS attribute.  Using a stack ensures that the parent nodes are
     * processed top down.
     * 
     * @param element
     */
    protected void processParentNamespaces(Element element) throws SAXException {
        // DO NOTHING FOR FRAGMENTS
    }
    
    protected void handleXsiTypeAttribute(Attr attr) throws SAXException {
        String value = attr.getValue();
        int colon = value.indexOf(':');
        if(colon != -1) {
            String prefix = value.substring(0, colon);
            String uri = this.resolveNamespacePrefix(prefix);
            if(uri == null) {
                uri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(attr.getOwnerElement(), prefix);
                if(uri != null) {
                    this.contentHandler.startPrefixMapping(prefix, uri);
                }
                
            }
        }
    }    
}
