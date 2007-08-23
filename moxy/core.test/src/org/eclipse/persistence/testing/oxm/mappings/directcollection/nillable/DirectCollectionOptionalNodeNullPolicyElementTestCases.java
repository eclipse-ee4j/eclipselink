/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/* $Header: DirectCollectionOptionalNodeNullPolicyElementTestCases.java 20-jul-2007.13:13:21 bdoughan Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    bdoughan    07/20/07 - 
    gyorke      11/02/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: DirectCollectionOptionalNodeNullPolicyElementTestCases.java 20-jul-2007.13:13:21 bdoughan Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;


import java.util.Vector;

import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.OptionalNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionOptionalNodeNullPolicyElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionOptionalNodeNullPolicyElement.xml";

    public DirectCollectionOptionalNodeNullPolicyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = OptionalNodeNullPolicy.getInstance();
        Project aProject = new DirectCollectionNodeNullPolicyProject(true);
        XMLCompositeDirectCollectionMapping aMapping = (XMLCompositeDirectCollectionMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("tasks");
        aMapping.setNodeNullPolicy(aNodeNullPolicy);
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