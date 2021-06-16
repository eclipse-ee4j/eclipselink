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
package org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class XMLRootComplexInheritanceProject extends Project {
    private NamespaceResolver namespaceResolver;

    public XMLRootComplexInheritanceProject() {
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        addDescriptor(getPersonDescriptor());
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getPersonDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Person.class);
        //descriptor.setDefaultRootElement("oxm:pRoot");

        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        descriptor.getInheritancePolicy().addClassIndicator(Employee.class, "oxm:emp");
        descriptor.getInheritancePolicy().addClassIndicator(Person.class, "oxm:person");

        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/oxm:person");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        namespaceResolver.put("oxm", "test");
        descriptor.setNamespaceResolver(namespaceResolver);

        return descriptor;
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.getInheritancePolicy().setParentClass(Person.class);
       // descriptor.setDefaultRootElement("oxm:eRoot");
        //descriptor.setDefaultRootElement("oxm:pRoot");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("empId");
        nameMapping.setGetMethodName("getEmpId");
        nameMapping.setSetMethodName("setEmpId");
        //nameMapping.setNullValue(new Integer(0));
        nameMapping.setXPath("id/text()");
        descriptor.addMapping(nameMapping);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/oxm:emp");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        namespaceResolver.put("oxm", "test");
        descriptor.setNamespaceResolver(namespaceResolver);

        return descriptor;
    }
}
