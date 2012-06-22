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

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity(name="OrderCard")
@Table(name="JPA_ORDER_CARD")
public class OrderCard implements java.io.Serializable {
    private Integer id;
    private Order order;

    public OrderCard() {}
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="ORDER_CARD_TABLE_GENERATOR")
    @TableGenerator(
        name="ORDER_CARD_TABLE_GENERATOR", 
        table="CMP3_CUSTOMER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ORDER_CARD_SEQ"
    )
    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }

    @OneToOne
    @JoinTable
    // Use the default join table values.
    public Order getOrder() { 
        return order; 
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
}
