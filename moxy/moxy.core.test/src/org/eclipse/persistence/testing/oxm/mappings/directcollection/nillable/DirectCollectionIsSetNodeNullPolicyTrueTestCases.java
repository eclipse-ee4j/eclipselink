/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/* $Header: DirectCollectionIsSetNodeNullPolicyTrueTestCases.java 02-nov-2006.10:57:16 gyorke Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    gyorke      11/02/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: DirectCollectionIsSetNodeNullPolicyTrueTestCases.java 02-nov-2006.10:57:16 gyorke Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;


import java.util.Vector;

import org.eclipse.persistence.oxm.mappings.IsSetNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionIsSetNodeNullPolicyTrueTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/directcollection/nillable/DirectCollectionIsSetNodeNullPolicyTrue.xml";

    public DirectCollectionIsSetNodeNullPolicyTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = new IsSetNodeNullPolicy();
        ((IsSetNodeNullPolicy)aNodeNullPolicy).setIsSetMethodName("isSetFirstName");
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
        // (the isSet method is set=true when we set tasks=null or tasks=new Vector()?) 
        //anEmployee.setTasks(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}