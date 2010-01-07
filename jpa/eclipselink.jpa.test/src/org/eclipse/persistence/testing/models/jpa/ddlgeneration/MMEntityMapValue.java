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
 *     07/17/2009 - tware - added tests for DDL generation of maps
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="DDL_MVMM")
public class MMEntityMapValue {

    private int id;
    private List<MapHolder> holders;

    public MMEntityMapValue(){
        holders = new ArrayList<MapHolder>();
    }
    
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToMany(cascade=ALL, mappedBy="mTMMap")
    public List<MapHolder> getHolders() {
        return holders;
    }

    public void setHolders(List<MapHolder> holders) {
        this.holders = holders;
    }
    
     
}

