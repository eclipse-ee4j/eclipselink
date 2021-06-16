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
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.core.sessions.CoreSession;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: If an implementation of NodeValue is capable of returning
 * a null value then it must implement this interface to be handled correctly by
 * the TreeObjectBuilder.</p>
 */

public interface NullCapableValue {
    /**
     * INTERNAL:
     * Set the null representation of the (object).
     * @param object
     * @param session
     */
    void setNullValue(Object object, CoreSession session);
}
