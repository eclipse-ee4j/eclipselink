/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nillable;

import java.util.Vector;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeCollectionIsSetNodeNullPolicyTrueTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nillable/CompositeCollectionIsSetNodeNullPolicyTrue.xml";

    public CompositeCollectionIsSetNodeNullPolicyTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
    	// alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false);
    	aNullPolicy.setNullRepresentedByXsiNil(false);
    	// alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);//.ABSENT_NODE);
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetDevelopers");
        Project aProject = new CompositeCollectionNodeNullPolicyProject(true);
        XMLCompositeCollectionMapping aMapping = (XMLCompositeCollectionMapping)aProject.getDescriptor(Team.class)//
        .getMappingForAttributeName("developers");
        // TODO: renable after we implement NullPolicy for this mapping
        //aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName("Jane");
        anEmployee.setLastName("Doe");

        Vector developers = new Vector();
        developers.add(new Employee());//null);
        developers.add(anEmployee);
        developers.add(new Employee());//null);

        Team aTeam = new Team();
        aTeam.setId(123);
        aTeam.setName("Eng");
        aTeam.setDevelopers(developers);

        return aTeam;
    }
}
