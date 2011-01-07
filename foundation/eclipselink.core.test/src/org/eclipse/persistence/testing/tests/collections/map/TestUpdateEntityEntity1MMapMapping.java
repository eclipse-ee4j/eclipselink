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
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestUpdateEntityEntity1MMapMapping extends TestReadEntityEntity1MMapMapping{
    
    protected OneToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldValuePrivateOwnedValue = false;
    protected ForeignReferenceMapping keyMapping = null;
    private boolean oldKeyPrivateOwnedValue = false;
    protected EntityEntity1MMapHolder changedHolder = null;
    
    public TestUpdateEntityEntity1MMapMapping(){
        super();
    }
    
    public TestUpdateEntityEntity1MMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateEntityEntity1MMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(EntityEntity1MMapHolder.class).getMappingForAttributeName("entityToEntityMap");
        oldValuePrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        keyMapping = (ForeignReferenceMapping)((MappedKeyMapContainerPolicy)mapping.getContainerPolicy()).getKeyMapping();
        oldKeyPrivateOwnedValue = keyMapping.isPrivateOwned();
        keyMapping.setIsPrivateOwned(usePrivateOwned);
        
        super.setup();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityEntity1MMapHolder.class, holderExp);
        changedHolder = (EntityEntity1MMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        changedHolder.removeEntityToEntityMapItem(key);
        EEOTMMapValue mapValue = new EEOTMMapValue();
        mapValue.setId(3);
        mapValue = (EEOTMMapValue)uow.registerObject(mapValue);
        key = new EntityMapKey();
        key.setId(33);
        key = (EntityMapKey)uow.registerObject(key);
        mapValue.getHolder().setValue(changedHolder);
        changedHolder.addEntityToEntityMapItem(key, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityEntity1MMapHolder.class, holderExp);
        EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        if (holder.getEntityToEntityMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new EntityMapKey();
        key.setId(33);
        EEOTMMapValue value = (EEOTMMapValue)holder.getEntityToEntityMap().get(key);
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (mapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(EEOTMMapValue.class);
            ExpressionBuilder values = new ExpressionBuilder();
            Expression criteria = values.get("id").equal(1);
            query.setSelectionCriteria(criteria);
            value = (EEOTMMapValue)getSession().executeQuery(query);
            if (value != null){
                throw new TestErrorException("PrivateOwned DEOTMMapValue was not deleted.");
            }
            query = new ReadObjectQuery(EntityMapKey.class);
            ExpressionBuilder keys = new ExpressionBuilder();
            Expression keycriteria = keys.get("id").equal(11);
            query.setSelectionCriteria(keycriteria);
            key = (EntityMapKey)getSession().executeQuery(query);
            if (key != null){
                throw new TestErrorException("PrivateOwned EntityMapKey was not deleted.");
            }
        }
    }
    
    public void reset(){
        super.reset();
        mapping.setIsPrivateOwned(oldValuePrivateOwnedValue);
        keyMapping.setIsPrivateOwned(oldKeyPrivateOwnedValue);
    }
    
}
