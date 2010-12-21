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
package org.eclipse.persistence.testing.models.collections.map;

import java.util.Map;
import java.util.HashMap;

public class DirectEntityMapHolder {

    private int id;
    private Map directToEntityMap = null;
    
    public DirectEntityMapHolder(){
        directToEntityMap = new HashMap();
    }

    public Map getDirectToEntityMap(){
        return directToEntityMap;
    }
    
    public int getId(){
        return id;
    }
    
    public void setDirectToEntityMap(Map map){
        directToEntityMap = map;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void addDirectToEntityMapItem(Integer key, EntityMapValue value){
        directToEntityMap.put(key, value);
    }
    
    public void removeDirectToEntityMapItem(Integer key){
        directToEntityMap.remove(key);
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_ENT_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_ENT_MAP_REL");
        definition.addField("HOLDER_ID", java.math.BigDecimal.class, 15);
        definition.addField("VALUE_ID", java.math.BigDecimal.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addForeignKeyConstraint("DIR_ENT_MAP_REL_FK", "HOLDER_ID", "ID", "DIR_ENT_MAP_HOLDER");
        definition.addForeignKeyConstraint("DIR_ENT_MAP_REL_VALUE_FK", "VALUE_ID", "ID", "ENT_MAP_VALUE");

        return definition;
    }
}
