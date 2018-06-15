/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Tomas Kraus - Initial implementation
package org.eclipse.persistence.testing.tests.logging;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Session mock-up used in logger tests.
 */
@SuppressWarnings("serial")
public class LogTestSession extends AbstractSession {

    /**
     * Creates an instance of session mock-up.
     */
    public LogTestSession() {
        super();
    }

}
