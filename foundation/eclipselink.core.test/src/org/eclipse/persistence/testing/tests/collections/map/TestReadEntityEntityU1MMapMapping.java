/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;

public class TestReadEntityEntityU1MMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected OneToManyMapping mapping = null;
    protected Expression holderExp;
    
    
    public TestReadEntityEntityU1MMapMapping(){
        super();
    }
    
    public TestReadEntityEntityU1MMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadEntityEntityU1MMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (UnidirectionalOneToManyMapping)getSession().getProject().getDescriptor(EntityEntityU1MMapHolder.class).getMappingForAttributeName("entityToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(EntityEntityU1MMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        EntityEntityU1MMapHolder holder = new EntityEntityU1MMapHolder();
        EntityMapValue value = new EntityMapValue();
        value.setId(1);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        key.setData("data1");
        holder.addEntityToEntityMapItem(key, value);
        uow.registerObject(key);
        
        EntityMapValue value2 = new EntityMapValue();
        value2.setId(2);
        key = new EntityMapKey();
        key.setId(22);
        holder.addEntityToEntityMapItem(key, value2);
        uow.registerObject(holder);
        uow.registerObject(key);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(EntityEntityU1MMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        EntityEntityU1MMapHolder holder = (EntityEntityU1MMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getEntityToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship > 0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        
        if (holder.getEntityToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(11);
        EntityMapValue value = (EntityMapValue)holder.getEntityToEntityMap().get(mapKey);
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
        Iterator i = holders.iterator();
        while (i.hasNext()){
            EntityEntityU1MMapHolder holder = (EntityEntityU1MMapHolder)i.next();
            Iterator j = holder.getEntityToEntityMap().keySet().iterator();
            while (j.hasNext()){
                Object key = j.next();
                uow.deleteObject(holder.getEntityToEntityMap().get(key));
                uow.deleteObject(key);
            }
        }
        uow.deleteAllObjects(holders);
        uow.commit();
        if (!verifyDelete(holders.get(0))){
            throw new TestErrorException("Delete was unsuccessful.");
        }
        mapping.setJoinFetch(oldFetchJoinValue);
    }

}

