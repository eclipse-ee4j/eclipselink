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
package test.org.eclipse.persistence.testing.models.jpa.spring;

import java.util.Collection;
import java.util.Vector;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="SPRING_TLE_ROUTE")
public class Route {

    private long   id;
    private int    averageTimeMins;
    private Collection<Address> addresses = new Vector<Address>();

    public Route() {}

    public Route(int averageTimeMins){
        this.averageTimeMins= averageTimeMins;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAverageTimeMins() {
        return averageTimeMins;
    }

    public void setAverageTimeMins(int averageTimeMins) {
        this.averageTimeMins = averageTimeMins;
    }

    @OneToMany(mappedBy="route", cascade = CascadeType.PERSIST)
    public Collection<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address){
        this.addresses.add(address);
        address.setRoute(this);
    }

    public void removeAddress(Address address){
        this.addresses.remove(address);
        address.setRoute(null);
    }
}
