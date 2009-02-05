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
import org.eclipse.persistence.testing.models.collections.map.EEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestUpdateEntityEntity1MMapMapping extends TestReadEntityEntity1MMapMapping{

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityEntity1MMapHolder.class);
        EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        holder.removeEntityToEntityMapItem(key);
        EEOTMMapValue mapValue = new EEOTMMapValue();
        mapValue.setId(3);
        mapValue = (EEOTMMapValue)uow.registerObject(mapValue);
        key = new EntityMapKey();
        key.setId(33);
        key = (EntityMapKey)uow.registerObject(key);
        mapValue.getHolder().setValue(holder);
        holder.addEntityToEntityMapItem(key, mapValue);
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityEntity1MMapHolder.class);
        EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        if (holder.getEntityToEntityMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new EntityMapKey();
        key.setId(33);
        EEOTMMapValue value = (EEOTMMapValue)holder.getEntityToEntityMap().get(key);
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
