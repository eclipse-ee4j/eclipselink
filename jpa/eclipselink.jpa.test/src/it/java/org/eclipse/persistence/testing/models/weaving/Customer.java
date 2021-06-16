/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.weaving;

import java.util.Collection;
import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name="CMP3_CUSTOMER")
@NamedQuery(
    name="findAllCustomers",
    query="SELECT OBJECT(thecust) FROM Customer thecust"
)
public class Customer implements java.io.Serializable{

    private Integer customerId;
    private int version;
    private String city;
    private String name;
    private Collection<Order> orders;

    public Customer() {
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="CUSTOMER_TABLE_GENERATOR",
        table="CMP3_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUST_SEQ"
    )
    @Column(name="CUST_ID")
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer id) {
        this.customerId = id;
    }

    @Version
    @Column(name="CUST_VERSION")
    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String aCity) {
        this.city = aCity;
    }


    public String getName() {
        return name;
    }

    public void setName(String aName) {
        this.name = aName;
    }

    @OneToMany(cascade=ALL, mappedBy="customer")
    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> newValue) {
        this.orders = newValue;
    }

    public void addOrder(Order anOrder) {
        getOrders().add(anOrder);
        anOrder.setCustomer(this);
    }

    public void removeOrder(Order anOrder) {
        getOrders().remove(anOrder);
    }
}
