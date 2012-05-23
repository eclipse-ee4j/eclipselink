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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.testing.framework.*;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * VariableOneToOneMapping doesn't work with proxyIndirection,
 * throws a nullPointerException when building the object reference.
 * Scenario: 2 classes (A and B) and an interface C, where A implements C.
 * Set up the mapping from B to A as VariableOneToOneMapping();
 * Then read in a B object that references an A object.  It will throw
 * the following exception :
 * java.lang.NullPointerException
 *       at TOPLink.Private.Indirection.ProxyIndirectionPolicy.valueFromQuery(
 *       ProxyIndirectionPolicy.java:123)
 *
 * Class created on Mar 22/2002; CR#3838 in StarTeam; Predrag
 */

public class QueryAccrossV11ProxyIndirectionTest extends AutoVerifyTestCase {
    protected Exception caughtException;
    protected ClassDescriptor descriptor;
    protected IndirectionPolicy indirectionPolicy = null;

    public QueryAccrossV11ProxyIndirectionTest() {
        super();
        setDescription("Test that VariableOneToOneMapping mapping with ProxyIndirection" + "  works correctly");
    }

    public ClassDescriptor getDescriptorV11ProxyIndirection() {
        return descriptor;
    }

    public void setDescriptorV11ProxyIndirection(Class cls, String attributeName, Class proxyIndirectClass) {
        // instruct descriptor to use proxy indirection since
        // VariableOneToOneMapping maping for attribute "program"
        // in InterfaceWithoutTablesProject.java, did not set it;
        ClassDescriptor descriptorToReturn = null;
        descriptorToReturn = getSession().getClassDescriptor(cls);
        ObjectReferenceMapping mappingForAttribute = 
            (ObjectReferenceMapping)descriptorToReturn.getMappingForAttributeName(attributeName);
        indirectionPolicy = mappingForAttribute.getIndirectionPolicy();
        mappingForAttribute.useProxyIndirection(proxyIndirectClass);
        this.descriptor = descriptorToReturn;
    } // end of setDescriptorNamedQuery

    public void resetDescriptorV11ProxyIndirection(String attributeName) {
        ObjectReferenceMapping mappingForAttribute = 
            (ObjectReferenceMapping)descriptor.getMappingForAttributeName(attributeName);
        mappingForAttribute.setIndirectionPolicy(indirectionPolicy);
    }

    public void setup() {
        setDescriptorV11ProxyIndirection(Actor.class, "program", ProgramInt.class);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    } // end of setup()

    public void test() {
        try {
            java.util.Vector actors = getSession().readAllObjects(Actor.class);
            for (int x = 0; x < actors.size(); x++) {
                Actor actor = (Actor)actors.elementAt(x);
                //trigger indirection
                String name = actor.program.getName();
            }
        } catch (NullPointerException e) {
            caughtException = e;
        }
    } // end of test()

    public void reset() {
        // do not want to keep proxy indirection on maping for 
        // attribute "program" - VariableOneToOneMapping
        resetDescriptorV11ProxyIndirection("program");
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    } // end of reset()

    public void verify() {
        if (caughtException != null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Test that VariableOneToOneMapping mapping with" + 
                                                                              " ProxyIndirection works correctly.\n" + 
                                                                              "This exception thrown while testing test case.\n" + 
                                                                              "----- QueryAccrossV11ProxyIndirectionTest() -----\n" + 
                                                                              caughtException.getMessage());
        }

    }
}   // end of class QueryAccrossV11ProxyIndirectionTest
