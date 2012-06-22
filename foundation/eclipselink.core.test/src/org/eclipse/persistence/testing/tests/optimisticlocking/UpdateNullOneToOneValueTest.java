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
public class UpdateNullOneToOneValueTest extends SwitchableOptimisticLockingPolicyTest {

    protected GamesConsole original;
    protected GamesConsole original2;
    
    public UpdateNullOneToOneValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(GamesConsole.class);
        addClassToModify(Camera.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new GamesConsole();
        original.setName("PloughStation");
        original.setCamera(null); // null camera
        original.setPsu(new PowerSupplyUnit("0-123-456-789"));
        
        original2 = new GamesConsole();
        original2.setName("Nintundo65");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            readObjectAndChangeAttributeValue(new Camera("lookcam", "black"));
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new Camera("barbeye", "pink"));
            readObjectAndChangeAttributeValue(new Camera("appleofmyeye", "white"));
            readObjectAndChangeAttributeValue(null);         
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
    
    public void readObjectAndChangeAttributeValue(Camera newCamera) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        clone.setCamera(newCamera);
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}
