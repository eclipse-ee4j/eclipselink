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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestException;

import org.eclipse.persistence.testing.models.optimisticlocking.Camera;
import org.eclipse.persistence.testing.models.optimisticlocking.GamesConsole;
import org.eclipse.persistence.testing.models.optimisticlocking.PowerSupplyUnit;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullAggregateValueTest extends SwitchableOptimisticLockingPolicyTest {

	protected GamesConsole original;
	protected GamesConsole original2;
    
    public UpdateNullAggregateValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(GamesConsole.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new GamesConsole("PloughStation3", "black");
        original.setCamera(new Camera("lookcam3", "black"));
        original.setPsu(null); // null PSU
        
        original2 = new GamesConsole();
        original2.setName("GameCub");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            readObjectAndChangeAttributeValue(new PowerSupplyUnit("7-898-213-378"));
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new PowerSupplyUnit("6-432-573-232"));
            
            readObjectAndModifyAttributeValue(null);
            readObjectAndModifyAttributeValue("7-432-888-44");
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new PowerSupplyUnit("5-321-325-183"));
            readObjectAndChangeAttributeValue(new PowerSupplyUnit(null));
            readObjectAndModifyAttributeValue("2-322-324-678");
            readObjectAndModifyAttributeValue("5-324-874-273");
            
            readObjectAndChangeAttributeValue(new PowerSupplyUnit("6-432-573-232"));
            readObjectAndChangeAttributeValue(null);
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
    
    public void readObjectAndChangeAttributeValue(PowerSupplyUnit newObject) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        clone.setPsu(newObject);
        
        uow.commit();
    }
    
    public void readObjectAndModifyAttributeValue(String newSerialNumber) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        clone.getPsu().setSerialNumber(newSerialNumber);
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}
