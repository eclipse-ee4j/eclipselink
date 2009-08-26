/*******************************************************************************
 * Copyright (c) 2008 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mmeswani (Sun Microsystems, Inc.) - Adding the class to test default listners (Bug 227046)
 ******************************************************************************/
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
public class DefaultListener1 extends ListenerSuperclass {
    @PrePersist
    @Override public void prePersist(Object obj) {
        if(obj instanceof Bus) {  //Only Bus is setup to record Listeners being called
            ((Bus) obj).addPrePersistCalledListener(this.getClass());
        }
    }
}