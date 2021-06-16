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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;
import org.eclipse.persistence.oxm.mappings.*;

public class AnyAttributeWithGroupingElementNSProject extends Project {
    public AnyAttributeWithGroupingElementNSProject() {
        this.addDescriptor(buildRootDescriptor());
        //XMLLogin login = new XMLLogin();
        //login.setPlatform(new DOMPlatform());
        //this.setLogin(login);
        //this.getLogin().setPlatform(new org.eclipse.persistence.oxm.platform.DOMPlatform());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("myns:root");

        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        mapping.setXPath("myns:grouping-element");
        descriptor.addMapping(mapping);

        NamespaceResolver nr = new NamespaceResolver();
        nr.put("myns", "www.example.com/some-dir/some.xsd");
        //nr.put("", "www.example.com/some-other-dir/some.xsd");
        descriptor.setNamespaceResolver(nr);

        return descriptor;
    }
}
