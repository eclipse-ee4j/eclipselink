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
 *     Jan 9, 2009-1.1 Chris Delahunt 
 *       - Bug 244802: PostLoad callback getting invoked twice 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * Tests the @PostLoad event from an EntityListener is fired only once when in a transaction.
 *
 * @author Chris Delahunt
 */
public class EntityListenerPostLoadRefreshTest extends EntityContainerTestBase {
    protected int m_beforeEvent, m_afterEvent;
    
    public void test() throws Exception {
        beginTransaction();
        
        Employee employee= new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");
        m_beforeEvent = EmployeeListener.POST_LOAD_COUNT;
        getEntityManager().persist(employee);
        getEntityManager().flush();
        getEntityManager().refresh( employee );
       
        m_afterEvent = EmployeeListener.POST_LOAD_COUNT;
        this.rollbackTransaction();
    }
    
    public void verify() {
        if ((m_afterEvent-m_beforeEvent) != 1) {
            throw new TestErrorException("The callback method was called "+(m_afterEvent - m_beforeEvent)+
                    " times.  It should have been called only once");
        }
    }
}
