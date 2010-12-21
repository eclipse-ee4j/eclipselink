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

import java.util.Iterator;
import java.util.ArrayList;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.models.optimisticlocking.Camera;
import org.eclipse.persistence.testing.models.optimisticlocking.Controller;
import org.eclipse.persistence.testing.models.optimisticlocking.GamesConsole;
import org.eclipse.persistence.testing.models.optimisticlocking.PowerSupplyUnit;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullOneToManyValueTest extends SwitchableOptimisticLockingPolicyTest {

    protected GamesConsole original;
    protected GamesConsole original2;
    
    public UpdateNullOneToManyValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(GamesConsole.class);
        addClassToModify(Controller.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new GamesConsole("PloughStation4", "black");
        original.setCamera(new Camera("eye", "black"));
        original.setPsu(new PowerSupplyUnit("0-123-456-789"));
        // no controllers
        
        original2 = new GamesConsole();
        original2.setName("Segone");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            Controller controller1 = new Controller("7axis", "blue");
            Controller controller2 = new Controller("dualshocking", null);
            Controller controller3 = new Controller(null, "red");
            Controller controller4 = new Controller("dualshocking", "green");
            Controller controller5 = new Controller("moov", "black");
            
            readObjectAndChangeAttributeValue(controller1);
            readObjectAndChangeAttributeValue(controller2);
            readObjectAndChangeAttributeValue(controller3);
            updateFirstObject(null);
            updateFirstObject("triplaxis");
            updateFirstObject(null);
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(controller4);
            readObjectAndChangeAttributeValue(controller5);
            updateFirstObject("quadraxis");
            updateFirstObject(null);
            readObjectAndChangeAttributeValue(null);
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
    
    public void readObjectAndChangeAttributeValue(Controller newController) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        if (newController == null) { // remove all
            Iterator<Controller> controllers = new ArrayList(clone.getControllers()).iterator();
            while (controllers.hasNext()) {
                Controller c = controllers.next();
                c.setName(null);
                clone.removeController(c);
            }
        } else {
            clone.addController(newController);
        }
        
        uow.commit();
    }
    
    public void updateFirstObject(String description) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        if (!clone.getControllers().isEmpty()) {
            Controller c = clone.getControllers().get(0);
            c.setDescription(description);
            c.setName(null);
        }
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}
