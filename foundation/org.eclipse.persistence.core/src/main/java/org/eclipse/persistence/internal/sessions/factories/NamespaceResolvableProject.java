/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     minorman - May 2008: helpful superclass that handles Namespaces for project-Project's

package org.eclipse.persistence.internal.sessions.factories;

//javase imports

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

import javax.xml.namespace.QName;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

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

    protected NamespaceResolvableProject() {
        super();
        fieldQname = new QName(getSecondaryNamespace(), "field");
        buildNamespaceResolver();
        buildDescriptors();
        setNamespaceResolverOnDescriptors();
    }
    protected NamespaceResolvableProject(NamespaceResolverWithPrefixes ns) {
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
        if (ns1 != null && !ns1.isEmpty()) {
            ns.putPrimary(ns1, getPrimaryNamespace());
        }
        String ns2 = getSecondaryNamespacePrefix();
        if (ns2 != null && !ns2.isEmpty()) {
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
        for (ClassDescriptor classDescriptor : getDescriptors().values()) {
            XMLDescriptor descriptor = (XMLDescriptor) classDescriptor;
            descriptor.setNamespaceResolver(ns);
        }
    }
}
