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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.collections.*;

public class BatchReadingBatchReadExpressionTest extends TestCase {
    public ReadAllQuery query;
    BatchFetchType batchType;

    /**
     * BatchReadingBatchReadExpressionTest was added for CR#3238.
     * It tests for a memory leak with Batch Read Expressions.
     * In a query with a batch read attribute expression, each time it
     * was executed, its vector containing the expression would grow.
     * It was fixed to that it would always have the correct number of
     * expression.
     * Creation date: (6/9/00 9:48:06 AM)
     * @author Tom Ware
     */
    public BatchReadingBatchReadExpressionTest(BatchFetchType batchType) {
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        query = new ReadAllQuery();
        query.setReferenceClass(Restaurant.class);
        query.setBatchFetchType(batchType);
        ExpressionBuilder restaurant = new ExpressionBuilder();
        // The test is designed for a problem in which the batchExpression traverses
        // more than one mapping as below.
        Expression batchExpression = restaurant.get("menus").get("owner");
        query.addBatchReadAttribute(batchExpression);
        // Execute the query with a batch read attribute several times
        for (int i = 0; i < 3; i++) {
            getSession().executeQuery(query);
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }

    }

    public void verify() {
        // The batchReadAttributeExpressions vector should only ever by one since we only add one expression in the execute() method of this test.
        ForeignReferenceMapping batchMapping = 
            (ForeignReferenceMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("menus");
        ReadAllQuery selectionQuery = (ReadAllQuery)batchMapping.getSelectionQuery();
        if (selectionQuery.getBatchReadAttributeExpressions().size() > 1) {
            throw new TestErrorException("Batch read attribute expressions are leaking memory by increasing the size of the batch read expressions vector each time the query is executed.");
        }
    }
}
