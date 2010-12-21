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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.models.optimisticlocking.Controller;
import org.eclipse.persistence.testing.models.optimisticlocking.GamesConsole;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullOneToManyValueTest2 extends SwitchableOptimisticLockingPolicyTest {

    protected Controller original;
    protected Controller original2;
    
    public UpdateNullOneToManyValueTest2(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(Controller.class);
        addClassToModify(GamesConsole.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new Controller("Mad-Dogz", "black");
        original.setConsole(null);
        
        original2 = new Controller("X-Bocks", "green");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            readObjectAndChangeAttributeValue(new GamesConsole(null, "mauve"));
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new GamesConsole("Whee", "white"));
            readObjectAndChangeAttributeValue(new GamesConsole("N3S", "gray"));
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new GamesConsole(null, "white"));
            readObjectAndChangeAttributeValue(null);
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
    
    public void readObjectAndChangeAttributeValue(GamesConsole newConsole) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        Controller clone = (Controller) uow.readObject(
                Controller.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        if (clone.getConsole() != null) {
            clone.getConsole().removeController(clone);
        }
        if (newConsole != null) {
            GamesConsole consoleClone = (GamesConsole)uow.registerObject(newConsole);
            consoleClone.addController(clone);
        } else {
            clone.setConsole(null);
        }
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}
