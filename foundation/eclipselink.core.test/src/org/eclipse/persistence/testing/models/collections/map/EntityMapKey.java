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

public class EntityMapKey {

    private int id;
    private String data;
    
    public String getData(){
        return data;
    }
    
    public int getId(){
        return id;
    }
    
    public void setData(String data){
        this.data = data;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public boolean equals(Object object){
        if (!(object instanceof EntityMapKey)){
            return false;
        } else {
            return ((EntityMapKey)object).getId() == this.id;
        }
    }
    
    public int hashCode(){
        return id;
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("ENT_MAP_KEY");
        definition.addField("ID", java.lang.Integer.class, 15);
        definition.addField("DATA", String.class, 15);

        return definition;
    }
    
}
