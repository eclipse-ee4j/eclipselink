/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
