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
 *     minorman - May 2008: helpful superclass that handles Namespaces for project-Project's
 ******************************************************************************/

package org.eclipse.persistence.internal.sessions.factories;

//javase imports
import java.util.Iterator;

import javax.xml.namespace.QName;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public abstract class NamespaceResolvableProject extends Project {

    public static final String ECLIPSELINK_PREFIX = 
        "eclipselink";
    public static final String ECLIPSELINK_NAMESPACE = 
        "http://www.eclipse.org/eclipselink/xsds/persistence";
    public static final String TOPLINK_PREFIX = 
        "toplink";
    public static final String TOPLINK_NAMESPACE = 
        "http://xmlns.oracle.com/ias/xsds/toplink";
    public static final String OPM_PREFIX = 
        "opm";
    public static final String OPM_NAMESPACE = 
        "http://xmlns.oracle.com/ias/xsds/opm";
    
    protected NamespaceResolverWithPrefixes ns;
    protected QName fieldQname;
    
    public NamespaceResolvableProject() {
        super();
        fieldQname = new QName(getSecondaryNamespace(), "field");
        buildNamespaceResolver();
        buildDescriptors();
        setNamespaceResolverOnDescriptors();
    }
    public NamespaceResolvableProject(NamespaceResolverWithPrefixes ns) {
        super();
        fieldQname = new QName(getSecondaryNamespace(), "field");
        this.ns = ns;
        buildDescriptors();
        setNamespaceResolverOnDescriptors();
    }

    public NamespaceResolverWithPrefixes getNamespaceResolver() {
        return ns;
    }
    protected void buildNamespaceResolver() {
        ns = new NamespaceResolverWithPrefixes();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);
        String ns1 = getPrimaryNamespacePrefix();
        if (ns1 != null && ns1.length() > 0) {
            ns.putPrimary(ns1, getPrimaryNamespace());
        }
        String ns2 = getSecondaryNamespacePrefix();
        if (ns2 != null && ns2.length() > 0) {
            ns.putSecondary(ns2, getSecondaryNamespace());
        }
    }
    
    public String getPrimaryNamespacePrefix() {
        return null;
    }
    public String getPrimaryNamespace() {
        return null;
    }
    
    public String getPrimaryNamespaceXPath() {
        if (ns.getPrimaryPrefix() != null) {
            return ns.getPrimaryPrefix() + ":";
        }
        return "";
    }
    public String resolvePrimaryNamespace() {
        return ns.resolveNamespacePrefix(ns.getPrimaryPrefix());
    }

    public String getSecondaryNamespacePrefix() {
        return null;
    }
    public String getSecondaryNamespace() {
        return null;
    }
    
    public String getSecondaryNamespaceXPath() {
        if (ns.getSecondaryPrefix() != null) {
            return ns.getSecondaryPrefix() + ":";
        }
        return "";
    }
    public String resolveSecondaryNamespace() {
        return ns.resolveNamespacePrefix(ns.getSecondaryPrefix());
    }
    
    protected abstract void buildDescriptors();

    protected void setNamespaceResolverOnDescriptors() {
        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(ns);
        }
    }
}
