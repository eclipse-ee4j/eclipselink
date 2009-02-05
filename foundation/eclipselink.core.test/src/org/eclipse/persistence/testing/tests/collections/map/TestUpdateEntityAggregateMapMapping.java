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
import org.eclipse.persistence.testing.models.collections.map.EntityAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;

public class TestUpdateEntityAggregateMapMapping extends TestReadEntityAggregateMapMapping {

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityAggregateMapHolder.class);
        EntityAggregateMapHolder holder = (EntityAggregateMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(1);
        holder.removeEntityToAggregateMapItem(key);
        AggregateMapValue mapValue = new AggregateMapValue();
        mapValue.setValue(3);
        key = new EntityMapKey();
        key.setId(3);
        key = (EntityMapKey)uow.registerObject(key);
        holder.addEntityToAggregateMapItem(key, mapValue);
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityAggregateMapHolder.class);
        EntityAggregateMapHolder holder = (EntityAggregateMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(1);
        if (holder.getEntityToAggregateMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new EntityMapKey();
        key.setId(3);
        AggregateMapValue value = (AggregateMapValue)holder.getEntityToAggregateMap().get(key);
        if (value.getValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
