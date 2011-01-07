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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityAggregateMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;

public class TestUpdateEntityAggregateMapMapping extends TestReadEntityAggregateMapMapping {
    
    private boolean usePrivateOwned = false;
    protected ForeignReferenceMapping keyMapping = null;
    private boolean oldKeyPrivateOwnedValue = false;
    protected EntityAggregateMapHolder changedHolder = null;
    
    public TestUpdateEntityAggregateMapMapping(){
        super();
    }
    
    public TestUpdateEntityAggregateMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateEntityAggregateMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        AggregateCollectionMapping mapping = (AggregateCollectionMapping)getSession().getProject().getDescriptor(EntityAggregateMapHolder.class).getMappingForAttributeName("entityToAggregateMap");
        keyMapping = (ForeignReferenceMapping)((MappedKeyMapContainerPolicy)mapping.getContainerPolicy()).getKeyMapping();
        oldKeyPrivateOwnedValue = keyMapping.isPrivateOwned();
        keyMapping.setIsPrivateOwned(usePrivateOwned);
        super.setup();
    }
    
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityAggregateMapHolder.class, holderExp);
        changedHolder = (EntityAggregateMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(1);
        changedHolder.removeEntityToAggregateMapItem(key);
        AggregateMapValue mapValue = new AggregateMapValue();
        mapValue.setValue(3);
        key = new EntityMapKey();
        key.setId(3);
        key = (EntityMapKey)uow.registerObject(key);
        changedHolder.addEntityToAggregateMapItem(key, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityAggregateMapHolder.class, holderExp);
        EntityAggregateMapHolder holder = (EntityAggregateMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        EntityMapKey key = new EntityMapKey();
        key.setId(1);
        if (holder.getEntityToAggregateMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new EntityMapKey();
        key.setId(3);
        AggregateMapValue value = (AggregateMapValue)holder.getEntityToAggregateMap().get(key);
        if (value.getValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (keyMapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(EntityMapKey.class);
            ExpressionBuilder keys = new ExpressionBuilder();
            Expression keycriteria = keys.get("id").equal(1);
            query.setSelectionCriteria(keycriteria);
            key = (EntityMapKey)getSession().executeQuery(query);
            if (key != null){
                throw new TestErrorException("PrivateOwned EntityMapKey was not deleted.");
            }
        }
    }
    
    public void reset(){
        super.reset();
        keyMapping.setIsPrivateOwned(oldKeyPrivateOwnedValue);
    }    
}
