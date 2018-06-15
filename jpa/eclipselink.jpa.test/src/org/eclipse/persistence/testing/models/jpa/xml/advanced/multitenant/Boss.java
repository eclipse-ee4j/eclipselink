/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

public class Boss extends Mafioso {
    private Underboss underboss;

    public Boss() {}

    public Underboss getUnderboss() {
        return underboss;
    }

    public void setUnderboss(Underboss underboss) {
        this.underboss = underboss;

        if (underboss != null) {
            underboss.setBoss(this);
        }
    }
}
