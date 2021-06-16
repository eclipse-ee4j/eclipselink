/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/26/2011-2.3 Guy Pelletier
//       - 307664:  Lifecycle callbacks not called for object from IndirectSet
package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import java.util.EventListener;
import jakarta.persistence.PostLoad;

import org.eclipse.persistence.testing.models.jpa.inheritance.DDD;
import org.eclipse.persistence.testing.models.jpa.inheritance.Vehicle;

public class DDDListener implements EventListener {
    @PostLoad
    public void postLoad(DDD ddd) {
        ddd.setCount2(ddd.getCount2()+1);
    }
}
