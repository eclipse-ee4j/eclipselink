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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.Cousin;
import org.eclipse.persistence.testing.models.aggregate.Parent;

/**
 * Test aggregate relationships with a OneToOneMapping
 * EL bug 332080
 */
public class AggregateRelationshipsOneToOneTestCase extends TestCase {
    
    protected Parent originalParent;
    protected Parent readParent;

    public AggregateRelationshipsOneToOneTestCase() {
        super();
        setDescription("AggregateRelationships: test OneToOneMapping");
    }
    
    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalParent = new Parent();
        originalParent.getAggregate().setCousin(new Cousin());
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
        compareObjects(originalParent.getAggregate().getCousin(), readParent.getAggregate().getCousin());
    }
    
    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(originalParent.getAggregate().getCousin());
        uow.deleteObject(originalParent);
        uow.commit();
    }
    
}
