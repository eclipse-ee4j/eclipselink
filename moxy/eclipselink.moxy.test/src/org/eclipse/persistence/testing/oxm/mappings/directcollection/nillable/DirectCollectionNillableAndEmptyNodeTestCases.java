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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectCollectionNillableAndEmptyNodeTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionNillableNodeNullPolicy.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionNillableAndEmptyNodeWrite.json";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionNillableAndEmptyNodeWrite.xml";

    public DirectCollectionNillableAndEmptyNodeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(true);
        aNullPolicy.setNullRepresentedByXsiNil(true);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);

        Project aProject = new DirectCollectionNodeNullPolicyProject(true);
        ClassDescriptor aDescriptor = aProject.getDescriptor(Employee.class);
        XMLCompositeDirectCollectionMapping aMapping = (XMLCompositeDirectCollectionMapping) aDescriptor.getMappingForAttributeName("tasks");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }



    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName(null);
        Vector aVector = new Vector();
        aVector.add(null);
        aVector.add("write code");
        aVector.add(null);
        aVector.add(null);
        anEmployee.setTasks(aVector);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}
