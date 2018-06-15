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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nillable;

import java.util.Vector;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase.Metadata;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CompositeCollectionNillableNodeNullPolicyTestCases extends XMLWithJSONMappingTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nillable/CompositeCollectionNillableNodeNullPolicy.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nillable/CompositeCollectionNillableNodeNullPolicy.json";

    public CompositeCollectionNillableNodeNullPolicyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(false);
        aNullPolicy.setNullRepresentedByXsiNil(true);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        Project aProject = new CompositeCollectionNodeNullPolicyProject(true);
        XMLCompositeCollectionMapping aMapping = (XMLCompositeCollectionMapping)aProject.getDescriptor(Team.class).getMappingForAttributeName("developers");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName("Jane");
        anEmployee.setLastName("Doe");

        Vector developers = new Vector();
        developers.add(null);
        developers.add(anEmployee);
        developers.add(null);

        Team aTeam = new Team();
        aTeam.setId(123);
        aTeam.setName("Eng");
        aTeam.setDevelopers(developers);

        return aTeam;
    }

    @Override
    public Metadata getMetadata() {
        return Metadata.JAVA;
    }
}
