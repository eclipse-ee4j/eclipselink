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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectNullPolicyElementSetNillableTestCases extends XMLWithJSONMappingTestCases {
    // TC UC 0-10 to 0-13, 2-0
    private final static String XML_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectNullPolicyElementSetNillable.xml";
    private final static String JSON_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectNullPolicyElementSetNillable.json";

    public DirectNullPolicyElementSetNillableTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // Alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(false);
        aNullPolicy.setNullRepresentedByXsiNil(true);
        // Alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        Project aProject = new DirectNodeNullPolicyProject(true);

        // Add xsi namespace map entry to the resolver (we don't pick up the one on the xml instance doc)
        XMLDescriptor employeeDescriptor = (XMLDescriptor) aProject.getDescriptor(Employee.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        employeeDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class) //
            .getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

}