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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.*;

/**
 * Test an UpdateAllQuery updating an object with a TimestampLockingPolicy configured
 * to use the default timestamp value (use server time). This was failing since the 
 * session was passed via the ExpressionBuilder, but the session was not set - this
 * caused a NullPointerException.
 * EL bug 247884 - NullPointerException using Timestamp (server) based optimistic locking and UpdateAllQuery
 * @author dminsky
 */
public class UpdateAllWithTimestampLockingTest extends TransactionalTestCase {

    protected int rowsUpdated = 0;
    protected TimestampInObject timestampInObject;

    public UpdateAllWithTimestampLockingTest() {
        super();
        setDescription("Test performing an UpdateAllQuery using an object with a TimestampLockingPolicy");
    }
    
    public void test() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        timestampInObject = (TimestampInObject) getSession().readObject(
            TimestampInObject.class,
            new ExpressionBuilder().get("value").equal(TimestampInObject.example1().value));
        assertNotNull(timestampInObject);

        UpdateAllQuery query = new UpdateAllQuery(TimestampInObject.class);
        query.setShouldDeferExecutionInUOW(false);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("id").equal(timestampInObject.id);
        query.setSelectionCriteria(expression);
        query.addUpdate(builder.get("value"), "Updated Object");

        UnitOfWork uow = getSession().acquireUnitOfWork();

        rowsUpdated = (Integer)uow.executeQuery(query);
        uow.commit();
    }
    
    public void verify() {
        if (rowsUpdated != 1) {
            throw new TestErrorException(rowsUpdated + " rows were updated, expected 1 row to be updated");
        }
        assertNotNull(timestampInObject);
        timestampInObject.verify(this);
    }
    
}
