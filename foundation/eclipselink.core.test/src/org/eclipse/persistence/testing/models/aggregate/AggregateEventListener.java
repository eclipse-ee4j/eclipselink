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

import java.util.Vector;
import org.eclipse.persistence.descriptors.*;

/**
 * Used to test events on aggregate objects.
 */
public class AggregateEventListener extends DescriptorEventAdapter {
    public Vector events;

    public AggregateEventListener() {
        super();
        this.events = new Vector();
    }

    public Vector getEvents() {
        return this.events;
    }

    public void postDelete(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void postInsert(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void postUpdate(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void postWrite(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void preDelete(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void preInsert(DescriptorEvent event) {
        this.events.addElement(event);
    }

    public void preUpdate(DescriptorEvent event) {
        this.events.addElement(event);
    }
}
