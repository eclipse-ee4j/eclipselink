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
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow a semi-generic way of creating store function.
 * Note that stored functions supported only on Oracle platform
 * <p>
 */
public class StoredFunctionDefinition extends StoredProcedureDefinition {
    public StoredFunctionDefinition() {
        super();
        this.addOutputArgument(new FieldDefinition());
    }

    /**
     * INTERNAL:
     * Return the create statement.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        if (session.getPlatform().supportsStoredFunctions()) {
            super.buildCreationWriter(session, writer);
        } else {
            throw ValidationException.platformDoesNotSupportStoredFunctions(Helper.getShortClassName(session.getPlatform()));
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop statement.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        if (session.getPlatform().supportsStoredFunctions()) {
            super.buildDeletionWriter(session, writer);
        } else {
            throw ValidationException.platformDoesNotSupportStoredFunctions(Helper.getShortClassName(session.getPlatform()));
        }
        return writer;
    }

    /**
     *
     */
    public String getCreationHeader() {
        return "CREATE FUNCTION ";
    }

    /**
     *
     */
    public String getDeletionHeader() {
        return "DROP FUNCTION ";
    }

    /**
     *
     */
    public int getFirstArgumentIndex() {
        return 1;
    }

    /**
     * Prints return for stored function
     */
    public void setReturnType(Class type) {
        FieldDefinition argument = (FieldDefinition)getArguments().firstElement();
        argument.setType(type);
    }

    /**
     * Prints return for stored function
     */
    protected void printReturn(Writer writer, AbstractSession session) throws ValidationException {
        try {
            session.getPlatform().printStoredFunctionReturnKeyWord(writer);
            FieldDefinition argument = (FieldDefinition)getArguments().firstElement();

            // argumentType should be OUT: getArgumentTypes().firstElement() == OUT;
            // but should be printed as IN
            printArgument(argument, writer, session);
            writer.write("\n");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }
}
