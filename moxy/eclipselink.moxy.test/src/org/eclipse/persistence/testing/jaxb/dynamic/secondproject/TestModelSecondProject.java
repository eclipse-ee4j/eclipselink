/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
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
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

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