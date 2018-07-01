/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Writer;
import java.io.IOException;

/**
 * INTERNAL:
 */
public class SimpleAppendCallCustomParameter implements AppendCallCustomParameter {
    public SimpleAppendCallCustomParameter(String str) {
        this.str = str;
    }

    public void append(Writer writer) throws IOException {
        writer.write(str);
    }

    protected String str;
}
