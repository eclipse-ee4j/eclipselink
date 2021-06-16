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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.identifiedbyname.withgroupingelement;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;
import org.eclipse.persistence.internal.oxm.*;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;

public class WithGroupingElementIdentifiedByNameProject extends Project {
    public WithGroupingElementIdentifiedByNameProject() {
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        resolver.put(XMLConstants.SCHEMA_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        descriptor.setNamespaceResolver(resolver);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLCompositeDirectCollectionMapping responsibilitiesMapping = new XMLCompositeDirectCollectionMapping();
        responsibilitiesMapping.setAttributeName("responsibilities");
        XMLField field = new XMLField("responsibilities/list/responsibility/text()");
        field.setIsTypedTextField(true);
        responsibilitiesMapping.setField(field);
        descriptor.addMapping(responsibilitiesMapping);

        return descriptor;
    }
}
