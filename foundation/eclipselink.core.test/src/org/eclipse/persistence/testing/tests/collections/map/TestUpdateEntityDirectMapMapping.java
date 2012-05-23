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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestUpdateEntityDirectMapMapping extends TestReadEntityDirectMapMapping {

    private boolean usePrivateOwned = false;
    protected ForeignReferenceMapping keyMapping = null;
    private boolean oldKeyPrivateOwnedValue = false;
    protected EntityDirectMapHolder changedHolder = null;
    
    public TestUpdateEntityDirectMapMapping(){
        super();
    }
    
    public TestUpdateEntityDirectMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateEntityDirectMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        DirectMapMapping mapping = (DirectMapMapping)getSession().getProject().getDescriptor(EntityDirectMapHolder.class).getMappingForAttributeName("entityToDirectMap");
        keyMapping = (ForeignReferenceMapping)((MappedKeyMapContainerPolicy)mapping.getContainerPolicy()).getKeyMapping();
        oldKeyPrivateOwnedValue = keyMapping.isPrivateOwned();
        keyMapping.setIsPrivateOwned(usePrivateOwned);
        super.setup();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityDirectMapHolder.class, holderExp);
        changedHolder = (EntityDirectMapHolder)holders.get(0);
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        changedHolder.removeEntityToDirectMapItem(mapKey);
        mapKey = new EntityMapKey();
        mapKey.setId(3);
        mapKey.setData("testData");
        mapKey = (EntityMapKey)uow.registerObject(mapKey);
        changedHolder.addEntityDirectMapItem(mapKey, new Integer(3));
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityDirectMapHolder.class, holderExp);
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        if (holder.getEntityToDirectMap().containsKey(mapKey)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        mapKey = new EntityMapKey();
        mapKey.setId(3);
        Integer value = (Integer)holder.getEntityToDirectMap().get(mapKey);
        if (value.intValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (keyMapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(EntityMapKey.class);
            ExpressionBuilder keys = new ExpressionBuilder();
            Expression keycriteria = keys.get("id").equal(1);
            query.setSelectionCriteria(keycriteria);
            mapKey = (EntityMapKey)getSession().executeQuery(query);
            if (mapKey != null){
                throw new TestErrorException("PrivateOwned EntityMapKey was not deleted.");
            }
        }
    }
    
    public void reset(){
        super.reset();
        keyMapping.setIsPrivateOwned(oldKeyPrivateOwnedValue);
    }    
}
