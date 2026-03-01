/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for creation of object varray type.
 * </p>
 */
public class ObjectVarrayDefinition extends VarrayDefinition {
    protected boolean isNullAllowed;

    public ObjectVarrayDefinition() {
        super();
        this.isNullAllowed = false;
    }

    /**
     * INTERNAL:
     * Append the type.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendTypeString(Writer writer, AbstractSession session) throws ValidationException {
        try {
            FieldDefinition.DatabaseType fieldType;
            if (getType() == null) {
                throw ValidationException.oracleObjectTypeIsNotDefined(getTypeName());
            } else if (getTypeName() == "") {
                throw ValidationException.oracleObjectTypeNameIsNotDefined(getType());
            } else {
                fieldType = new FieldDefinition.DatabaseType(getTypeName());
            }
            writer.write(fieldType.name());
            if (!isNullAllowed) {
                writer.write(" NOT NULL");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * Return if the varray collection is allowed NULL or not
     */
    public boolean isNullAllowed() {
        return isNullAllowed;
    }

    /**
     * Set if the varray collection is allowed NULL or not
     */
    public void setIsNullAllowed(boolean isNullAllowed) {
        this.isNullAllowed = isNullAllowed;
    }
}
