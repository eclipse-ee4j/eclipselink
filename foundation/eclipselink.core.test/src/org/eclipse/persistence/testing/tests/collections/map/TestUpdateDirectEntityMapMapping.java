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
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityMapHolder;

public class TestUpdateDirectEntityMapMapping extends TestCase {
    
    private DirectEntityMapHolder holder = null;
    
    public void setup(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = new DirectEntityMapHolder();
        EntityMapValue value = new EntityMapValue();
        value.setId(1);
        holder.addDirectToEntityMapItem(new Integer(11), value);

        
        EntityMapValue value2 = new EntityMapValue();
        value2.setId(2);
        holder.addDirectToEntityMapItem(new Integer(22), value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = (DirectEntityMapHolder)uow.readObject(holder);
        EntityMapValue value = new EntityMapValue();
        value.setId(3);
        holder.addDirectToEntityMapItem(new Integer(33), value);
        
        holder.getDirectToEntityMap().remove(new Integer(11));
        uow.commit();
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        holder = (DirectEntityMapHolder)getSession().readObject(holder);
        if (holder.getDirectToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapValue value = (EntityMapValue)holder.getDirectToEntityMap().get(new Integer(33));
        if (value.getId() != 3){
            throw new TestErrorException("MapEntityValue was not added properly.");
        }
        value = (EntityMapValue)holder.getDirectToEntityMap().get(11);
        if (value != null){
            throw new TestErrorException("Deleted EntityMapValue still around.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(holder);
        List keys = uow.readAllObjects(EntityMapValue.class);
        uow.deleteAllObjects(keys);
        uow.commit();
    }

}
