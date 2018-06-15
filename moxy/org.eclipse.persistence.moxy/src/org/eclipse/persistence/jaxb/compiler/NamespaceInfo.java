/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.compiler;

import java.util.Map;

import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 *  INTERNAL:
 *  <p><b>Purpose:</b>To store some information about a schema's target namespace and some additional
 *  information gathered from XmlSchema annotation at the package (namespace) level
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Store target namespace and namespace prefix information for a specific schema</li>
 *  <li>Store some additional Schema information (such as element/attribute form and XmlAccessType)</li>
 *  </ul>
 *
 *  @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 *  @author mmacivor
 *  @since Oracle TopLink 11.1.1.0.0
 */

public class NamespaceInfo {
    private String namespace;
    private boolean attributeFormQualified = false;
    private boolean elementFormQualified = false;
    private NamespaceResolver namespaceResolver;
    private String location;
    private NamespaceResolver namespaceResolverForDescriptor;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String ns) {
        if(ns != null) {
            ns = ns.intern();
        }
        this.namespace = ns;
    }

    public boolean isAttributeFormQualified() {
        return attributeFormQualified;
    }

    public void setAttributeFormQualified(boolean b) {
        attributeFormQualified = b;
    }

    public boolean isElementFormQualified() {
        return elementFormQualified;
    }

    public void setElementFormQualified(boolean b) {
        elementFormQualified = b;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public void setNamespaceResolver(NamespaceResolver resolver) {
        namespaceResolver = resolver;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /** Provides a {@link NamespaceResolver} resolver for Descriptor.
     * <p>
     * The returned {@link NamespaceResolver} is consistent with {@code contextResolver}.
     * Should there be any clashes in prefix or default name-space assignments,
     * these will be re-mapped to another prefix in the resulting resolver.
     * Alongside, all new prefix or default name-space assignments are added to the {@code contextResolver}.
     * <p>
     * <b>IMPORTANT</b>: The first result is cached and re-used since then,
     * even if later calls are with different {@code contextResolver}.
     *
     * @param contextResolver context resolver
     * @param canUseDefaultNamespace indicates whether default name-space can be used
     * @return {@link NamespaceResolver}
     *
     * @throws NullPointerException if {@code contextResolver} is {@code null}
     */
    public NamespaceResolver getNamespaceResolverForDescriptor(
            NamespaceResolver contextResolver,
            boolean canUseDefaultNamespace) {
        if(this.namespaceResolverForDescriptor == null) {
            this.namespaceResolverForDescriptor = new NamespaceResolver();
            // initialize
            // prefixed name-spaces
            if(this.namespaceResolver.hasPrefixesToNamespaces()) {
                for(Map.Entry<String, String> entry: this.namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                    final String namespace = entry.getValue();
                    if (namespace != null) {
                        addToDescriptorNamespaceResolver(false, entry.getKey(), namespace, contextResolver);
                    }
                }
            }
            // default name-space
            final String defaultNS = this.namespaceResolver.getDefaultNamespaceURI();
            if (defaultNS != null) {
                boolean isDefault = canUseDefaultNamespace || defaultNS.equals(namespace);
                addToDescriptorNamespaceResolver(isDefault, null, defaultNS, contextResolver);
            }
        }
        return this.namespaceResolverForDescriptor;
    }

    private void addToDescriptorNamespaceResolver(
            boolean asDefault,
            String prefix,
            String namespace,
            NamespaceResolver contextResolver) {
        String contextDefault = contextResolver.getDefaultNamespaceURI();
        if (asDefault && (contextDefault == null || contextDefault.equals(namespace))) {
            this.namespaceResolverForDescriptor.setDefaultNamespaceURI(namespace);
            contextResolver.setDefaultNamespaceURI(namespace);
        } else {
            String newPrefix = prefix == null
                    || contextResolver.hasPrefix(prefix)
                    || namespaceResolverForDescriptor.hasPrefix(prefix)
                ? getNamespacePrefixForDescriptorNamespaceResolver(namespace, contextResolver)
                : prefix;
            this.namespaceResolverForDescriptor.put(newPrefix, namespace);
            contextResolver.put(newPrefix, namespace);
        }
    }

    private String getNamespacePrefixForDescriptorNamespaceResolver(String namespace, NamespaceResolver contextResolver) {
        String prefix = contextResolver.resolveNamespaceURI(namespace);
        while (prefix == null
                || namespaceResolverForDescriptor.hasPrefix(prefix)) {
            prefix = contextResolver.generatePrefix();
        }
        return prefix;
    }
}
