/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.testing.models.collections.map;

import java.util.HashMap;
import java.util.Map;

public class AggregateEntity1MMapHolder {

    private int id;
    private Map aggregateToEntityMap = null;

    public AggregateEntity1MMapHolder(){
        aggregateToEntityMap = new HashMap();
    }

    public Map getAggregateToEntityMap(){
        return aggregateToEntityMap;
    }

    public int getId(){
        return id;
    }

    public void setAggregateToEntityMap(Map map){
        aggregateToEntityMap = map;
    }

    public void setId(int id){
        this.id = id;
    }

    public void addAggregateToEntityMapItem(AggregateMapKey key, AEOTMMapValue value){
        aggregateToEntityMap.put(key, value);
    }

    public void removeAggregateToEntityMapItem(AggregateMapKey key){
        aggregateToEntityMap.remove(key);
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("AGG_ENT_1M_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }


}
