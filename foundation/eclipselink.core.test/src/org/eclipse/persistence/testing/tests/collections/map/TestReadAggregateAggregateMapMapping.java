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

import java.util.List;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;

public class TestReadAggregateAggregateMapMapping extends TestCase {
    
    protected List holders = null;
    
    public void setup(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        AggregateAggregateMapHolder holder = new AggregateAggregateMapHolder();
        AggregateMapValue value = new AggregateMapValue();
        value.setValue(1);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.addAggregateToAggregateMapItem(key, value);

        
        AggregateMapValue value2 = new AggregateMapValue();
        value2.setValue(2);
        key = new AggregateMapKey();
        key.setKey(22);
        holder.addAggregateToAggregateMapItem(key, value2);
        uow.registerObject(holder);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(AggregateAggregateMapHolder.class);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        AggregateAggregateMapHolder holder = (AggregateAggregateMapHolder)holders.get(0);
        
        if (holder.getAggregateToAggregateMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(11);
        AggregateMapValue value = (AggregateMapValue)holder.getAggregateToAggregateMap().get(mapKey);
        if (value.getValue() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        uow.commit();
    }

}

