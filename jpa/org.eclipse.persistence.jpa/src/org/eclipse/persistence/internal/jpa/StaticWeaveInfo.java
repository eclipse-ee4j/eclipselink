/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     05/09/2011  Andrei Ilitchev (Oracle) - initial API and implementation
/**
 * A helper class used by EntityManagerSetupImpl class for static weaving.
 *
 * @since Elipselink 2.3
 */
package org.eclipse.persistence.internal.jpa;

import java.io.Writer;

public class StaticWeaveInfo {
    private Writer logWriter;
    private int logLevel;

    public StaticWeaveInfo(Writer logWriter, int logLevel) {
        this.logWriter = logWriter;
        this.logLevel = logLevel;
    }

    public Writer getLogWriter() {
        return logWriter;
    }

    public int getLogLevel() {
        return logLevel;
    }
}
