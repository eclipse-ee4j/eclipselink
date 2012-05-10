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
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

//public class DirectCollectionOptionalNodeNullPolicyAttributeTestCases extends XMLWithJSONMappingTestCases {
public class DirectCollectionOptionalNodeNullPolicyAttributeTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionOptionalNodeNullPolicyAttribute.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionOptionalNodeNullPolicyAttribute.json";

    public DirectCollectionOptionalNodeNullPolicyAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
      // setControlJSON(JSON_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(false);
        aNullPolicy.setNullRepresentedByXsiNil(false);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);

        Project aProject = new DirectCollectionNodeNullPolicyProject(false);
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
        anEmployee.setTasks(aVector);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

    public Object getReadControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName(null);
        Vector aVector = new Vector();
        aVector.add("write code");
        anEmployee.setTasks(aVector);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

}