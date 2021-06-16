/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.mappings.*;

/**
 *  @version $Header: AnyObjectWithGroupingElementProjectNS.java 26-apr-2007.16:59:08 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class AnyObjectWithGroupingElementProjectNS extends Project {
    public AnyObjectWithGroupingElementProjectNS() {
        this.addDescriptor(buildRootDescriptor());
        this.addDescriptor(buildChildDescriptor());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("myns:root");

        XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
        mapping.setXPath("myns:nested");
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        descriptor.addMapping(mapping);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("myns", "www.example.com/some-dir/some.xsd");
        //namespaceResolver.put("", "www.example.com/some-other-dir/some.xsd");
        descriptor.setNamespaceResolver(namespaceResolver);

        return descriptor;
    }

    public ClassDescriptor buildChildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Child.class);
        descriptor.setDefaultRootElement("myns:child");
        descriptor.addRootElement("myns:someChild");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("myns", "www.example.com/some-dir/some.xsd");
        //namespaceResolver.put("", "www.example.com/some-other-dir/some.xsd");
        descriptor.setNamespaceResolver(namespaceResolver);

        return descriptor;
    }
}
