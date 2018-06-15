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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.usemaptests;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.sessions.Project;

/**
 *
 */
public class AnyAttributeUsingMapProject extends Project {
    public AnyAttributeUsingMapProject() {
        this.addDescriptor(buildRootDescriptor());
        //org.eclipse.persistence.oxm.XMLLogin login = new XMLLogin();
        //login.setPlatform(new org.eclipse.persistence.oxm.platform.DOMPlatform());
        //this.setLogin(login);
        //this.getLogin().setPlatform(new org.eclipse.persistence.oxm.platform.DOMPlatform());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        mapping.useMapClass(java.util.Hashtable.class);
        descriptor.addMapping(mapping);

        return descriptor;
    }
}
