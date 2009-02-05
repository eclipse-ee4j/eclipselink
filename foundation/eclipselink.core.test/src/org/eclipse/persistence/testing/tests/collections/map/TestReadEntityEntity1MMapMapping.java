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
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.EEOTMMapValue;;

public class TestReadEntityEntity1MMapMapping extends TestCase {
    
    protected List holders = null;
    
    public void setup(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        EntityEntity1MMapHolder holder = new EntityEntity1MMapHolder();
        EEOTMMapValue value = new EEOTMMapValue();
        value.setId(1);
        value.getHolder().setValue(holder);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        key.setData("data1");
        holder.addEntityToEntityMapItem(key, value);
        uow.registerObject(key);
        
        EEOTMMapValue value2 = new EEOTMMapValue();
        value2.setId(2);
        value2.getHolder().setValue(holder);
        key = new EntityMapKey();
        key.setId(22);
        holder.addEntityToEntityMapItem(key, value2);
        uow.registerObject(holder);
        uow.registerObject(key);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(EntityEntity1MMapHolder.class);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)holders.get(0);
        
        if (holder.getEntityToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(11);
        EEOTMMapValue value = (EEOTMMapValue)holder.getEntityToEntityMap().get(mapKey);
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
        mapKey = (EntityMapKey)getSession().readObject(mapKey);
        if (!mapKey.getData().equals("data1")){
            throw new TestErrorException("EntityMapKey had wrong data");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        List keys = uow.readAllObjects(EEOTMMapValue.class);
        uow.deleteAllObjects(keys);
        uow.commit();
    }

}

