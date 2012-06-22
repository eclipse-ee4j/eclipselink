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
package org.eclipse.persistence.testing.tests.collections.map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;

public class TestUpdateDirectAggregateMapMapping extends TestReadDirectAggregateMapMapping {

    protected DirectAggregateMapHolder changedHolder = null;
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectAggregateMapHolder.class, holderExp);
        changedHolder = (DirectAggregateMapHolder)holders.get(0);
        changedHolder.removeDirectToAggregateMapItem(new Integer(1));
        AggregateMapValue mapValue = new AggregateMapValue();
        mapValue.setValue(3);
        changedHolder.addDirectToAggregateMapItem(new Integer(3), mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object initialHolder = holders.get(0);
        holders = getSession().readAllObjects(DirectAggregateMapHolder.class, holderExp);
        DirectAggregateMapHolder holder = (DirectAggregateMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getDirectToAggregateMap().containsKey(new Integer(1))){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        AggregateMapValue value = (AggregateMapValue)holder.getDirectToAggregateMap().get(new Integer(3));
        if (value.getValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
