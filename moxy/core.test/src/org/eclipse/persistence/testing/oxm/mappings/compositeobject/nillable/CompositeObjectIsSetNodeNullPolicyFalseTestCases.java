/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/* $Header: CompositeObjectIsSetNodeNullPolicyFalseTestCases.java 02-nov-2006.10:57:14 gyorke Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    gyorke      11/02/06 - 
    bdoughan    11/14/06 - 
    mfobrien    10/27/06 - Creation
 */

/**
 *  @version $Header: CompositeObjectIsSetNodeNullPolicyFalseTestCases.java 02-nov-2006.10:57:14 gyorke Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.eclipse.persistence.oxm.mappings.IsSetNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectIsSetNodeNullPolicyFalseTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNodeNullPolicyFalse.xml";

    public CompositeObjectIsSetNodeNullPolicyFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = new IsSetNodeNullPolicy();
        ((IsSetNodeNullPolicy)aNodeNullPolicy).setIsSetMethodName("isSetManager");
        Project aProject = new CompositeObjectNodeNullPolicyProject(true);
        XMLCompositeObjectMapping aMapping = (XMLCompositeObjectMapping)aProject.getDescriptor(Team.class)//
        .getMappingForAttributeName("manager");
        aMapping.setNodeNullPolicy(aNodeNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
    	Team aTeam = new Team();
    	aTeam.setId(123);
    	aTeam.setName("Eng");        
        return aTeam;
    }
}
