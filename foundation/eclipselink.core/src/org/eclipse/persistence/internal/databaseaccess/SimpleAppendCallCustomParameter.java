/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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