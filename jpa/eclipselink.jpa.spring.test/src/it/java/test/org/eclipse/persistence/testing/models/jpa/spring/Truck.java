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

import jakarta.persistence.*;

@Entity
@Table(name="Spring_TLE_Truck")
@NamedQuery (
        name="findTruckByDriverName",
        query="SELECT Object(t) FROM Truck t WHERE t.driverName = ?1"
)
public class Truck {
    private long   id;
    private String driverName;
    private Route  route;

    public Truck() {}

    public Truck(String driver) {
        this.driverName = driver;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driver) {
        this.driverName = driver;
    }

    @OneToOne(cascade = CascadeType.PERSIST)
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
