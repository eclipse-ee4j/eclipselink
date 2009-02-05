package org.eclipse.persistence.testing.models.collections.map;

import java.util.HashMap;
import java.util.Map;

public class DirectEntityU1MMapHolder  {

    private int id;
    private Map directToEntityMap = null;
    
    public DirectEntityU1MMapHolder(){
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

        definition.setName("DIR_ENT_U1M_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }

}
