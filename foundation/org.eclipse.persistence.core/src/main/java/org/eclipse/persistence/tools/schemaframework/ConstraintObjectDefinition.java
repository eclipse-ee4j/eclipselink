/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public abstract class ConstraintObjectDefinition extends DatabaseObjectDefinition {
    private String options;

    protected ConstraintObjectDefinition() {
        this("");
    }

    protected ConstraintObjectDefinition(String name) {
        super(name);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            if (getOptions() != null && !getOptions().isEmpty()) {
                writer.write(" ");
                writer.write(getOptions());
                writer.write(" ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        //noop
        return writer;
    }

    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        //noop
        return writer;
    }
}
