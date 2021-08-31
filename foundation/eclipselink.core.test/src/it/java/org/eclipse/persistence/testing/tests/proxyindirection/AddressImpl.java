/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Address implementation.
 *
 * Implementation of the Address interface.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public class AddressImpl implements Address {
    public int id;
    public String street;
    public String city;
    public String state;
    public String country;
    public String postalCode;

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getCountry() {
        return this.country;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getPostalCode() {
        return this.postalCode;
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public String getStreet() {
        return this.street;
    }

    @Override
    public void setCity(String value) {
        this.city = value;
    }

    @Override
    public void setCountry(String value) {
        this.country = value;
    }

    @Override
    public void setID(int value) {
        this.id = value;
    }

    @Override
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    @Override
    public void setState(String value) {
        this.state = value;
    }

    @Override
    public void setStreet(String value) {
        this.street = value;
    }

    public String toString() {
        return "[Address #" + getID() + "] " + getStreet() + ", " + getCity() + ", " + getState() + ", " + getCountry() + " " + getPostalCode();
    }
}
