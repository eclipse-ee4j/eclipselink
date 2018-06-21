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
//     Denise Smith - October 20, 2009

package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;

public class XMLMarshallerNSTestProject extends XMLMarshallerNoDefaultRootTestProject {
    public XMLMarshallerNSTestProject() {
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = super.getEmployeeDescriptor();
        descriptor.setDefaultRootElement("thens:employee");

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put("thens", "www.example.com/some-dir/employee.xsd");
        descriptor.setNamespaceResolver(resolver);

        return descriptor;
    }
}
