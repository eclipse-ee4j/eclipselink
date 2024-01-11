/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Allow a semi-generic way of creating packages.
 * </p>
 */
public class PackageDefinition extends DatabaseObjectDefinition {
    protected List<String> statements;
    protected List<StoredProcedureDefinition> procedures;

    public PackageDefinition() {
        this.statements = new Vector<>();
        this.procedures = new Vector<>();
    }

    /**
     * Packages can contain sets of procedures.
     */
    public void addProcedures(StoredProcedureDefinition procedure) {
        getProcedures().add(procedure);
    }

    /**
     * The statements are the SQL lines of code.
     */
    public void addStatement(String statement) {
        getStatements().add(statement);
    }

    /**
     * INTERNAL:
     * Return the create table statement.
     */
    @Override
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            DatabasePlatform platform = session.getPlatform();
            writer.write("CREATE PACKAGE " + getFullName());
            writer.write(" AS");
            writer.write("\n");
            for (String statement: statements) {
                writer.write(statement);
                writer.write(platform.getBatchDelimiterString());
                writer.write("\n");
            }
            for (StoredProcedureDefinition procedure: procedures) {
                writer.write("\n");
                String procedureString = procedure.buildCreationWriter(session, writer).toString();
                writer.write(procedureString.substring(7));
                writer.write("\n");
            }
            writer.write(platform.getBatchEndString());
            writer.write("\n" + session.getPlatform().getStoredProcedureTerminationToken());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop table statement.
     */
    @Override
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP PACKAGE " + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * Packages can contain sets of procedures.
     */
    public List<StoredProcedureDefinition> getProcedures() {
        return procedures;
    }

    /**
     * The statements are the SQL lines of code.
     */
    public List<String> getStatements() {
        return statements;
    }

    /**
     * Packages can contain sets of procedures.
     */
    public void setProcedures(List<StoredProcedureDefinition> procedures) {
        this.procedures = procedures;
    }

    /**
     * The statements are the SQL lines of code.
     */
    public void setStatements(List<String> statements) {
        this.statements = statements;
    }
}
