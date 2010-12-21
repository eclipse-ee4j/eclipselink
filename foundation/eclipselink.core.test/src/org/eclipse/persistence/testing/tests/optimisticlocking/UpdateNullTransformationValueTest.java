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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.GamesConsole;
import org.eclipse.persistence.testing.models.optimisticlocking.PowerSupplyUnit;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullTransformationValueTest extends SwitchableOptimisticLockingPolicyTest {
    
    protected GamesConsole original;
    protected GamesConsole original2;
    
    public UpdateNullTransformationValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(GamesConsole.class);
    }

    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new GamesConsole();
        original.setPsu(null);
        
        original2 = new GamesConsole();
        original2.setName("LookyHere");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            // update the existing object's attribute
            readObjectAndChangeAttributeValue(new PowerSupplyUnit("1"));
            
            // update the object's attribute to null
            PowerSupplyUnit psu2 = new PowerSupplyUnit("2");
            psu2.setOn(true);
            readObjectAndChangeAttributeValue(psu2);
            
            PowerSupplyUnit psu3 = new PowerSupplyUnit("3");
            psu3.setOn(false);
            readObjectAndChangeAttributeValue(psu3);
            
            // update the object's attribute to a null value
            readObjectAndChangeAttributeValue(new PowerSupplyUnit(null));
            
            // update the object's attribute to a different value
            readObjectAndChangeAttributeValue(null);
           
            // delete the object
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
        }
    }
    
    public void readObjectAndChangeAttributeValue(PowerSupplyUnit psu) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        clone.setPsu(psu);
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }

}
