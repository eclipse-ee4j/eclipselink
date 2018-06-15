/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmeswani (Sun Microsystems, Inc.) - Adding the class to test default listners (Bug 227046)
package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

import javax.persistence.PrePersist;
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
public class DefaultListener3 extends ListenerSuperclass {
    @PrePersist
    @Override public void prePersist(Object obj) {
        if(obj instanceof Bus) {  //Only Bus is setup to record Listeners being called
            ((Bus) obj).addPrePersistCalledListener(this.getClass());
        }
    }
}
