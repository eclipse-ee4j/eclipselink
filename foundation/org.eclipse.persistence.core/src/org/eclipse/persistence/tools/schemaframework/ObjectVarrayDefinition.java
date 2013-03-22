/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Allow for creation of object varray type.
 * <p>
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
    public void appendTypeString(Writer writer, AbstractSession session) throws ValidationException {
        try {
            FieldTypeDefinition fieldType;
            if (getType() == null) {
                throw ValidationException.oracleObjectTypeIsNotDefined(getTypeName());
            } else if (getTypeName() == "") {
                throw ValidationException.oracleObjectTypeNameIsNotDefined(getType());
            } else {
                fieldType = new FieldTypeDefinition(getTypeName());
            }
            writer.write(fieldType.getName());
            if (!isNullAllowed) {
                writer.write(" NOT NULL");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     * Return if the varray collection is allowed NULL or not
     */
    public boolean isNullAllowed() {
        return isNullAllowed;
    }

    /**
     * PUBLIC:
     * Set if the varray collection is allowed NULL or not
     */
    public void setIsNullAllowed(boolean isNullAllowed) {
        this.isNullAllowed = isNullAllowed;
    }
}
