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

    @Override
    public void append(Writer writer) throws IOException {
        writer.write(str);
    }

    protected String str;
}
