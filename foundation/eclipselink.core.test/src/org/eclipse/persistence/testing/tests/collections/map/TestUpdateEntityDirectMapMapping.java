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
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestUpdateEntityDirectMapMapping extends TestReadEntityDirectMapMapping {

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityDirectMapHolder.class);
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        holder.removeEntityToDirectMapItem(mapKey);
        mapKey = new EntityMapKey();
        mapKey.setId(3);
        mapKey.setData("testData");
        mapKey = (EntityMapKey)uow.registerObject(mapKey);
        holder.addEntityDirectMapItem(mapKey, new Integer(3));
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityDirectMapHolder.class);
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        if (holder.getEntityToDirectMap().containsKey(mapKey)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        mapKey = new EntityMapKey();
        mapKey.setId(3);
        Integer value = (Integer)holder.getEntityToDirectMap().get(mapKey);
        if (value.intValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
