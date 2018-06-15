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
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.casesensitivity;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This class serves as a bridge between {@link org.eclipse.persistence.testing.jaxb.casesensitivity.correctCase.CustomerImpl}
 * and {@link org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl}.
 * <p>Provides uniform bridged <i>equals()<i/> and <i>hashCode()<i/> methods.</p>
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@XmlTransient
public abstract class Customer {

    /**
     * Getter for customer's id.
     * A part of bridge allowing to compare camel case implementation and upper case implementation of Customer.
     *
     * @return id
     */
    public abstract int getIdBridge();

    /**
     * Getter for customer's age.
     * A part of bridge allowing to compare camel case implementation and upper case implementation of Customer.
     *
     * @return id
     */
    public abstract int getAgeBridge();

    /**
     * Getter for customer's name.
     * A part of bridge allowing to compare camel case implementation and upper case implementation of Customer.
     *
     * @return id
     */
    public abstract String getNameBridge();

    /**
     * Bridge for {@link org.eclipse.persistence.testing.jaxb.casesensitivity.correctCase.CustomerImpl#equals(Object)} and
     * {@link org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl#equals(Object)}
     *
     * @param o Employee object.
     * @return true if the Employee classes' attributes match the same values.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass().getSuperclass() != o.getClass().getSuperclass()) return false;

        Customer customer = (Customer) o;

        if (getIdBridge() != customer.getIdBridge()) return false;
        if (getAgeBridge() != customer.getAgeBridge()) return false;
        if (getNameBridge() != null ? !getNameBridge().equals(customer.getNameBridge()) : customer.getNameBridge() != null)
            return false;

        return true;
    }

    /**
     * Bridge for {@link org.eclipse.persistence.testing.jaxb.casesensitivity.correctCase.CustomerImpl#hashCode()} and
     * {@link org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase.CustomerImpl#hashCode()}
     *
     * @return hashCode
     */
    public int hashCode() {
        int result = getNameBridge() != null ? getNameBridge().hashCode() : 0;
        result = 31 * result + getAgeBridge();
        result = 31 * result + getIdBridge();
        return result;
    }
}
