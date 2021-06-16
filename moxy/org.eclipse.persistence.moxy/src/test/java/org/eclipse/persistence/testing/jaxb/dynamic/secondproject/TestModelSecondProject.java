/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic.secondproject;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;

public class TestModelSecondProject extends Project {

    private NamespaceResolver nsResolver;

    public TestModelSecondProject() {
        super();

        nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

        addEmployeeDescriptor();
    }

    public void addEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.zzz.Employee");
        descriptor.setDefaultRootElement("ns0:employee");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLDirectMapping department = new XMLDirectMapping();
        department.setAttributeName("department");
        department.setXPath("department/text()");
        department.getNullPolicy().setNullRepresentedByXsiNil(true);
        department.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        descriptor.addMapping(department);

        this.addDescriptor(descriptor);
    }

}
