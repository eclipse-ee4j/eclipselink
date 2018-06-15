/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;

public class ListenerUnmarshal extends Unmarshaller.Listener {

    List<UnmarshalEvent> events = new ArrayList<UnmarshalEvent>();

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        events.add(new UnmarshalEvent(false, target, parent));
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        events.add(new UnmarshalEvent(true, target, parent));
    }

    public List<UnmarshalEvent> getEvents() {
        return events;
    }

    public static class UnmarshalEvent {

        private boolean beforeEvent;
        private Object target;
        private Object parent;

        public UnmarshalEvent(boolean beforeEvent, Object target, Object parent) {
            this.beforeEvent = beforeEvent;
            this.target = target;
            this.parent = parent;
        }

        public boolean isBeforeEvent() {
            return beforeEvent;
        }

        public Object getTarget() {
            return target;
        }

        public Object getParent() {
            return parent;
        }

    }

}
