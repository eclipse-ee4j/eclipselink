/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.plsqlrecord;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class OutputRowSessionEventAdapter extends SessionEventAdapter {

    Object eventResult = null;

    public void outputParametersDetected(SessionEvent event) {
        eventResult = event.getResult();
    }

    public Object getEventResult() {
        return eventResult;
    }
}
