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
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections.map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.AggregateDirectMapHolder;

public class TestUpdateAggregateDirectMapMapping extends TestReadAggregateDirectMapMapping {

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(AggregateDirectMapHolder.class, holderExp);
        AggregateDirectMapHolder holder = (AggregateDirectMapHolder)holders.get(0);
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(1);
        holder.removeAggregateToDirectMapItem(mapKey);
        mapKey = new AggregateMapKey();
        mapKey.setKey(3);
        holder.addAggregateToDirectMapItem(mapKey, new Integer(3));
        uow.commit();
        Object holderForComparison = uow.readObject(holder);
        if (!compareObjects(holder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }

    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object changedHolder = holders.get(0);
        holders = getSession().readAllObjects(AggregateDirectMapHolder.class, holderExp);
        AggregateDirectMapHolder holder = (AggregateDirectMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(1);
        if (holder.getAggregateToDirectMap().containsKey(mapKey)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        mapKey = new AggregateMapKey();
        mapKey.setKey(3);
        Integer value = (Integer)holder.getAggregateToDirectMap().get(mapKey);
        if (value.intValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
