/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.testing.models.collections.map;

import java.util.HashMap;
import java.util.Map;

public class AggregateAggregateMapHolder {


    private int id;
    private Map aggregateToAggregateMap = null;

    public AggregateAggregateMapHolder(){
        aggregateToAggregateMap = new HashMap();
    }

    public Map getAggregateToAggregateMap(){
        return aggregateToAggregateMap;
    }

    public int getId(){
        return id;
    }

    public void setAggregateToAggregateMap(Map map){
        aggregateToAggregateMap = map;
    }

    public void setId(int id){
        this.id = id;
    }

    public void addAggregateToAggregateMapItem(AggregateMapKey key, AggregateMapKey value){
        aggregateToAggregateMap.put(key, value);
    }

    public void removeAggregateToAggregateMapItem(AggregateMapKey key){
        aggregateToAggregateMap.remove(key);
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("AGG_AGG_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("AGG_AGG_MAP_REL");
        definition.addField("HOLDER_ID", java.math.BigDecimal.class, 15);
        definition.addField("MAP_VALUE", java.math.BigDecimal.class, 15);
        definition.addField("MAP_KEY", java.math.BigDecimal.class, 15);
        definition.addForeignKeyConstraint("AGG_AGG_MAP_REL_HOLDER_FK", "HOLDER_ID", "ID", "AGG_AGG_MAP_HOLDER");

        return definition;
    }
}
