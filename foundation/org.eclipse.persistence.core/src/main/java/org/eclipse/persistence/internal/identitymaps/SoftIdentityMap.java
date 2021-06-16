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
package org.eclipse.persistence.internal.identitymaps;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A SoftIdentityMap holds all objects referenced by the application.
 * The soft identity map is similar to the weak identity map except for the fact that it allows
 * only garbage collects when memory is low.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Guarantees identity.
 * <li> Allows garbage collection when memory is low.
 * </ul>
 * @author James Sutherland
 * @since TopLink 11g
 */
public class SoftIdentityMap extends WeakIdentityMap {

    public SoftIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isIsolated) {
        super(size, descriptor, session, isIsolated);
        this.cleanupCount = 0;
        this.cleanupSize = size;
    }

    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new SoftCacheKey(primaryKey, object, writeLockValue, readTime, isIsolated);
    }
}
