/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Oct 29/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.keybased.compositekeyclass;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;

public class AttributeProject extends Project {

    public AttributeProject() {
        this.addDescriptor(getDepartmentDescriptor());
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getEmployeeIDDescriptor());
    }

    private XMLDescriptor getDepartmentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Department.class);
        descriptor.setDefaultRootElement("ns:department");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeCollectionMapping employeesMapping = new XMLCompositeCollectionMapping();
        employeesMapping.setAttributeName("employees");
        employeesMapping.setXPath("employee");
        employeesMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(employeesMapping);

        return descriptor;
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.addPrimaryKeyFieldName("id/@id1");
        descriptor.addPrimaryKeyFieldName("id/@ns:id2");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeObjectMapping idMapping = new XMLCompositeObjectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id");
        idMapping.setReferenceClass(EmployeeID.class);
        descriptor.addMapping(idMapping);

        XMLObjectReferenceMapping managerMapping = new XMLObjectReferenceMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.addSourceToTargetKeyFieldAssociation("manager/@fk1", "id/@id1");
        managerMapping.addSourceToTargetKeyFieldAssociation("manager/@ns:fk2", "id/@ns:id2");
        managerMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(managerMapping);

        XMLCollectionReferenceMapping employeesMapping = new XMLCollectionReferenceMapping();
        employeesMapping.setAttributeName("teamMembers");
        employeesMapping.addSourceToTargetKeyFieldAssociation("team-member/@fk1", "id/@id1");
        employeesMapping.addSourceToTargetKeyFieldAssociation("team-member/@ns:fk2", "id/@ns:id2");
        employeesMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(employeesMapping);

        return descriptor;
    }

    private XMLDescriptor getEmployeeIDDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmployeeID.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping id1Mapping = new XMLDirectMapping();
        id1Mapping.setAttributeName("id1");
        id1Mapping.setXPath("@id1");
        descriptor.addMapping(id1Mapping);

        XMLDirectMapping id2Mapping = new XMLDirectMapping();
        id2Mapping.setAttributeName("id2");
        id2Mapping.setXPath("@ns:id2");
        descriptor.addMapping(id2Mapping);

        return descriptor;
    }

}