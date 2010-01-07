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

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class DEOTMMapValue {

    private int id;
    private ValueHolderInterface holder;
    
    public DEOTMMapValue(){
        this.holder = new ValueHolder();
    }
    
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public ValueHolderInterface getHolder(){
        return holder;
    }
    
    public void setHolder(ValueHolderInterface holder){
        this.holder = holder;
    }
    
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DE_OM_ENT_MAP_VALUE");
        definition.addField("ID", java.math.BigDecimal.class, 15);
        definition.addField("HOLDER_ID", Integer.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addForeignKeyConstraint("DE_OM_ENT_MAP_VALUE_FK", "HOLDER_ID", "ID", "DIR_ENT_U1M_MAP_HOLDER");

        return definition;
    }
}
