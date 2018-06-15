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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public class XMLMarshallerTestProject extends XMLMarshallerNoDefaultRootTestProject {
    public XMLMarshallerTestProject() {
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = super.getEmployeeDescriptor();
        descriptor.setDefaultRootElement("employee");

        return descriptor;
    }
}
