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

public class AggregateMapValue {

   private int value;
    
    public int getValue(){
        return value;
    }
    
    public void setValue(int value){
        this.value = value;
    }
    
    public boolean equals(Object object){
        if (!(object instanceof AggregateMapValue)){
            return false;
        } else {
            return ((AggregateMapValue)object).getValue() == this.value;
        }
    }
    
    public int hashCode(){
        return value;
    }
    
}
