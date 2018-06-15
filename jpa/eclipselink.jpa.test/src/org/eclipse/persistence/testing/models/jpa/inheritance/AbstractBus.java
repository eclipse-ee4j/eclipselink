/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
/**
 * Note there are multiple generics in the declaration here, some of which are not used.  These were added
 * as a test for bug 336133.  Please do not remove
 */
public abstract class AbstractBus<H extends TireInfo<J>, I extends TireInfoMappedSuperclass, J> extends FueledVehicle {
    public static int PRE_PERSIST_COUNT = 0;

    protected Collection<I> tires = new ArrayList<I>();

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
