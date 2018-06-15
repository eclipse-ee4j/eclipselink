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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;

import javax.persistence.*;

@Entity(name="FieldAccessCustomer2")
@Table(name="CMP3_FIELDACCESS_CUSTOMER2")
public class Customer2 implements java.io.Serializable {
    @Id
    @GeneratedValue
    @Column(name="CUST_ID")
    private Integer customerId;

    @Version
    @Column(name="CUST_VERSION")
    private int version;

    private String name;

    public Customer2() {}

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer id) {
        this.customerId = id;
    }

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
            this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        this.name = aName;
    }
}
