/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     jlamande    - Initial API and implementation
//     Tomas Kraus - EclipseLink jUnit tests integration
package org.eclipse.persistence.testing.models.jpa.advanced.customer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Registered customer entity.
 */
@Entity
@Table(name="CMP3_AD_CU_CUSTOMER")
@SequenceGenerator(name="CMP3_AD_CU_CUSTOMER_ID_SEQ")
@NamedQueries({
    @NamedQuery(name="RegisteredCustomer.selecAll",
            query="SELECT c FROM RegisteredCustomer c"),

    @NamedQuery(name="RegisteredCustomer.selecByName",
            query="SELECT c FROM RegisteredCustomer c"
                + " WHERE c.name = :name"),

    @NamedQuery(name="RegisteredCustomer.selecByNameFetch",
            query="SELECT c FROM RegisteredCustomer c"
                + "  LEFT JOIN FETCH c.deliveryAddress"
                + "  LEFT JOIN FETCH c.billingAddress"
                + " WHERE c.name = :name")
})
public class RegisteredCustomer {

    /** Customer primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CMP3_AD_CU_CUSTOMER_ID_SEQ")
    @Column(name="ID")
    private long id;

    /** Customer name. */
    @Column(name="NAME")
    private String name;

    /** Customer billing address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="ADDR_BA_ID", referencedColumnName="ID")
    private CustomerAddress billingAddress;

    /** Customer delivery address. */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="ADDR_DA_ID", referencedColumnName="ID")
    private CustomerAddress deliveryAddress;

    /**
     * Creates an instance of {@code RegisteredCustomer} entity.
     * No entity attributes are set.
     */
    public RegisteredCustomer() {
    }

    /**
     * Creates an instance of {@code RegisteredCustomer} entity.
     * @param name            Customer name to be set.
     * @param billingAddress  Customer billing address to be set.
     * @param deliveryAddress Customer delivery address to be set.
     */
    public RegisteredCustomer(final String name, final CustomerAddress billingAddress,
            final CustomerAddress deliveryAddress) {
        this.name = name;
        this.billingAddress = billingAddress;
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Get Customer primary key.
     * @return Customer primary key attribute value.
     */
    public long getId() {
        return id;
    }

    /**
     * Get Customer name.
     * @return Customer name attribute value.
     */
    public String getName() {
        return name;
    }

    /**
     * Set Customer name.
     * @return Customer name to be set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get Customer billing address.
     * @return Customer billing address attribute value.
     */
    public CustomerAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * Set Customer billing address.
     * @param billingAddress Customer billing address to be set.
     */
    public void setBillingAddress(final CustomerAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * Get Customer delivery address.
     * @return Customer delivery address attribute value.
     */
    public CustomerAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Set Customer delivery address.
     * @param deliveryAddress Customer delivery address to be set.
     */
    public void setDeliveryAddress(final CustomerAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Return Customer entity in human readable form.
     * @return Entity converted to {@link String} in human readable form.
     */
    @Override
    public String toString() {
        return "RegisteredCustomer [" + id + "," + name + "," + billingAddress + "," + deliveryAddress + "]";
    }

}
