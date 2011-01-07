/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.*;
import java.io.Writer;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: This test checks the printIdentityMaps() method on the session when using root inheritance with no subclasses.
 * This test is for bug #2675242
 * <p>
 *
 * <b>Responsibilities</b>: Check if the printIdentityMaps functionality works properly with single inheritance with no subclasses
 */
public class SingleInheritanceTest extends TestCase {
    protected InheritancePolicy originalInheritancePolicy;
    protected org.eclipse.persistence.mappings.DatabaseMapping originalDbMapping;
    protected Map originalDescriptors;
    protected ClassDescriptor vehicleDescriptor;
    protected Writer originalLogWriter;

    /**
     * This is required to allow subclassing.
     */
    public SingleInheritanceTest() {
        setDescription("The test uses inheritance on a single class w/ no subclasses and calls printIdentityMaps on the session");
    }

    public void reset() {
        //reset the descriptors, and add back the Vehicle's inheritance info and 1:1 mapping
        getSession().getProject().setDescriptors(originalDescriptors);
        getAbstractSession().clearDescriptors();
        vehicleDescriptor.setInheritancePolicy(originalInheritancePolicy);
        vehicleDescriptor.addMapping(originalDbMapping);
        getSession().setLog(originalLogWriter);
    }

    protected void setup() {

        /*
         * For this test, it is necessary to remove all subclasses of Vehicle.  So,
         * it is easier to remove all descriptors and re-add the vehicle descriptor - removing
         * its 1:1 mapping and inheritance info pointing to other descriptors.  We also need
         * to remove + restore the log so that printIdentityMaps does not display output
         */
        originalLogWriter = getSession().getLog();
        getSession().setLog(new java.io.CharArrayWriter());

        vehicleDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.inheritance.Vehicle.class);
        originalDescriptors = getSession().getProject().getDescriptors();
        originalInheritancePolicy = vehicleDescriptor.getInheritancePolicy();
        originalDbMapping = vehicleDescriptor.getMappingForAttributeName("owner");
        //remove the 1:1 "owner" mapping
        vehicleDescriptor.getMappings().remove(originalDbMapping);

        //empty Vehicle's inheritance info
        InheritancePolicy newInheritancePolicy = (InheritancePolicy)originalInheritancePolicy.clone();
        newInheritancePolicy.addClassIndicator(org.eclipse.persistence.testing.models.inheritance.Vehicle.class, new java.lang.Long(8));
        newInheritancePolicy.setChildDescriptors(new Vector());
        newInheritancePolicy.setClassIndicatorMapping(new Hashtable(3));
        vehicleDescriptor.setInheritancePolicy(newInheritancePolicy);

        //empty the descriptors and re-add the Vehicle descriptor
        getSession().getProject().setDescriptors(new Hashtable(3));
        getSession().getProject().addDescriptor(vehicleDescriptor);
    }

    public void test() {
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        try {
            ((AbstractSession)getSession()).getIdentityMapAccessorInstance().printIdentityMaps();

            return;
        } catch (java.util.NoSuchElementException e) {
            throw new TestErrorException("Single class inheritance (no subclasses) caused a java.util.NoSuchElementException when" + " printIdentityMaps() was called on the session");
        }
    }
}
