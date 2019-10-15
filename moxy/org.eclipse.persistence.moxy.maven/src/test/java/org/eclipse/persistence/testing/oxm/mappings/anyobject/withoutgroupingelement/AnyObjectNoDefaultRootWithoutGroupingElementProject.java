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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

/**
 *  @author  mfobrien
 *  @since   10.1.3.1.0
 */
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Child;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Root;

public class AnyObjectNoDefaultRootWithoutGroupingElementProject extends AnyObjectWithoutGroupingElementProject {
    public AnyObjectNoDefaultRootWithoutGroupingElementProject() {
        this.addDescriptor(buildRootDescriptor());
        this.addDescriptor(buildChildDescriptor());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLAnyObjectMapping anyObjectMapping = new XMLAnyObjectMapping();
        anyObjectMapping.setXPath("nested");
        anyObjectMapping.setAttributeName("any");
        anyObjectMapping.setGetMethodName("getAny");
        anyObjectMapping.setSetMethodName("setAny");
        descriptor.addMapping(anyObjectMapping);

        return descriptor;
    }

    public ClassDescriptor buildChildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Child.class);

        /*
         * B5112171: 25 Apr 2006
         * During marshalling - XML AnyObject and AnyCollection
         * mappings throw a NullPointerException when the
         * "document root element" on child object descriptors are not
         * all defined.  These nodes will be ignored with a warning.
         * Root descriptor above must be anyObject|Collection mapping
         */

        // make default root element undefined to invoke warning log
        //descriptor.setDefaultRootElement("child");
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);

        return descriptor;
    }
}
