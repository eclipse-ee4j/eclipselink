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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * Besides the existing tests in this model, this test adds a couple of sanity
 * checks for the XML/Annotation merging of the Employee descriptor.
 *
 * @author Guy Pelletier
 */
public class XMLAnnotationMergingTest extends EntityContainerTestBase  {
    protected boolean reset = false;    // reset gets called twice on error
    protected Exception m_exception;
    
    public void setup () {
        super.setup();
        this.reset = true;
        
        // Clear the cache so we are working from scratch.
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void reset () {
        if (reset) {
            reset = false;
        }
        super.reset();
    }
    
    public void test() throws Exception {
        try {
            getEntityManager().createNamedQuery("findAllEmployeesByFirstName").setParameter("firstname", "Guy").getResultList();
        } catch (Exception e) {
            m_exception = e;
        }
    }
    
    public void verify() {
        // Check that the named query from employee was found.
        if (m_exception != null) {
            throw new TestErrorException("Our named query from Employee was lost in the XML/Annotation merging. " + m_exception);
        }

        // Check that the descriptor contains those mappings we expect from
        // annotations.
        ClassDescriptor descriptor = ((EntityManagerImpl)getEntityManager()).getActiveSession().getClassDescriptor(Employee.class);
        
        if (descriptor.getMappingForAttributeName("lastName") == null ) {
            throw new TestErrorException("The mapping for [lastName] was was lost in the XML/Annotation merging.");
        }
        
        if (descriptor.getMappingForAttributeName("manager") == null ) {
            throw new TestErrorException("The mapping for [manager] was lost in the XML/Annotation merging.");
        }
    }
}
