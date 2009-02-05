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
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityU1MMapHolder;

public class TestUpdateDirectEntityU1MMapMapping extends TestReadDirectEntityU1MMapMapping{

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectEntityU1MMapHolder.class);
        DirectEntityU1MMapHolder holder = (DirectEntityU1MMapHolder)holders.get(0);
        holder.removeDirectToEntityMapItem(new Integer(11));
        EntityMapValue mapValue = new EntityMapValue();
        mapValue.setId(3);
        mapValue = (EntityMapValue)uow.registerObject(mapValue);
        holder.addDirectToEntityMapItem(new Integer(33), mapValue);
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(DirectEntityU1MMapHolder.class);
        DirectEntityU1MMapHolder holder = (DirectEntityU1MMapHolder)holders.get(0);
        if (holder.getDirectToEntityMap().containsKey(new Integer(1))){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        EntityMapValue value = (EntityMapValue)holder.getDirectToEntityMap().get(new Integer(33));
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }
    
}
