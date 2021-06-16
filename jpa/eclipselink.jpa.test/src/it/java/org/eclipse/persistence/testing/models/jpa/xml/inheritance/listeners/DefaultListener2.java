/*
 * Copyright (c) 2008, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     mmeswani (Sun Microsystems, Inc.) - Adding the class to test default listners (Bug 227046)
package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

import jakarta.persistence.PrePersist;
import org.eclipse.persistence.testing.models.jpa.xml.inheritance.Bus;

/**
 * A listener for the Bus entity.
 *
 * It implements the following annotations:
 * - None
 *
 * It overrides the following annotations:
 * - PrePersist from ListenerSuperclass
 *
 */
public class DefaultListener2 extends ListenerSuperclass {
    @PrePersist
    @Override public void prePersist(Object obj) {
        if(obj instanceof Bus) {  //Only Bus is setup to record Listeners being called
            ((Bus) obj).addPrePersistCalledListener(this.getClass());
        }
    }
}
