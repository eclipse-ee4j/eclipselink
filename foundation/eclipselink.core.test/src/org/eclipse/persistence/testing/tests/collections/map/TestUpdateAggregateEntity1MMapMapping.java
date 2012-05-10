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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapKey;

public class TestUpdateAggregateEntity1MMapMapping extends TestReadAggregateEntity1MMapMapping{

    
    protected OneToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    protected AggregateEntity1MMapHolder changedHolder = null;
    
    public TestUpdateAggregateEntity1MMapMapping(){
        super();
    }
    
    public TestUpdateAggregateEntity1MMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateAggregateEntity1MMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        ClassDescriptor descriptor = getSession().getProject().getDescriptor(AggregateEntity1MMapHolder.class);
        mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("aggregateToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        super.setup();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(AggregateEntity1MMapHolder.class, holderExp);
        changedHolder = (AggregateEntity1MMapHolder)holders.get(0);
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        changedHolder.removeAggregateToEntityMapItem(key);
        AEOTMMapValue mapValue = new AEOTMMapValue();
        mapValue.setId(3);
        mapValue = (AEOTMMapValue)uow.registerObject(mapValue);
        key = new AggregateMapKey();
        key.setKey(33);
        mapValue.getHolder().setValue(changedHolder);
        changedHolder.addAggregateToEntityMapItem(key, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(AggregateEntity1MMapHolder.class, holderExp);
        AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        if (holder.getAggregateToEntityMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new AggregateMapKey();
        key.setKey(33);
        AEOTMMapValue value = (AEOTMMapValue)holder.getAggregateToEntityMap().get(key);
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (mapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(AEOTMMapValue.class);
            ExpressionBuilder values = new ExpressionBuilder();
            Expression criteria = values.get("id").equal(1);
            query.setSelectionCriteria(criteria);
            value = (AEOTMMapValue)getSession().executeQuery(query);
            if (value != null){
                throw new TestErrorException("PrivateOwned DEOTMMapValue was not deleted.");
            }
        }
    }
    
    public void reset(){
        super.reset();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
    }
}
