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
 *     07/17/2009 - tware - added tests for DDL generation of maps
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.CascadeType.ALL;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
@Table(name="DDL_MAP_HOLDER")
public class MapHolder {

    private int id;
    private Map<EntityMapKey, String> directCollectionMap;
    private Map<EntityMapKey, AggregateMapValue> aggregateCollectionMap;
    private Map<AggregateMapKey, EntityMapValueWithBackPointer> oneToManyMap;
    private Map<Integer, EntityMapValue> unidirectionalOneToManyMap;
    private Map<EntityMapKey, MMEntityMapValue> manyToManyMap;
    private MapHolderEmbeddable mapHolderEmbedded;
    private Map<String, String> stringMap;
    
    public MapHolder(){
        directCollectionMap = new HashMap<EntityMapKey, String>();
        aggregateCollectionMap = new HashMap<EntityMapKey, AggregateMapValue>();
        oneToManyMap = new HashMap<AggregateMapKey, EntityMapValueWithBackPointer>();
        unidirectionalOneToManyMap = new HashMap<Integer, EntityMapValue>();
        manyToManyMap = new HashMap<EntityMapKey, MMEntityMapValue>();
        mapHolderEmbedded = new MapHolderEmbeddable();
        stringMap = new HashMap<String, String>();
    }
    
    @Id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    @ElementCollection
    @CascadeOnDelete
    public Map<EntityMapKey, String> getDCMap() {
        return directCollectionMap;
    }
    public void setDCMap(Map<EntityMapKey, String> directCollectionMap) {
        this.directCollectionMap = directCollectionMap;
    }
    
    @ElementCollection
    @CascadeOnDelete
    public Map<EntityMapKey, AggregateMapValue> getACMap() {
        return aggregateCollectionMap;
    }
    public void setACMap(
            Map<EntityMapKey, AggregateMapValue> aggregateCollectionMap) {
        this.aggregateCollectionMap = aggregateCollectionMap;
    }
    
    @OneToMany(targetEntity=EntityMapValueWithBackPointer.class, cascade=ALL)
    public Map<AggregateMapKey, EntityMapValueWithBackPointer> getOTMMap() {
        return oneToManyMap;
    }
    public void setOTMMap(
            Map<AggregateMapKey, EntityMapValueWithBackPointer> oneToManyMap) {
        this.oneToManyMap = oneToManyMap;
    }
    
    @OneToMany(targetEntity=EntityMapValue.class, cascade=ALL)
    @JoinColumn(name="HOLDER_ID")
    public Map<Integer, EntityMapValue> getUOTMMap() {
        return unidirectionalOneToManyMap;
    }
    public void setUOTMMap(
            Map<Integer, EntityMapValue> unidirectionalOneToManyMap) {
        this.unidirectionalOneToManyMap = unidirectionalOneToManyMap;
    }

    @ManyToMany(cascade={ALL})
    public Map<EntityMapKey, MMEntityMapValue> getMTMMap() {
        return manyToManyMap;
    }
    public void setMTMMap(Map<EntityMapKey, MMEntityMapValue> manyToManyMap) {
        this.manyToManyMap = manyToManyMap;
    }
    
    public MapHolderEmbeddable getMapHolderEmbedded() {
        return this.mapHolderEmbedded;
    }
    public void setMapHolderEmbedded(MapHolderEmbeddable mapHolderEmbedded) {
        this.mapHolderEmbedded = mapHolderEmbedded;
    }   
    
    @BatchFetch(BatchFetchType.JOIN)
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="DDL_MAP_HOLDER_STRING_MAP")
    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }
}

