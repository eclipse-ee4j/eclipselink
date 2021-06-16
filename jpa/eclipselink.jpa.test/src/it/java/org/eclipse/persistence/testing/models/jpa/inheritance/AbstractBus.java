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

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.MappedSuperclass;

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
