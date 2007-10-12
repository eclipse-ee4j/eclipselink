/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import java.util.Vector;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionOptionalNodeNullPolicyElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionOptionalNodeNullPolicyElement.xml";

    public DirectCollectionOptionalNodeNullPolicyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
    	// alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false);
    	aNullPolicy.setNullRepresentedByXsiNil(false);
    	// alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
        Project aProject = new DirectCollectionNodeNullPolicyProject(true);
        XMLCompositeDirectCollectionMapping aMapping = (XMLCompositeDirectCollectionMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("tasks");
        // TODO: renable after we implement NullPolicy for this mapping
        //aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        //anEmployee.setFirstName(null);
        Vector aVector = new Vector();
        //aVector.add(null);
        aVector.add("write code");
        //aVector.add(null);
        anEmployee.setTasks(aVector);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}