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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;

public class TestUpdateAggregateEntity1MMapMapping extends TestReadAggregateEntity1MMapMapping{

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(AggregateEntity1MMapHolder.class);
        AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)holders.get(0);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.removeAggregateToEntityMapItem(key);
        AEOTMMapValue mapValue = new AEOTMMapValue();
        mapValue.setId(3);
        mapValue = (AEOTMMapValue)uow.registerObject(mapValue);
        key = new AggregateMapKey();
        key.setKey(33);
        mapValue.getHolder().setValue(holder);
        holder.addAggregateToEntityMapItem(key, mapValue);
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(AggregateEntity1MMapHolder.class);
        AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)holders.get(0);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        if (holder.getAggregateToEntityMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new AggregateMapKey();
        key.setKey(33);
        AEOTMMapValue value = (AEOTMMapValue)holder.getAggregateToEntityMap().get(key);
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
