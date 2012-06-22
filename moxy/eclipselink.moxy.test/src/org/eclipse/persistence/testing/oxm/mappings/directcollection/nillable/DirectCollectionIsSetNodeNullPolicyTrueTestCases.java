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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionIsSetNodeNullPolicyTrueTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionIsSetNodeNullPolicyTrue.xml";

    public DirectCollectionIsSetNodeNullPolicyTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByXsiNil(true);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);

        ((IsSetNullPolicy) aNullPolicy).setIsSetMethodName("isSetTasks");

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
        aVector.add("");
        anEmployee.setTasks(aVector);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

}