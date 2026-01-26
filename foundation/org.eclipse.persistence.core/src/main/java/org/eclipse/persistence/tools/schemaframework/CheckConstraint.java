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

/**
 * <b>Purpose</b>: Define a check constraint.
 */
public class CheckConstraint extends ConstraintObjectDefinition {
    protected String constraint;

    public CheckConstraint() {
        this("", "");
    }

    public CheckConstraint(String name, String constraint) {
        super(name);
        this.constraint = constraint;
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            writer.write("CONSTRAINT " + getName() + " CHECK (");
            writer.write(getConstraint());
            if (getOptions() != null && !getOptions().isEmpty()) {
                writer.write(" ");
                writer.write(getOptions());
                writer.write(" ");
            }
            writer.write(")");
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

    /**
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }
}

