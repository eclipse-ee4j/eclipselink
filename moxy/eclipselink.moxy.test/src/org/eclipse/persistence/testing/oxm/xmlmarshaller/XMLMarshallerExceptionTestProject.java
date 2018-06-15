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
//     Denise Smith - October 20, 2009
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;

public class XMLMarshallerExceptionTestProject extends XMLMarshallerNoDefaultRootTestProject {
    public XMLMarshallerExceptionTestProject() {
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = super.getEmployeeDescriptor();
        descriptor.setDefaultRootElement("thens:employee");

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put("thensdiff", "www.example.com/some-dir/employee.xsd");
        descriptor.setNamespaceResolver(resolver);

        return descriptor;
    }
}
