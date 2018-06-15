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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectNullPolicyAttributeSetEmptyTrueTestCases extends XMLWithJSONMappingTestCases {
    // TC  UC 6-2(marshal) and 5-1 to 5-4(unmarshal)
    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectNullPolicyAttributeSetEmptyTrue.xml";
    private final static String JSON_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectNullPolicyAttributeSetEmptyTrue.json";

    public DirectNullPolicyAttributeSetEmptyTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Project aProject = new DirectNodeNullPolicyProject(false);
        updateNullPolicyForAttribute(aProject, "firstName");
        updateNullPolicyForAttribute(aProject, "id");

        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(null);
        anEmployee.setFirstName(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

    private void updateNullPolicyForAttribute(Project aProject, String attributeName){

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // Alter unmarshal policy state
        ((NullPolicy)aNullPolicy).setSetPerformedForAbsentNode(true); // no effect
        aNullPolicy.setNullRepresentedByEmptyNode(true); // no effect
        aNullPolicy.setNullRepresentedByXsiNil(false);  // no effect
        //  Alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName(attributeName);
        aMapping.setNullPolicy(aNullPolicy);
    }
}
