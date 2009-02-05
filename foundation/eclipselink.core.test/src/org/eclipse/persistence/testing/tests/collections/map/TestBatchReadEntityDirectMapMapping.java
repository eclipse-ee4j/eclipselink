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
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestBatchReadEntityDirectMapMapping extends TestReadEntityDirectMapMapping {
    
    public void test(){
        ReadAllQuery query = new ReadAllQuery(EntityDirectMapHolder.class);
        Expression exp = (new ExpressionBuilder()).get("id").greaterThan(0);
        query.setSelectionCriteria(exp);
        query.addBatchReadAttribute("entityToDirectMap");
        holders = (Vector)getSession().executeQuery(query);
    }
    
    // note: here we just test that the batch read query is successful, not that the batch reading actually occured
    public void verify(){
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        if (holder.getEntityToDirectMap().get(mapKey) == null){
            throw new TestErrorException("BatchReadQuery did not correctly travers relationship EntityToDirectMap on EntityDirectMapHolder");
        }
    }
    
}
