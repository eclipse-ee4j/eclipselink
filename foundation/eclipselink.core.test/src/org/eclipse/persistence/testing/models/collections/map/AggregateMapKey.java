/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

public class AggregateMapKey {

    private int key;
    
    public int getKey(){
        return key;
    }
    
    public void setKey(int key){
        this.key = key;
    }
    
    public boolean equals(Object object){
        if (!(object instanceof AggregateMapKey)){
            return false;
        } else {
            return ((AggregateMapKey)object).getKey() == this.key;
        }
    }
    
    public int hashCode(){
        return key;
    }

}
