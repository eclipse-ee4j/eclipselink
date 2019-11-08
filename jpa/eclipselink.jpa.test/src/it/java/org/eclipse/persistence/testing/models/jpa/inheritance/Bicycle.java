/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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


package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;

@Entity
@Table(name="CMP3_BICYCLE")
@DiscriminatorValue("BI")
public class Bicycle extends NonFueledVehicle {
    private String description;

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void change() {
        this.setPassengerCapacity(new Integer(100));
        this.setDescription("This Bike is easy to handle");
    }
}
