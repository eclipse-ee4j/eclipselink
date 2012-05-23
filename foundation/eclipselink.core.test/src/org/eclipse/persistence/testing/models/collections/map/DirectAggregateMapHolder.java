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
package org.eclipse.persistence.testing.models.collections.map;

import java.util.HashMap;
import java.util.Map;

public class DirectAggregateMapHolder {

    private int id;
    private Map directToAggregateMap = null;
    
    public DirectAggregateMapHolder(){
        directToAggregateMap = new HashMap();
    }

    public Map getDirectToAggregateMap(){
        return directToAggregateMap;
    }
    
    public int getId(){
        return id;
    }
    
    public void setDirectToAggregateMap(Map map){
        directToAggregateMap = map;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void addDirectToAggregateMapItem(Integer key, AggregateMapValue value){
        directToAggregateMap.put(key, value);
    }
    
    public void removeDirectToAggregateMapItem(Integer key){
        directToAggregateMap.remove(key);
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_AGG_MAP_HOLDER");
        definition.addField("ID", Integer.class, 15);

        return definition;
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_AGG_MAP_REL");
        definition.addField("HOLDER_ID", Integer.class, 15);
        definition.addField("MAP_VALUE", Integer.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addForeignKeyConstraint("DIR_AGG_MAP_REL_HOLDER_FK", "HOLDER_ID", "ID", "DIR_AGG_MAP_HOLDER");

        return definition;
    }

}

