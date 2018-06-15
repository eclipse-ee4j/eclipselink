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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventListener;

/**
 * A generic "State Change" event gets delivered whenever a model changes to
 * such extent that it cannot be delineated all aspects of it that have changed.
 * You can register a StateChangeListener with a source model so as to be notified
 * of any such changes.
 */
public interface StateChangeListener
    extends EventListener
{
    /**
     * This method gets called when a model has changed in some general fashion.
     *
     * @param e A StateChangeEvent object describing the event source.
     */
    void stateChanged(StateChangeEvent e);

}
