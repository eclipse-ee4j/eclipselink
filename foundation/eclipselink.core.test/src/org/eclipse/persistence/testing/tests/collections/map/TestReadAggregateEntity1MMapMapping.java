/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.AEOTMMapValue;

public class TestReadAggregateEntity1MMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected OneToManyMapping mapping = null;
    protected Expression holderExp;
    
    public TestReadAggregateEntity1MMapMapping(){
        super();
    }
    
    public TestReadAggregateEntity1MMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadAggregateEntity1MMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(AggregateEntity1MMapHolder.class).getMappingForAttributeName("aggregateToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(AggregateEntity1MMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        AggregateEntity1MMapHolder holder = new AggregateEntity1MMapHolder();
        AEOTMMapValue value = new AEOTMMapValue();
        value.setId(1);
        value.getHolder().setValue(holder);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.addAggregateToEntityMapItem(key, value);

        
        AEOTMMapValue value2 = new AEOTMMapValue();
        value2.setId(2);
        value2.getHolder().setValue(holder);
        key = new AggregateMapKey();
        key.setKey(22);
        holder.addAggregateToEntityMapItem(key, value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(AggregateEntity1MMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getAggregateToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship > 0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getAggregateToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(11);
        AEOTMMapValue value = (AEOTMMapValue)holder.getAggregateToEntityMap().get(mapKey);
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Iterator i = holders.iterator();
        while (i.hasNext()){
            AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)i.next();
            Iterator j = holder.getAggregateToEntityMap().keySet().iterator();
            while (j.hasNext()){
                uow.deleteObject(holder.getAggregateToEntityMap().get(j.next()));
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

