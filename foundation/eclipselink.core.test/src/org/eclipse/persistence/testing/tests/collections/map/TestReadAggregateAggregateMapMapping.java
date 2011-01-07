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

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;

public class TestReadAggregateAggregateMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected AggregateCollectionMapping mapping = null;
    protected Expression holderExp;
    
    public TestReadAggregateAggregateMapMapping(){
        super();
    }
    
    public TestReadAggregateAggregateMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadAggregateAggregateMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (AggregateCollectionMapping)getSession().getProject().getDescriptor(AggregateAggregateMapHolder.class).getMappingForAttributeName("aggregateToAggregateMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(AggregateAggregateMapHolder.class).reInitializeJoinedAttributes();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        AggregateAggregateMapHolder holder = new AggregateAggregateMapHolder();
        AggregateMapKey value = new AggregateMapKey();
        value.setKey(1);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.addAggregateToAggregateMapItem(key, value);

        
        AggregateMapKey value2 = new AggregateMapKey();
        value2.setKey(2);
        key = new AggregateMapKey();
        key.setKey(22);
        holder.addAggregateToAggregateMapItem(key, value2);
        uow.registerObject(holder);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(AggregateAggregateMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        AggregateAggregateMapHolder holder = (AggregateAggregateMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getAggregateToAggregateMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getAggregateToAggregateMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(11);
        AggregateMapKey value = (AggregateMapKey)holder.getAggregateToAggregateMap().get(mapKey);
        if (value.getKey() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        uow.commit();
        if (!verifyDelete(holders.get(0))){
            throw new TestErrorException("Delete was unsuccessful.");
        }
        mapping.setJoinFetch(oldFetchJoinValue);
    }

}

