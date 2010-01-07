/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;

public class TestReadAggregateDirectMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected DirectCollectionMapping mapping = null;
    protected Expression holderExp;
    
    public TestReadAggregateDirectMapMapping(){
        super();
    }
    
    public TestReadAggregateDirectMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadAggregateDirectMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (DirectCollectionMapping)getSession().getProject().getDescriptor(AggregateDirectMapHolder.class).getMappingForAttributeName("aggregateToDirectMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(AggregateDirectMapHolder.class).reInitializeJoinedAttributes();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        AggregateDirectMapHolder holder = new AggregateDirectMapHolder();
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(1);
        holder.addAggregateToDirectMapItem(mapKey, new Integer(1));
        AggregateMapKey mapKey2 = new AggregateMapKey();
        mapKey2.setKey(2);
        holder.addAggregateToDirectMapItem(mapKey2, new Integer(2));
        uow.registerObject(holder);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(AggregateDirectMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        AggregateDirectMapHolder holder = (AggregateDirectMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getAggregateToDirectMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getAggregateToDirectMap().size() != 2){
            throw new TestErrorException("Incorrect Number of Map values was read.");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(1);
        Integer value = (Integer)holder.getAggregateToDirectMap().get(mapKey);
        if (value.intValue() != 1){
            throw new TestErrorException("Incorrect map value was read.");
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
