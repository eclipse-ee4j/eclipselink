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

import java.util.Iterator;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityEntityMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;

public class TestUpdateEntityEntityMapMapping extends TestCase {
    
    private EntityEntityMapHolder holder = null;
    
    protected ManyToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    protected ForeignReferenceMapping keyMapping = null;
    private boolean oldKeyPrivateOwnedValue = false;
    protected EntityEntityMapHolder changedHolder = null;
    
    public TestUpdateEntityEntityMapMapping(){
        super();
    }
    
    public TestUpdateEntityEntityMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateEntityEntityMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        mapping = (ManyToManyMapping)getSession().getProject().getDescriptor(EntityEntityMapHolder.class).getMappingForAttributeName("entityToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        keyMapping = (ForeignReferenceMapping)((MappedKeyMapContainerPolicy)mapping.getContainerPolicy()).getKeyMapping();
        oldKeyPrivateOwnedValue = keyMapping.isPrivateOwned();
        keyMapping.setIsPrivateOwned(usePrivateOwned);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = new EntityEntityMapHolder();
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
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        changedHolder = (EntityEntityMapHolder)uow.readObject(holder);
        EntityMapValue value = new EntityMapValue();
        value.setId(3);
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(33);
        EntityMapKey clonedKey = (EntityMapKey)uow.registerObject(mapKey);
        changedHolder.addEntityToEntityMapItem(clonedKey, value);
        
        mapKey = new EntityMapKey();
        mapKey.setId(11);
        changedHolder.getEntityToEntityMap().remove(mapKey);
        uow.commit();
        Object holderForComparison = uow.readObject(holder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        holder = (EntityEntityMapHolder)getSession().readObject(holder);
        if (holder == null){
            throw new TestErrorException("AggregateKeyMapHolder could not be read.");
        }
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getEntityToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(33);
        EntityMapValue value = (EntityMapValue)holder.getEntityToEntityMap().get(mapKey);
        if (value.getId() != 3){
            throw new TestErrorException("MapEntityValue was not added properly.");
        }
        mapKey = new EntityMapKey();
        mapKey.setId(11);
        value = (EntityMapValue)holder.getEntityToEntityMap().get(mapKey);
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
            query = new ReadObjectQuery(EntityMapKey.class);
            ExpressionBuilder keys = new ExpressionBuilder();
            Expression keycriteria = keys.get("id").equal(11);
            query.setSelectionCriteria(keycriteria);
            EntityMapKey key = (EntityMapKey)getSession().executeQuery(query);
            if (key != null){
                throw new TestErrorException("PrivateOwned EntityMapKey was not deleted.");
            }
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Iterator j = holder.getEntityToEntityMap().keySet().iterator();
        while (j.hasNext()){
            uow.deleteObject(holder.getEntityToEntityMap().get(j.next()));
        }
        uow.deleteObject(holder);
        uow.commit();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
        keyMapping.setIsPrivateOwned(oldKeyPrivateOwnedValue);
    }

}
