/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Note there are multiple generics in the declaration here, some of which are not used.  These were added
 * as a test for bug 336133.  Please do not remove
 */
@MappedSuperclass
public abstract class AbstractBus<H extends TireInfo<J>, I extends TireInfoMappedSuperclass, J> extends FueledVehicle {
    public static int PRE_PERSIST_COUNT = 0;

    protected Collection<I> tires = new ArrayList<>();

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    public Collection<I> getTires() {
        return tires;
    }

    @PrePersist
    private void prePersist() {
        PRE_PERSIST_COUNT++;
    }

    public void setTires(Collection<I> tires) {
        this.tires = tires;
    }

}
