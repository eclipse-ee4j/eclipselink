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


package org.eclipse.persistence.testing.models.jpa.inheritance;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.inheritance.listeners.SportsCarListener.class)
@Table(name="CMP3_SPORTS_CAR")
@DiscriminatorValue("SC")
@ExcludeDefaultListeners
public class SportsCar extends Car {
    public static int PRE_PERSIST_COUNT = 0;

    private int maxSpeed;
    private Person user;

    @Column(name="MAX_SPEED")
    public int getMaxSpeed() {
        return maxSpeed;
    }

    @PrePersist
    public void prePersist() {
        ++PRE_PERSIST_COUNT;
    }

    public void setMaxSpeed(int speed) {
        maxSpeed = speed;
    }

    @OneToOne(mappedBy ="car", fetch=LAZY)
    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }
}
