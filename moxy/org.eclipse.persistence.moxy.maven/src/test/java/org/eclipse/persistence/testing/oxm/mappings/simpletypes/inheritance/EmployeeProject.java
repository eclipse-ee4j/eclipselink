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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.inheritance;

// TopLink imports
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;

public class EmployeeProject extends Project {

  private NamespaceResolver namespaceResolver;

    public EmployeeProject() {
        super();

        namespaceResolver = new NamespaceResolver();
    namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        this.addDescriptor(getPersonDescriptor());
        this.addDescriptor(getEmployeeDescriptor());
    }

    XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);
        xmlDescriptor.getInheritancePolicy().setParentClass(Person.class);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("jobTitle");
        mapping.setXPath("text()");
        xmlDescriptor.addMapping(mapping);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/employee");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);

        return xmlDescriptor;
    }

    XMLDescriptor getPersonDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Person.class);
        xmlDescriptor.setDefaultRootElement("person");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);
        XMLField classIndicatorField = new XMLField("@xsi:type");
        xmlDescriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        xmlDescriptor.getInheritancePolicy().addClassIndicator(Employee.class, "employee");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("firstname");
        mapping.setXPath("firstName/text()");
        xmlDescriptor.addMapping(mapping);

        XMLDirectMapping mapping2 = new XMLDirectMapping();
        mapping2.setAttributeName("lastname");
        mapping2.setXPath("lastName/text()");
        xmlDescriptor.addMapping(mapping2);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/person");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);

        return xmlDescriptor;
    }
}
