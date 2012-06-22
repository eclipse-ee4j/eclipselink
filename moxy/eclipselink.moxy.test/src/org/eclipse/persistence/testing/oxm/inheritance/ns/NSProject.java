/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance.ns;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class NSProject extends Project {

    public NSProject(String childPrefix) {
        this.addDescriptor(buildParentDescriptor());
        this.addDescriptor(buildChildDescriptor(childPrefix));
    }

    private XMLDescriptor buildParentDescriptor() {
        XMLDescriptor desc = new XMLDescriptor();
        desc.setJavaClass(NSParent.class);
        desc.setDefaultRootElement("parent:root");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("parent", "urn:parent");
        nsResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        desc.setNamespaceResolver(nsResolver);

        XMLField classIndicatorField = new XMLField("@xsi:type");
        desc.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        desc.getInheritancePolicy().addClassIndicator(NSChild.class, "parent:child");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("parentProp");
        mapping.setXPath("parent:parent-prop/text()");
        desc.addMapping(mapping);
        
        return desc;
    }

    private XMLDescriptor buildChildDescriptor(String childPrefix) {
        XMLDescriptor desc = new XMLDescriptor();
        desc.setJavaClass(NSChild.class);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put(childPrefix, "urn:child");
        desc.setNamespaceResolver(nsResolver);

        desc.getInheritancePolicy().setParentClass(NSParent.class);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("childProp");
        mapping.setXPath(childPrefix + ":child-prop/text()");
        desc.addMapping(mapping);

        return desc;
    }

}
