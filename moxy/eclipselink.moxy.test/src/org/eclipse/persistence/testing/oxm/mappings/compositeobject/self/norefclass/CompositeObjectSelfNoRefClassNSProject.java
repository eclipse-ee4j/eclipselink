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
//     Denise Smith - May 8/2009
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectSelfNoRefClassNSProject extends Project {
    public CompositeObjectSelfNoRefClassNSProject() {
        addDescriptor(getRootDescriptor());
        addDescriptor(getAddressDescriptor());
    }

    protected XMLDescriptor getRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("ns0:root");

        XMLCompositeObjectMapping theObjectMapping = new XMLCompositeObjectMapping();
        theObjectMapping.setAttributeName("theObject");
        theObjectMapping.setXPath(".");
        descriptor.addMapping(theObjectMapping);

        NamespaceResolver nr = new NamespaceResolver();
        nr.put("ns0", "namespace1");
        descriptor.setNamespaceResolver(nr);
        return descriptor;
    }
    protected XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.setDefaultRootElement("ns0:address");

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("ns0:street/text()");
        descriptor.addMapping(streetMapping);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/ns0:mailingAddressType");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        NamespaceResolver nr = new NamespaceResolver();
        nr.put("ns0", "namespace1");
        descriptor.setNamespaceResolver(nr);

        return descriptor;
    }
}
