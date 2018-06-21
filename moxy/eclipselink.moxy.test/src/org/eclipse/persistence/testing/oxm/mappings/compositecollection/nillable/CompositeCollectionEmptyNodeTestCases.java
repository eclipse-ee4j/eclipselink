/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nillable;

import java.util.Vector;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase.Metadata;
import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CompositeCollectionEmptyNodeTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nillable/EmptyNode.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nillable/EmptyNode.json";

    @Override
    public Platform getPlatform() {
        Platform superPlatform = super.getPlatform();
        if(Platform.DOC_PRES == superPlatform) {
            return Platform.SAX;
        }
        return superPlatform;
    }

    public CompositeCollectionEmptyNodeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(true);
        aNullPolicy.setNullRepresentedByXsiNil(false);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        Project aProject = new CompositeCollectionNodeNullPolicyProject(true);
        XMLCompositeCollectionMapping aMapping = (XMLCompositeCollectionMapping)aProject.getDescriptor(Team.class)//
        .getMappingForAttributeName("developers");
        // TODO: renable after we implement NullPolicy for this mapping
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    @Override
    public Metadata getMetadata() {
        return Metadata.JAVA;
    }

    @Override
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

}
