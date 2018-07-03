/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventObject;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A generic "State Change" event gets delivered whenever a model changes to
 * such extent that it cannot be delineated all aspects of it that have changed.
 * A StateChangeEvent object is sent as an argument to the StateChangeListener.
 */
public class StateChangeEvent
    extends EventObject
{
    private static final long serialVersionUID = 1L;


    public StateChangeEvent(Object source) {
        super(source);
    }

    public String toString() {
        return StringTools.buildToStringFor(this);
    }
}
