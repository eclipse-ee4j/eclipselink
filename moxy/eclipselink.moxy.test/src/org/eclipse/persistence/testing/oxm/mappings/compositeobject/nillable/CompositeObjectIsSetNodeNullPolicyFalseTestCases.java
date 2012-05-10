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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CompositeObjectIsSetNodeNullPolicyFalseTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNodeNullPolicyFalse.xml";
    private final static String JSON_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNodeNullPolicyFalse.json";

    public CompositeObjectIsSetNodeNullPolicyFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
		setControlJSON(JSON_RESOURCE);

        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false);
    	aNullPolicy.setNullRepresentedByXsiNil(false);
    	// alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetManager");
        Project aProject = new CompositeObjectNodeNullPolicyProject(true);
        XMLCompositeObjectMapping aMapping = (XMLCompositeObjectMapping)aProject.getDescriptor(Team.class)//
        .getMappingForAttributeName("manager");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
    	Team aTeam = new Team();
    	aTeam.setId(123);
    	aTeam.setName("Eng");        
        return aTeam;
    }
}
