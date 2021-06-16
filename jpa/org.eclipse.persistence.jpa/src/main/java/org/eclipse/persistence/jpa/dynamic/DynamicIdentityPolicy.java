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
//     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
//               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//
// This code is being developed under INCUBATION and is not currently included
// in the automated EclipseLink build. The API in this code may change, or
// may never be included in the product. Please provide feedback through mailing
// lists or the bug database.
package org.eclipse.persistence.jpa.dynamic;

//EclipseLink imports
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Custom CMPPolicy to handle Object[]. The parent class supports only List and
 * ID class for composite keys.
 *
 * @author dclarke
 * @since EclipseLink 1.2
 */
public class DynamicIdentityPolicy extends CMP3Policy {

    @Override
    public Object createPrimaryKeyFromId(Object key, AbstractSession session) {
        if (key instanceof Object[]) {
            return new CacheId((Object[])key);
        }
        return super.createPrimaryKeyFromId(key, session);
    }

    @Override
    protected KeyElementAccessor[] initializePrimaryKeyFields(Class keyClass, AbstractSession session) {
        if (keyClass == null && getDescriptor().getPrimaryKeyFields().size() > 1) {
            KeyElementAccessor[] result = super.initializePrimaryKeyFields(null, session);
            this.pkClass = Object[].class;
            return result;
        } else {
            return super.initializePrimaryKeyFields(keyClass, session);
        }
    }

}
