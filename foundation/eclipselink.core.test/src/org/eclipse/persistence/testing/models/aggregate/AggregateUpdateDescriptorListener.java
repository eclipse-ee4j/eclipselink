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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.descriptors.*;

/**
 * Listener to listen to the aboutToUpdate event which occurs when an update occurs
 */
public class AggregateUpdateDescriptorListener extends DescriptorEventAdapter {
    public boolean updateOccured = false;

    public void aboutToUpdate(DescriptorEvent event) {
        updateOccured = true;
    }

    public boolean didUpdateOccur() {
        return updateOccured;
    }
}
