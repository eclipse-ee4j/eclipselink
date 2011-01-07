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

import java.util.Iterator;
import java.util.ArrayList;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.models.optimisticlocking.Camera;
import org.eclipse.persistence.testing.models.optimisticlocking.Gamer;
import org.eclipse.persistence.testing.models.optimisticlocking.GamesConsole;
import org.eclipse.persistence.testing.models.optimisticlocking.PowerSupplyUnit;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullManyToManyValueTest extends SwitchableOptimisticLockingPolicyTest {

    protected GamesConsole original;
    protected GamesConsole original2;
    
    public UpdateNullManyToManyValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(Gamer.class);
        addClassToModify(GamesConsole.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new GamesConsole("PloughStation4", "black");
        original.setCamera(new Camera("eye", "black"));
        original.setPsu(new PowerSupplyUnit("0-123-456-789"));
        // no gamers
        
        original2 = new GamesConsole();
        original2.setName("Segone");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            readObjectAndAddIntoCollection(new Gamer("testy", null), false);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer("t4h_fury", "pwner"), false);
            readObjectAndAddIntoCollection(new Gamer("t5h_fury", null), false);
            readObjectAndAddIntoCollection(new Gamer("t6h_fury", "pwner"), false);
            readObjectAndAddIntoCollection(null, true);
            readObjectAndAddIntoCollection(new Gamer(null, "pwner"), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer("t8h_fury", null), false);
            readObjectAndAddIntoCollection(new Gamer("t9h_fury", "pwner"), false);
            readObjectAndAddIntoCollection(null, true);
            readObjectAndAddIntoCollection(new Gamer("10_fury", "pwner"), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer(null, "pwner"), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer(null, null), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer("13_fury", null), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(new Gamer("14_fury", "pwner"), true);
            updateFirstObject();
            readObjectAndAddIntoCollection(null, true);
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
      
    public void readObjectAndAddIntoCollection(Gamer newGamer, boolean removeAll) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        if (removeAll == true) {
            Iterator<Gamer> gamers = new ArrayList(clone.getGamers()).iterator();
            while (gamers.hasNext()) {
                Gamer g = gamers.next();
                g.setName(null);
                clone.removeGamer(g);
            }
        }
        
        if (newGamer != null) {
            clone.addGamer(newGamer);
        }
        
        uow.commit();
    }

    public void updateFirstObject() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        GamesConsole clone = (GamesConsole) uow.readObject(
                GamesConsole.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        
        if (!clone.getGamers().isEmpty()) {
            Gamer g = clone.getGamers().get(0);
            g.setDescription(null);
            g.setName(null);
        }
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}

