/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-06 10:01:09 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.reuse;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.sessions.Project;

public class AnyAttributeReuseProject extends Project {

    public AnyAttributeReuseProject() {
        this.addDescriptor(buildRootDescriptor());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        mapping.setReuseContainer(true);
        descriptor.addMapping(mapping);

        return descriptor;
    }

}