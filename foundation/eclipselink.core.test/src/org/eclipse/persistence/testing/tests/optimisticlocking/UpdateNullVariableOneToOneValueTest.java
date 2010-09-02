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

import org.eclipse.persistence.testing.models.optimisticlocking.Gamer;
import org.eclipse.persistence.testing.models.optimisticlocking.Knitting;
import org.eclipse.persistence.testing.models.optimisticlocking.Cooking;
import org.eclipse.persistence.testing.models.optimisticlocking.Skill;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class UpdateNullVariableOneToOneValueTest extends SwitchableOptimisticLockingPolicyTest {

    protected Gamer original;
    protected Gamer original2;
    
    public UpdateNullVariableOneToOneValueTest(Class optimisticLockingPolicyClass) {
        super(optimisticLockingPolicyClass);
        addClassToModify(Gamer.class);
        addClassToModify(Knitting.class);
        addClassToModify(Cooking.class);
    }
    
    public void setup() {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = new Gamer();
        original.setName("Bob");
        original.setSkill(null); // null skill
        
        original2 = new Gamer();
        original2.setName("Doug");
        
        uow.registerObject(original);
        uow.registerObject(original2);
        uow.commit();
    }
    
    public void test() throws TestException {
        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            readObjectAndChangeAttributeValue(new Knitting("Knitting", "Lame"));
            readObjectAndChangeAttributeValue(null);
            readObjectAndChangeAttributeValue(new Cooking("Pwning", "Awesome"));
            readObjectAndChangeAttributeValue(new Knitting("Knitting", "Still Lame"));
            readObjectAndChangeAttributeValue(null);         
            
            // delete the objects
            deleteObject(original);
            deleteObject(original2);
        } catch (Exception tle) {
            this.tlException = tle;
            this.tlException.printStackTrace();
        }
    }
    
    public void readObjectAndChangeAttributeValue(Skill newSkill) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        Gamer clone = (Gamer) uow.readObject(
                Gamer.class, 
            new ExpressionBuilder().get("id").equal(this.original.getId()));
        
        assertNotNull("The object returned should be not null", clone);
        clone.setSkill(newSkill);
        
        uow.commit();
    }
    
    public void reset() {
        super.reset();
        this.original = null;
        this.original2 = null;
    }
    
}
