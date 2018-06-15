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
