/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import javax.persistence.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@EntityListeners({
    org.eclipse.persistence.testing.models.jpa21.advanced.inheritance.listeners.BusListener.class,
    org.eclipse.persistence.testing.models.jpa21.advanced.inheritance.listeners.BusNativeListener.class
})
@Table(name="JPA21_BUS")
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
