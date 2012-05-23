package org.eclipse.persistence.testing.tests.collections.map;

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectEntity1MMapHolder;
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
 *     tware - initial implementation
 ******************************************************************************/  
public class InMemoryDirectEntity1MTest extends AutoVerifyTestCase {

    private List results = null;
    
    public void test(){
        getSession().readAllObjects(DirectEntity1MMapHolder.class);
        
        ReadAllQuery query = new ReadAllQuery(DirectEntity1MMapHolder.class);
        ExpressionBuilder holders = query.getExpressionBuilder();
        Expression exp = holders.anyOf("directToEntityMap").mapKey().equal(11);
        query.checkCacheOnly();
        results = (List)getSession().executeQuery(query);
    }
    
    public void verify(){
        if (results.size() != 1){
            throw new TestErrorException("Incorrect number of results.");
        }
    }
}
