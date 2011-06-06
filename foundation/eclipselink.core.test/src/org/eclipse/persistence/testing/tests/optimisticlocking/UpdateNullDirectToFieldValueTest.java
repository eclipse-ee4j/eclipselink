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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.Camera;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullDirectToFieldValueTest extends SwitchableOptimisticLockingPolicyTest {
    
    protected Camera original;
    protected Camera original2;
    
    public UpdateNullDirectToFieldValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(Camera.class);
    }

    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new Camera();
        original.setName(null);
        
        original2 = new Camera();
        original2.setName("LookyHere");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            // update the existing object's attribute
            readObjectAssertAndChangeAttributeValue(null, "LookyThere");
            
            // update the object's attribute to a null value
            readObjectAssertAndChangeAttributeValue("LookyThere", null);
            
            // update the object's attribute to a different value
            readObjectAssertAndChangeAttributeValue(null, "DumbCam");
            
            // update the object's attribute to null
            readObjectAssertAndChangeAttributeValue("DumbCam", null);
            
            // delete the object
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
        }
    }
    
    public void readObjectAssertAndChangeAttributeValue(String expectedAttributeValue, String newAttributeValue) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Camera clone = (Camera) uow.readObject(
                Camera.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        assertEquals("The object's field should be [" + expectedAttributeValue + ']', expectedAttributeValue, clone.getName());
        
        clone.setName(newAttributeValue);
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }

}
