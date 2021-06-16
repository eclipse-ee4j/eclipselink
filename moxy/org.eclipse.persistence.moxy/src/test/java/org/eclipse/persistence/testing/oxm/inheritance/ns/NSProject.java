/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.1 - initial implementation
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
