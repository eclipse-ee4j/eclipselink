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
//     bdoughan - July 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.xmlroot.nil;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public class NilProject extends Project {

    public NilProject() {
        super();
        this.addDescriptor(getAddressDescriptor());
    }

    XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);
        xmlDescriptor.setDefaultRootElement("ns0:bar");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns0", "urn:foo");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        return xmlDescriptor;
    }

}
