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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;


import javax.persistence.*;

import org.eclipse.persistence.annotations.Cache;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import java.util.Collection;

@Entity(name="FieldAccessSalesPerson")
@Table(name="CMP3_FIELDACCESS_SALESPERSON")
@Cache(shared=false)
public class SalesPerson 
{
    private String name;
    @Id
    @GeneratedValue(strategy=TABLE, generator="FIELDACCESS_SALESPERSON_TABLE_GENERATOR")
    @TableGenerator(
            name="FIELDACCESS_SALESPERSON_TABLE_GENERATOR", 
            table="CMP3_FIELDACCESS_CUSTOMER_SEQ", 
            pkColumnName="SEQ_NAME", 
            valueColumnName="SEQ_COUNT",
            pkColumnValue="SALESPERSON_SEQ"
    )
    @Column(name="ID")
    private int id;
    @OneToMany(cascade=ALL, mappedBy="salesPerson")
    private Collection<Order> orders;

    public SalesPerson() {};

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

    public Collection<Order> getOrders() {
        return orders;
    }
    public void setOrders(Collection orders) {
        this.orders = orders;
    }
}
