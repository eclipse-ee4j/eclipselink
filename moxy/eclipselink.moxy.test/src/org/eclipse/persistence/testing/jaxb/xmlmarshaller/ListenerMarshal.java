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

import javax.xml.bind.Marshaller;

public class ListenerMarshal extends Marshaller.Listener {

    List<MarshalEvent> events = new ArrayList<MarshalEvent>();

    @Override
    public void afterMarshal(Object source) {
        events.add(new MarshalEvent(false, source));
    }

    @Override
    public void beforeMarshal(Object source) {
        events.add(new MarshalEvent(true, source));
    }

    public List<MarshalEvent> getEvents() {
        return events;
    }

    public static class MarshalEvent {

        private boolean beforeEvent;
        private Object source;

        public MarshalEvent(boolean beforeEvent, Object source) {
            this.beforeEvent = beforeEvent;
            this.source = source;
        }

        public boolean isBeforeEvent() {
            return beforeEvent;
        }

        public Object getSource() {
            return source;
        }

    }

}
