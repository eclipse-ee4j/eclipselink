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
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestReadEntityDirectMapMapping extends TestCase {
    
    protected List holders = null;
    
    public void setup(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        EntityDirectMapHolder holder = new EntityDirectMapHolder();
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        mapKey.setData("11");
        uow.registerObject(mapKey);
        holder.addEntityDirectMapItem(mapKey, new Integer(1));
        EntityMapKey mapKey2 = new EntityMapKey();
        mapKey2.setId(2);
        mapKey2.setData("22");
        uow.registerObject(mapKey2);
        holder.addEntityDirectMapItem(mapKey2, new Integer(2));
        uow.registerObject(holder);
        
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(EntityDirectMapHolder.class);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        
        if (holder.getEntityToDirectMap().size() != 2){
            throw new TestErrorException("Incorrect Number of Map values was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        Integer value = (Integer)holder.getEntityToDirectMap().get(mapKey);
        if (value.intValue() != 1){
            throw new TestErrorException("Incorrect map value was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        List keys = uow.readAllObjects(EntityMapKey.class);
        uow.deleteAllObjects(keys);
        uow.commit();
    }
}

