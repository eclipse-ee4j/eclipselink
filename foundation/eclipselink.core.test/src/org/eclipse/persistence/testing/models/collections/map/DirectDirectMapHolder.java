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

import java.util.HashMap;
import java.util.Map;

public class DirectDirectMapHolder {

    private int id;
    private Map directToDirectMap = null;
    
    public DirectDirectMapHolder(){
        directToDirectMap = new HashMap();
    }

    public Map getDirectToDirectMap(){
        return directToDirectMap;
    }
    
    public int getId(){
        return id;
    }
    
    public void setDirectToDirectMap(Map map){
        directToDirectMap = map;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void addDirectToDirectMapItem(Integer key, Integer value){
        directToDirectMap.put(key, value);
    }
    
    public void removeDirectToDirectMapItem(Integer key){
        directToDirectMap.remove(key);
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_DIR_MAP_HOLDER");
        definition.addField("ID", Integer.class, 15);

        return definition;
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_DIR_MAP_REL");
        definition.addField("HOLDER_ID", Integer.class, 15);
        definition.addField("MAP_VALUE", Integer.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addForeignKeyConstraint("DIR_DIR_MAP_REL_FK", "HOLDER_ID", "ID", "DIR_DIR_MAP_HOLDER");

        return definition;
    }
}
