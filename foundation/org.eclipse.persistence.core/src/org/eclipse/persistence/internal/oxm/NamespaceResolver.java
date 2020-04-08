/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
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
    private static final Vector EMPTY_VECTOR = VectorUtils.emptyVector();

    private String defaultNamespaceURI;
    private NamespaceResolverStorage prefixesToNamespaces;
    private int prefixCounter;
    private Node dom;

    /**
     * Default constructor, creates a new NamespaceResolver.
     */
    public NamespaceResolver() {
        super();
    }

    /**
     * Copy Constructor
     * @since EclipseLink 2.5.0
     */
    public NamespaceResolver(NamespaceResolver namespaceResolver) {
        this.defaultNamespaceURI = namespaceResolver.defaultNamespaceURI;
        setPrefixesToNamespaces(namespaceResolver.prefixesToNamespaces);

        this.prefixCounter = namespaceResolver.prefixCounter;
        this.dom = namespaceResolver.dom;
    }

    private void setPrefixesToNamespaces(Map<String, String> input) {
        if (input == null) {
            return;
        }
        prefixesToNamespaces = new NamespaceResolverStorage(input.size());
        prefixesToNamespaces.putAll(input);
    }

    public Map<String, String> getPrefixesToNamespaces() {
        if (null == prefixesToNamespaces) {
            prefixesToNamespaces = new NamespaceResolverStorage();
        }
        return prefixesToNamespaces;
    }

    public boolean hasPrefixesToNamespaces() {
        return null != prefixesToNamespaces;
    }

    /**
     * Indicates whether given {@code prefix} is assigned to a name-space.
     * @param prefix name-space prefix
     * @return {@code true} if {@code prefix} is present in prefix to name-space map
     * ({@link #getPrefixesToNamespaces()}
     */
    public boolean hasPrefix(String prefix) {
        return null != prefixesToNamespaces ? prefixesToNamespaces.containsKey(prefix) : false;
    }

    public void setDOM(Node dom) {
        this.dom = dom;
    }

    /**
     * Returns the namespace URI associated with a specified namespace prefix
     * @param prefix The prefix to lookup a namespace URI for
     * @return The namespace URI associated with the specified prefix
     */
    @Override
    public String resolveNamespacePrefix(String prefix) {
        if (null == prefix || prefix.length() == 0) {
            return defaultNamespaceURI;
        }
        String uri = null;
        if (null != prefixesToNamespaces) {
            uri = prefixesToNamespaces.get(prefix);
        }
        if (null != uri) {
            return uri;
        } else if (javax.xml.XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return javax.xml.XMLConstants.XML_NS_URI;
        } else if (javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        if (dom != null) {
            return XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(dom, prefix);
        }
        return null;
    }

    /**
     * Return the namespace prefix associated with a namespace URI.
     * @param uri A namespace URI.
     * @return The prefix associated with the namespace URI.
     */
    public String resolveNamespaceURI(String uri) {
        if (null == uri) {
            return null;
        }
        if (null != prefixesToNamespaces) {
            for (Entry<String, String> entry : prefixesToNamespaces.entrySet()) {
                if (uri.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        if (uri.equalsIgnoreCase(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
        } else if (uri.equalsIgnoreCase(javax.xml.XMLConstants.XML_NS_URI)) {
            return javax.xml.XMLConstants.XML_NS_PREFIX;
        }
        return resolveNamespaceURI(dom, uri);
    }

    private String resolveNamespaceURI(Node node, String uri) {
        if (null == node) {
            return null;
        }

        // If the element is of the same namespace URI, then return the prefix.
        if (uri.equals(node.getNamespaceURI())) {
            return node.getPrefix();
        }

        // Check the namespace URI declarations.
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (null != namedNodeMap) {
            int namedNodeMapSize = namedNodeMap.getLength();
            for (int x = 0; x < namedNodeMapSize; x++) {
                Node attr = namedNodeMap.item(x);
                if (javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                    if (uri.equals(attr.getNodeValue())) {
                        if (attr.getLocalName() != null && (!(attr.getLocalName()
                            .equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)))) {
                            return attr.getLocalName();
                        } else {
                            return "";
                        }
                    }
                }
            }
        }

        // Repeat the process on the parent node.
        return resolveNamespaceURI(node.getParentNode(), uri);
    }

    /**
     * Adds a namespace to the collection of namespaces on the NamespaceResolver
     * @param prefix The prefix for a namespace
     * @param namespaceURI The namespace URI associated with the specified prefix
     */
    public void put(String prefix, String namespaceURI) {
        if (null == prefix || 0 == prefix.length()) {
            defaultNamespaceURI = namespaceURI;
        } else {
            //Replace same namespace with given prefix and put them to the end of list.

            //If you have prefix xmlns:oxm="namespace1" defined on the schema root,
            //and you (programmatically via namespace resolver) inject prefix xmlns:myns="namespace1" on some element (more deeply)
            //in the schema, you want this element (in the xml instance) to be prefixed with this myns (because it was defined more closely to the given element).
            //This can probably be setup in different way (declaratively maybe with some JAXB spec support or MOXy external xml mechanism).
            //This behavior is preserved, but it is now working independently on the JDK (because we are using HashMap and we are changing the order
            //of items in the LinkedHashMap so the resolver always find the prefix which is more closely (in xml schema) to the given element.
            ///@see XMLRootComplexDifferentPrefixTestCases
            List<String> removedKeys = null;
            final String cachedJvmValue = namespaceURI.intern();
            if (getPrefixesToNamespaces().containsValue(cachedJvmValue)) {
                removedKeys = new ArrayList<>();
                for (Map.Entry<String, String> prefixEntry : prefixesToNamespaces.entrySet()) {
                    if (cachedJvmValue.equals(prefixEntry.getValue())) {
                        removedKeys.add(prefixEntry.getKey());
                    }
                }
            }
            if (null != removedKeys) {
                for (String key : removedKeys) {
                    prefixesToNamespaces.remove(key);
                }
            }
            prefixesToNamespaces.put(prefix, cachedJvmValue);
            if (null != removedKeys) {
                for (String key : removedKeys) {
                    prefixesToNamespaces.put(key, cachedJvmValue);
                }
            }
        }
    }

    /**
     * Returns the list of prefixes in the NamespaceResolver
     * @return An Enumeration containing the prefixes in the NamespaceResolver
     */
    public Enumeration getPrefixes() {
        if (hasPrefixesToNamespaces()) {
            return new IteratorEnumeration(getPrefixesToNamespaces().keySet().iterator());
        } else {
            return new IteratorEnumeration(null);
        }
    }

    /**
     * INTERNAL:
     * Returns a Vector of of Namespace objects in the current Namespace Resolver
     * Used for deployment XML
     * @return A Vector containing the namespace URIs in the namespace resolver
     */
    public Vector getNamespaces() {
        if (!hasPrefixesToNamespaces()) {
            return EMPTY_VECTOR;
        }
        return prefixesToNamespaces.getNamespaces();
    }

    /**
     * INTERNAL:
     * Set the namespaces on the namespace resolver based on the specified Vector of Namespace objects
     * Used for deployment XML
     * @param names A Vector of namespace URIs
     */
    public void setNamespaces(Vector names) {
        prefixesToNamespaces = new NamespaceResolverStorage(names.size());
        prefixesToNamespaces.setNamespaces(names);
    }

    public String generatePrefix() {
        return generatePrefix(getNextPrefix());
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
        if (null != prefixesToNamespaces) {
            prefixesToNamespaces.remove(prefix);
        }
    }

    public void setDefaultNamespaceURI(String namespaceUri) {
        if (namespaceUri == null) {
            defaultNamespaceURI = null;
        } else {
            defaultNamespaceURI = namespaceUri.intern();
        }
    }

    public String getDefaultNamespaceURI() {
        if (null != defaultNamespaceURI) {
            return defaultNamespaceURI;
        } else if (dom != null) {
            return XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(dom, null);
        }
        return null;
    }

    private static class IteratorEnumeration implements Enumeration {

        private Iterator iterator;

        public IteratorEnumeration(Iterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            if (null == iterator) {
                return false;
            }
            return iterator.hasNext();
        }

        @Override
        public Object nextElement() {
            return iterator.next();
        }

    }

}
