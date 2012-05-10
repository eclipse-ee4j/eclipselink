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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.Parent;
import org.eclipse.persistence.testing.models.aggregate.Relative;

/**
 * Test aggregate relationships with a ManyToManyMapping
 * EL bug 332080
 */
public class AggregateRelationshipsManyToManyTestCase extends TestCase {
    
    protected Parent originalParent;
    protected Parent readParent;

    public AggregateRelationshipsManyToManyTestCase() {
        super();
        setDescription("AggregateRelationships: test ManyToManyMapping");
    }
    
    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        originalParent = new Parent();
        Relative relative1 = new Relative();
        Relative relative2 = new Relative();
        Relative relative3 = new Relative();
        originalParent.addRelative(relative1);
        originalParent.addRelative(relative2);
        originalParent.addRelative(relative3);
        
        uow.registerObject(originalParent);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
    
    public void test() {
        int id = originalParent.getId();
        try {
            readParent = (Parent) getSession().readObject(
                Parent.class, 
                new ExpressionBuilder().get("id").equal(id));
        } catch (EclipseLinkException exception) {
            throwError("An exception occurred whilst reading back a Parent object with id " + id, exception);
        }
    }
    
    public void verify() {
        assertNotNull("Parent read back should not be null", readParent);
        compareObjects(originalParent, readParent);
        assertEquals(originalParent.getAggregate().getRelatives().size(), readParent.getAggregate().getRelatives().size());
    }
    
    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Parent parent = (Parent)uow.registerObject(originalParent);
        List<Relative> relatives = new ArrayList<Relative>(parent.getAggregate().getRelatives());
        for (Iterator<Relative> iterator = relatives.iterator(); iterator.hasNext();) {
            Relative relative = iterator.next();
            parent.removeRelative(relative);
            uow.deleteObject(relative);
        }
        uow.deleteObject(originalParent);
        uow.commit();
    }
    
}
