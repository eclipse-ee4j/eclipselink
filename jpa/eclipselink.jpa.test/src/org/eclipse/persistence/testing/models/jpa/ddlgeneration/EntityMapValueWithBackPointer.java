package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

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
 *     07/17/2009 - tware - added tests for DDL generation of maps
 ******************************************************************************/  
import javax.persistence.*;

@Entity
@Table(name="DDL_MVBP")
public class EntityMapValueWithBackPointer {

    private int id;
    private MapHolder holder;

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToOne
    public MapHolder getHolder() {
        return holder;
    }

    public void setHolder(MapHolder holder) {
        this.holder = holder;
    }
}
