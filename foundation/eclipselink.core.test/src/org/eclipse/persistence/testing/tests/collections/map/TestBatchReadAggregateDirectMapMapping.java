/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;

public class TestBatchReadAggregateDirectMapMapping extends TestReadAggregateDirectMapMapping {

    
    public void test(){
        ReadAllQuery query = new ReadAllQuery(AggregateDirectMapHolder.class);
        Expression exp = (new ExpressionBuilder()).get("id").greaterThan(0);
        query.setSelectionCriteria(exp);
        query.addBatchReadAttribute("aggregateToDirectMap");
        holders = (Vector)getSession().executeQuery(query);
    }
    
    // note: here we just test that the batch read query is successful, not that the batch reading actually occured
    public void verify(){
       
        AggregateDirectMapHolder holder = (AggregateDirectMapHolder)holders.get(0);
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(1);
        if (holder.getAggregateToDirectMap().get(mapKey) == null){
            throw new TestErrorException("BatchReadQuery did not correctly travers relationship AggregateToDirectMap on AggregateDirectMapHolder");
        }
    }
    
}
