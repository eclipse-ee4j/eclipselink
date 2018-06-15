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
//     07/17/2009 - tware - added tests for DDL generation of maps
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.*;

@Embeddable
public class AggregateMapKey {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object object){
        if (object == null || !(object instanceof AggregateMapKey)){
            return false;
        }
        return description.equals(((AggregateMapKey)object).getDescription());
    }

    public int hashCode(){
        return description.hashCode();
    }

}
