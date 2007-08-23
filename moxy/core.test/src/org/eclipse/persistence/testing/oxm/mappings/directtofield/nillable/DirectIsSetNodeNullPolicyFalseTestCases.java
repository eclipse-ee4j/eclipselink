/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/* $Header: DirectIsSetNodeNullPolicyFalseTestCases.java 02-nov-2006.10:57:19 gyorke Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    gyorke      11/02/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: DirectIsSetNodeNullPolicyFalseTestCases.java 02-nov-2006.10:57:19 gyorke Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.IsSetNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectIsSetNodeNullPolicyFalseTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNodeNullPolicyFalse.xml";

    public DirectIsSetNodeNullPolicyFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = new IsSetNodeNullPolicy();
        ((IsSetNodeNullPolicy)aNodeNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNodeNullPolicy(aNodeNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        //anEmployee.setFirstName(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}