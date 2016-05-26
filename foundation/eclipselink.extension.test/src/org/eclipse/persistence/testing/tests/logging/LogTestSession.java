/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Tomas Kraus - Initial implementation
 ******************************************************************************/
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
