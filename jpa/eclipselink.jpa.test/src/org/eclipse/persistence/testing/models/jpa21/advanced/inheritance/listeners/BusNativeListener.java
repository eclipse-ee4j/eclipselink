/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance.listeners;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * A listener class that implements DescriptorEventListener (directly or through
 * an adapter) is added to the descriptor's event manager directly.
 *
 * @author gpelleti
 */
public class BusNativeListener extends DescriptorEventAdapter {
    public static int PRE_WRITE_COUNT = 0;
    public static int POST_WRITE_COUNT = 0;

    public void preWrite(DescriptorEvent event) {
        PRE_WRITE_COUNT++;
    }

    public void postWrite(DescriptorEvent event) {
        POST_WRITE_COUNT++;
    }
}
