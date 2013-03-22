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
package org.eclipse.persistence.jaxb.compiler;

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
    
    public NamespaceResolver getNamespaceResolverForDescriptor() {
        if(this.namespaceResolverForDescriptor == null) {
            this.namespaceResolverForDescriptor = new NamespaceResolver();
            if(this.namespaceResolver.hasPrefixesToNamespaces()) {
                for(String next:this.namespaceResolver.getPrefixesToNamespaces().keySet()) {
                    this.namespaceResolverForDescriptor.put(next, this.namespaceResolver.resolveNamespacePrefix(next));
                }
            }
            this.namespaceResolverForDescriptor.setDefaultNamespaceURI(namespaceResolver.getDefaultNamespaceURI());
        }
        return this.namespaceResolverForDescriptor;
    }
}
