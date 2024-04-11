/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <b>Purpose</b>: Define a check constraint.
 */
public class CheckConstraint implements Serializable {
    protected String name;
    protected String constraint;
    protected String options;

    public CheckConstraint() {
        this.name = "";
        this.constraint = "";
    }

    public CheckConstraint(String name, String constraint) {
        this();
        this.name = name;
        this.constraint = constraint;
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
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
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     */
    public String getName() {
        return name;
    }

    /**
     * PUBLIC:
     */
    public String getConstraint() {
        return constraint;
    }

    public String getOptions() {
        return options;
    }

    /**
     * PUBLIC:
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * PUBLIC:
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}

