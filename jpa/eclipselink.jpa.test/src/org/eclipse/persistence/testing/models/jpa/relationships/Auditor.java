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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.HashSet;
import java.util.Collection;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;

@Entity
@Table(name="JPA_AUDITOR")
public class Auditor implements java.io.Serializable{
    private Integer id;
    private String name;
    private Collection<Order> orders = new HashSet<Order>();
        
    public Auditor() {}

    public void addOrder(Order anOrder) {
        getOrders().add(anOrder);
        anOrder.setAuditor(this);
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="AUDITOR_TABLE_GENERATOR")
    @TableGenerator(
        name="AUDITOR_TABLE_GENERATOR", 
        table="JPA_AUDITOR_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="AUDITOR_SEQ"
    )
    public Integer getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }
    
    @OneToMany(targetEntity=Order.class, mappedBy="auditor")
    public Collection getOrders() { 
        return orders; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public void setOrders(Collection<Order> newValue) { 
        this.orders = newValue; 
    }

    public void removeOrder(Order anOrder) {
        getOrders().remove(anOrder);
    }
}


