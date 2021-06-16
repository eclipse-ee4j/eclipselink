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

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public int getID() {
        return this.id;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getState() {
        return this.state;
    }

    public String getStreet() {
        return this.street;
    }

    public void setCity(String value) {
        this.city = value;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    public void setState(String value) {
        this.state = value;
    }

    public void setStreet(String value) {
        this.street = value;
    }

    public String toString() {
        return "[Address #" + getID() + "] " + getStreet() + ", " + getCity() + ", " + getState() + ", " + getCountry() + " " + getPostalCode();
    }
}
