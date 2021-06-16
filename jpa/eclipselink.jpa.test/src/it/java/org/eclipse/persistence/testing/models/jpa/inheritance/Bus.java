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
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@EntityListeners({
    org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusListener.class,
    org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusNativeListener.class
})
@Table(name="CMP3_BUS")
@DiscriminatorValue("BU")
@PrimaryKeyJoinColumn(name="BUS_ID", referencedColumnName="ID")
public class Bus extends AbstractBus<PerformanceTireInfo, TireInfo, Integer> {
    private Person busDriver;

    @OneToOne(cascade=PERSIST, fetch=LAZY)
    @JoinColumn(name="DRIVER_ID", referencedColumnName="ID")
    public Person getBusDriver() {
        return busDriver;
    }

    public void setBusDriver(Person busDriver) {
        this.busDriver = busDriver;
    }
}
