/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;


import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import java.util.Collection;

@Entity(name="FieldAccessSalesPerson")
@Table(name="CMP3_FIELDACCESS_SALESPERSON")
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