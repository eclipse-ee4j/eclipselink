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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntityMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;

public class TestUpdateAggregateEntityMapMapping extends TestCase {
    
    private AggregateEntityMapHolder holder = null;
    
    
    protected ManyToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    
    public TestUpdateAggregateEntityMapMapping(){
        super();
    }
    
    public TestUpdateAggregateEntityMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateAggregateEntityMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        mapping = (ManyToManyMapping)getSession().getProject().getDescriptor(AggregateEntityMapHolder.class).getMappingForAttributeName("aggregateToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = new AggregateEntityMapHolder();
        EntityMapValue value = new EntityMapValue();
        value.setId(1);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        holder.addAggregateToEntityMapItem(key, value);

        
        EntityMapValue value2 = new EntityMapValue();
        value2.setId(2);
        key = new AggregateMapKey();
        key.setKey(22);
        holder.addAggregateToEntityMapItem(key, value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = (AggregateEntityMapHolder)uow.readObject(holder);
        EntityMapValue value = new EntityMapValue();
        value.setId(3);
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(33);
        holder.addAggregateToEntityMapItem(mapKey, value);
        
        mapKey = new AggregateMapKey();
        mapKey.setKey(11);
        holder.getAggregateToEntityMap().remove(mapKey);
        uow.commit();
        Object holderForComparison = uow.readObject(holder);
        if (!compareObjects(holder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Object changedHolder = holder;
        holder = (AggregateEntityMapHolder)getSession().readObject(holder);
        if (holder == null){
            throw new TestErrorException("AggregateKeyMapHolder could not be read.");
        }
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getAggregateToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        AggregateMapKey mapKey = new AggregateMapKey();
        mapKey.setKey(33);
        EntityMapValue value = (EntityMapValue)holder.getAggregateToEntityMap().get(mapKey);
        if (value.getId() != 3){
            throw new TestErrorException("MapEntityValue was not added properly.");
        }
        mapKey = new AggregateMapKey();
        mapKey.setKey(11);
        value = (EntityMapValue)holder.getAggregateToEntityMap().get(mapKey);
        if (value != null){
            throw new TestErrorException("Deleted EntityMapValue still around.");
        }
        if (mapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(EntityMapValue.class);
            ExpressionBuilder values = new ExpressionBuilder();
            Expression criteria = values.get("id").equal(1);
            query.setSelectionCriteria(criteria);
            value = (EntityMapValue)getSession().executeQuery(query);
            if (value != null){
                throw new TestErrorException("PrivateOwned EntityMapValue was not deleted.");
            }
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Iterator j = holder.getAggregateToEntityMap().keySet().iterator();
        while (j.hasNext()){
            uow.deleteObject(holder.getAggregateToEntityMap().get(j.next()));
        }
        uow.deleteObject(holder);
        uow.commit();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
    }

}
