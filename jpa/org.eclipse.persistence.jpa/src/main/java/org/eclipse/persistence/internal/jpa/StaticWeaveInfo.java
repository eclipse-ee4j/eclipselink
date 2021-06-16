/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
