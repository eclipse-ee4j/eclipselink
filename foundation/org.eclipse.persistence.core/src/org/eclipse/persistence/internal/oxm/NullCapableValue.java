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
    public void setNullValue(Object object, CoreSession session);
}
