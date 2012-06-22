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
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityU1MMapHolder;

public class TestUpdateDirectEntityU1MMapMapping extends TestReadDirectEntityU1MMapMapping{
    
    protected OneToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    protected DirectEntityU1MMapHolder changedHolder = null;
    
    public TestUpdateDirectEntityU1MMapMapping(){
        super();
    }
    
    public TestUpdateDirectEntityU1MMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateDirectEntity1MMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(DirectEntityU1MMapHolder.class).getMappingForAttributeName("directToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        super.setup();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectEntityU1MMapHolder.class, holderExp);
        changedHolder = (DirectEntityU1MMapHolder)holders.get(0);
        changedHolder.removeDirectToEntityMapItem(new Integer(11));
        EntityMapValue mapValue = new EntityMapValue();
        mapValue.setId(3);
        mapValue = (EntityMapValue)uow.registerObject(mapValue);
        changedHolder.addDirectToEntityMapItem(new Integer(33), mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(DirectEntityU1MMapHolder.class, holderExp);
        DirectEntityU1MMapHolder holder = (DirectEntityU1MMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getDirectToEntityMap().containsKey(new Integer(1))){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        EntityMapValue value = (EntityMapValue)holder.getDirectToEntityMap().get(new Integer(33));
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
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
        super.reset();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
    }
}
