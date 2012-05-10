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
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.AggregateAggregateMapHolder;

public class TestUpdateAggregateAggregateMapMapping extends TestReadAggregateAggregateMapMapping {

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(AggregateAggregateMapHolder.class, holderExp);
        AggregateAggregateMapHolder holder = (AggregateAggregateMapHolder)holders.get(0);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.removeAggregateToAggregateMapItem(key);
        AggregateMapKey mapValue = new AggregateMapKey();
        mapValue.setKey(3);
        key = new AggregateMapKey();
        key.setKey(33);
        holder.addAggregateToAggregateMapItem(key, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(holder);
        if (!compareObjects(holder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object initialHolder = holders.get(0);
        holders = getSession().readAllObjects(AggregateAggregateMapHolder.class, holderExp);
        AggregateAggregateMapHolder holder = (AggregateAggregateMapHolder)holders.get(0);
        if (!compareObjects(holder, initialHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        if (holder.getAggregateToAggregateMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new AggregateMapKey();
        key.setKey(33);
        AggregateMapKey value = (AggregateMapKey)holder.getAggregateToAggregateMap().get(key);
        if (value.getKey() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
