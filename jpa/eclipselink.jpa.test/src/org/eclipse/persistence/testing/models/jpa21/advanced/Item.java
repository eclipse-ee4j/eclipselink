/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/06/2013-2.5 Guy Pelletier 
 *       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
 *     02/11/2013-2.5 Guy Pelletier 
 *       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with 
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA21_ITEM")
public class Item {
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer itemId;
    
    @Basic
    private String name;

    public Item() {}

    public Integer getItemId() { 
        return itemId; 
    }
    
    public String getName() {
        return name;
    }
    
    public void setItemId(Integer id) { 
        this.itemId = id; 
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return "Item [" + getName() + "]";
    }
}
