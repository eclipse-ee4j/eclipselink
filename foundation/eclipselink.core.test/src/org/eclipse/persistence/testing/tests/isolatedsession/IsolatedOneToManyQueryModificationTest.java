/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * EL Bug 426500 - Modifying a mapping's selectionCriteria in afterLoad can result in uninitialized DatabaseField instances
 * @author dminsky
 */
public class IsolatedOneToManyQueryModificationTest extends TestCase {
    
    public IsolatedOneToManyQueryModificationTest() {
        super();
        setDescription("IsolatedOneToManyQueryModificationTest");
    }
    
    public void test() {
        IsolatedParent exampleParent = IsolatedParent.buildIsolatedParentExample2();
        
        for (int i = 0; i < 100; i++) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            exampleParent = (IsolatedParent)uow.readObject(exampleParent);
            
            IsolatedParent result = (IsolatedParent) uow.executeQuery("findParentBySerial", IsolatedParent.class, exampleParent.getSerial());
            
            assertNotNull("Parent should not be null", result);
            assertNotNull("Parent should not have a null collection", result.getChildren());
            
            assertEquals("Parent should be the same id", exampleParent.getId(), result.getId());
            assertEquals("Parent should be the same serial", exampleParent.getSerial(), result.getSerial());
            
            assertEquals("Parent should not have a zero length collection", exampleParent.getChildren().size(), result.getChildren().size());
            
            IsolatedChild child = result.getChildren().get(0);
            IsolatedChild exampleChild = exampleParent.getChildren().get(0);
            
            assertNotNull("Only child should not be null", child);
            assertEquals("Only child should have the same id", exampleChild.getId(), child.getId());
            assertNotNull("Only child should have a valid parent", child.getParent());
            assertEquals("Only child should reference the found parent", result, child.getParent());
            
            uow.release();
        }
    }
    
}
