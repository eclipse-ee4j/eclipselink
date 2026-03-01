/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Define a unique key constraint for a table.
 */
public class UniqueKeyConstraint extends KeyConstraintObjectDefinition {

    public UniqueKeyConstraint() {
        super("");
    }

    public UniqueKeyConstraint(String name, String sourceField) {
        super(name, sourceField);
    }

    public UniqueKeyConstraint(String name, String[] sourceFields) {
        super(name);
        getSourceFields().addAll(Arrays.asList(sourceFields));
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            writer.write("UNIQUE ");
            appendKeys(writer, getSourceFields());
            super.appendDBString(writer, session);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IOException) {
                throw ValidationException.fileError((IOException) ex.getCause());
            }
            throw ValidationException.fileError((new IOException(ex.getCause())));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }
}

