/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.3 - test automation
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Entity
@Table(name = "PHONENUMBER")
public class PhoneNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String num;

    private String type;

    @ManyToOne
    @JoinColumn(name = "ID_CUSTOMER")
    private Customer customer;

    public PhoneNumber() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlInverseReference(mappedBy = "phoneNumbers")
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean equals(Object object) {
        if (null == object || object.getClass() != this.getClass()) {
            return false;
        }
        PhoneNumber test = ((PhoneNumber) object);
        if (!equals(id, test.getId())) {
            return false;
        }
        if (!equals(num, test.getNum())) {
            return false;
        }
        if (!equals(type, test.getType())) {
            return false;
        }
        if (null == test.getCustomer() && null != customer) {
            return false;
        }
        if (null != test.getCustomer() && null == customer) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if (null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
