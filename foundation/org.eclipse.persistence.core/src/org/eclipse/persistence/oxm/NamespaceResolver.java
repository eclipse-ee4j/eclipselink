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
package org.eclipse.persistence.oxm;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <p >It is common for an XML document to include one or more namespaces.
 * TopLink supports this using its NamespaceResolver. The namespace resolver maintains
 * pairs of namespace prefixes and URIs. TopLink uses these prefixes in conjunction with the
 * XPath statements you specify on EIS mappings to XML records and XML mappings.
 *
 * <p>Although TopLink captures namespace prefixes in the XPath statements for mappings (if applicable),
 * the input document is not required to use the same namespace prefixes. TopLink will use the namespace
 * prefixes specified in the mapping when creating new documents.
 *
 * <p><em>Code Sample</em><br>
 * <code>
 *  <b>NamespaceResolver resolver = new NamespaceResolver();<br>
 *  resolver.put(    "ns", "urn:namespace-example");<br><br></b>
 *
 *  XMLDescriptor descriptor = new XMLDescriptor();<br>
 *  descriptor.setJavaClass(Customer.class); <br>
 *  descriptor.setDefaultRootElement("<b>ns</b>:customer");<br>
 *  descriptor.setNamespaceResolver(resolver);<br><br>
 *
 *  XMLDirectMapping mapping = new XMLDirectMapping();<br>
 *  mapping.setAttributeName("id");<br>
 *  mapping.setXPath("<b>ns</b>:id/text()");<br>
 *  descriptor.addMapping(mapping);
 *  </code>
 *
 *  @see org.eclipse.persistence.oxm.XMLDescriptor
 *  @see org.eclipse.persistence.eis.EISDescriptor
 *
 */
public class NamespaceResolver implements XMLNamespaceResolver {
    private static final String BASE_PREFIX = "ns";

    private String defaultNamespaceURI;
    private Properties namespaces;
    int prefixCounter;
    private Node dom;

    /**
    * Default constructor, creates a new NamespaceResolver.
     */
    public NamespaceResolver() {
        super();
        namespaces = new Properties();
    }

    public void setDOM(Node dom) {
        this.dom = dom;
    }

    /**
    * Returns the namespace URI associated with a specified namespace prefix
     *
     * @param  prefix The prefix to lookup a namespace URI for
     * @return The namespace URI associated with the specified prefix
     */
    public String resolveNamespacePrefix(String prefix) {
        if (null == prefix) {
            return defaultNamespaceURI;
        }
        String uri = namespaces.getProperty(prefix);
        if ((uri == null) && prefix.equals(XMLConstants.XML_NAMESPACE_PREFIX)) {
            uri = XMLConstants.XML_NAMESPACE_URL;
        }
        return uri;
    }

    /**
     * Return the namespace prefix associated with a namespace URI.
     * @param uri A namespace URI.
     * @return The prefix associated with the namespace URI.
     */
    public String resolveNamespaceURI(String uri) {
        if(null == uri) {
            return null;
        }

        Enumeration keys = namespaces.keys();
        String prefix;
        while (keys.hasMoreElements()) {
            prefix = (String)keys.nextElement();
            if (namespaces.getProperty(prefix).equals(uri)) {
                return prefix;
            }
        }
        if (uri.equalsIgnoreCase(XMLConstants.XMLNS_URL)) {
            return XMLConstants.XMLNS;
        } else if (uri.equalsIgnoreCase(XMLConstants.XML_NAMESPACE_URL)) {
            return XMLConstants.XML_NAMESPACE_PREFIX;
        }
        return resolveNamespaceURI(dom, uri);
    }

    private String resolveNamespaceURI(Node node, String uri) {
        if(null == node) {
            return null;
        }
        
        // If the element is of the same namespace URI, then return the prefix.
        if(uri.equals(node.getNamespaceURI())) {
            return node.getPrefix();
        } 
        
        // Check the namespace URI declarations.
        NamedNodeMap namedNodeMap = node.getAttributes();
        if(null != namedNodeMap) {
            int namedNodeMapSize = namedNodeMap.getLength();
            for(int x=0; x<namedNodeMapSize; x++) {
                Node attr = namedNodeMap.item(x);
                if(XMLConstants.XMLNS_URL.equals(attr.getNamespaceURI())) {
                    if(uri.equals(attr.getNodeValue())) {
                        return attr.getLocalName();
                    }
                }
            }
        }

        // Repeat the process on the parent node.
        return resolveNamespaceURI(node.getParentNode(), uri);
    }

    /**
     * Adds a namespace to the collection of namespaces on the NamespaceResolver
     *
     * @param  prefix  The prefix for a namespace
     * @param  namespaceURI  The namespace URI associated with the specified prefix
    */
    public void put(String prefix, String namespaceURI) {
        namespaces.setProperty(prefix, namespaceURI);
    }

    /**
    * Returns the list of prefixes in the NamespaceResolver
     *
     * @return An Enumeration containing the prefixes in the NamespaceResolver
     */
    public Enumeration getPrefixes() {
        return namespaces.keys();
    }

    /**
    * INTERNAL:
     * Returns a Vector of of Namespace objects in the current Namespace Resolver
     * Used for deployment XML
     * @return  A Vector containing the namespace URIs in the namespace resolver
     */
    public Vector getNamespaces() {
        Vector names = new Vector(namespaces.size());
        for (Enumeration sources = namespaces.keys(); sources.hasMoreElements();) {
            String prefix = (String)sources.nextElement();
            String URI = (String)namespaces.get(prefix);
            Namespace namespace = new Namespace(prefix, URI);
            names.addElement(namespace);
        }
        return names;
    }

    /**
    * INTERNAL:
     * Set the namespaces on the namespace resolver based on the specified Vector of Namespace objects
     * Used for deployment XML
     * @param  names A Vector of namespace URIs
     */
    public void setNamespaces(Vector names) {
        namespaces = new Properties();
        for (Enumeration sources = names.elements(); sources.hasMoreElements();) {
            Namespace namespace = (Namespace)sources.nextElement();
            if ((namespace.getPrefix() != null) && (namespace.getNamespaceURI() != null)) {
                namespaces.put(namespace.getPrefix(), namespace.getNamespaceURI());
            }
        }
    }

    public String generatePrefix() {
        String generatedPrefix = getNextPrefix();
        return generatePrefix(generatedPrefix);
    }

    private String getNextPrefix() {
        return BASE_PREFIX + prefixCounter++;
    }

    public String generatePrefix(String defaultPrefix) {
        String lookup = resolveNamespacePrefix(defaultPrefix);
        while (lookup != null) {
            defaultPrefix = getNextPrefix();
            lookup = resolveNamespacePrefix(defaultPrefix);
        }
        return defaultPrefix;
    }

    public void removeNamespace(String prefix) {
        namespaces.remove(prefix);
    }
    
    public void setDefaultNamespaceURI(String namespaceUri) {
        defaultNamespaceURI = namespaceUri;
    }
    
    public String getDefaultNamespaceURI() {
        return defaultNamespaceURI;
    }
}
