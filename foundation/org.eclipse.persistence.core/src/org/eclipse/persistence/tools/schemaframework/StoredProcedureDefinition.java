/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Markus KARG - Added methods allowing to support stored procedure creation on SQLAnywherePlatform.
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Allow a semi-generic way of creating stored procedures.
 */
public class StoredProcedureDefinition extends DatabaseObjectDefinition {
    protected Vector variables;
    protected Vector statements;
    protected Vector arguments;
    protected Vector argumentTypes;
    protected static final Integer IN = Integer.valueOf(1);
    protected static final Integer OUT = Integer.valueOf(2);
    protected static final Integer INOUT = Integer.valueOf(3);

    public StoredProcedureDefinition() {
        this.statements = new Vector();
        this.variables = new Vector();
        this.arguments = new Vector();
        this.argumentTypes = new Vector();
    }

    /**
     * The arguments are the names of the parameters to the procedure.
     */
    public void addArgument(String argumentName, Class type) {
        addArgument(new FieldDefinition(argumentName, type));
    }

    /**
     * The arguments are the names of the parameters to the procedure.
     */
    public void addArgument(String argumentName, Class type, int size) {
        addArgument(new FieldDefinition(argumentName, type, size));
    }

    /**
     * The arguments are the names of the parameters to the procedure.
     */
    public void addArgument(String argumentName, String typeName) {
        addArgument(new FieldDefinition(argumentName, typeName));
    }

    /**
     * The arguments are the names of the parameters to the procedure.
     */
    public void addArgument(FieldDefinition argument) {
        getArguments().addElement(argument);
        getArgumentTypes().addElement(IN);
    }

    /**
     * The output arguments are used to get values back from the proc.
     */
    public void addInOutputArgument(String argumentName, Class type) {
        addInOutputArgument(new FieldDefinition(argumentName, type));
    }

    /**
     * The output arguments are used to get values back from the proc, such as cursors.
     */
    public void addInOutputArgument(FieldDefinition argument) {
        getArguments().addElement(argument);
        getArgumentTypes().addElement(INOUT);
    }

    /**
     * The output arguments are used to get values back from the proc.
     */
    public void addOutputArgument(String argumentName, Class type) {
        addOutputArgument(new FieldDefinition(argumentName, type));
    }

    /**
     * The output arguments are used to get values back from the proc.
     */
    public void addOutputArgument(String argumentName, Class type, int size) {
        addOutputArgument(new FieldDefinition(argumentName, type, size));
    }

    /**
     * The output arguments are used to get values back from the proc, such as cursors.
     */
    public void addOutputArgument(String argumentName, String typeName) {
        addOutputArgument(new FieldDefinition(argumentName, typeName));
    }

    /**
     * The output arguments are used to get values back from the proc, such as cursors.
     */
    public void addOutputArgument(FieldDefinition argument) {
        getArguments().addElement(argument);
        getArgumentTypes().addElement(OUT);
    }

    /**
     * The statements are the SQL lines of code in procedure.
     */
    public void addStatement(String statement) {
        getStatements().addElement(statement);
    }

    /**
     * The variables are the names of the declared variables used in the procedure.
     */
    public void addVariable(String variableName, String typeName) {
        this.addVariable(new FieldDefinition(variableName, typeName));
    }

    /**
     * The variables are the names of the declared variables used in the procedure.
     */
    public void addVariable(FieldDefinition variable) {
        getVariables().addElement(variable);
    }

    /**
     * INTERNAL: Return the create table statement.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            DatabasePlatform platform = session.getPlatform();
            writer.write(getCreationHeader() + getFullName());
            if (getArguments().size() > getFirstArgumentIndex() || platform.requiresProcedureBrackets()) {
                writer.write(" (");
            }
            writer.write("\n");
            for (int i = getFirstArgumentIndex(); i < getArguments().size(); i++) {
                writer.write("\t");
                FieldDefinition argument = (FieldDefinition)getArguments().elementAt(i);
                Integer argumentType = (Integer)getArgumentTypes().elementAt(i);
                if (argumentType == IN) {
                    printArgument(argument, writer, session);
                } else if (argumentType == OUT) {
                    printOutputArgument(argument, writer, session);
                } else if (argumentType == INOUT) {
                    printInOutputArgument(argument, writer, session);
                }
                if (i < (getArguments().size() - 1)) {
                    writer.write(",\n");
                }
            }
            if (getArguments().size() > getFirstArgumentIndex() || platform.requiresProcedureBrackets()) {
                writer.write(")");
            }

            printReturn(writer, session);
            writer.write(platform.getProcedureAsString());
            writer.write("\n");

            writer.write(platform.getProcedureOptionList());
            writer.write("\n");

            if (platform.shouldPrintStoredProcedureVariablesAfterBeginString()) {
                writer.write(platform.getProcedureBeginString());
                writer.write("\n");
            }

            if (!getVariables().isEmpty()) {
                writer.write("DECLARE\n");
            }

            for (Enumeration variablesEnum = getVariables().elements();
                     variablesEnum.hasMoreElements();) {
                FieldDefinition field = (FieldDefinition)variablesEnum.nextElement();
                writer.write("\t");
                writer.write(field.getName());
                writer.write(" ");
                writer.write(field.getTypeName());
                writer.write(platform.getBatchDelimiterString());
                writer.write("\n");
            }

            if (!platform.shouldPrintStoredProcedureVariablesAfterBeginString()) {
                writer.write(platform.getProcedureBeginString());
                writer.write("\n");
            }

            for (Enumeration statementsEnum = getStatements().elements();
                     statementsEnum.hasMoreElements();) {
                writer.write((String)statementsEnum.nextElement());
                writer.write(platform.getBatchDelimiterString());
                writer.write("\n");
            }
            writer.write(platform.getProcedureEndString());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL: Return the drop table statement.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(getDeletionHeader() + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * The arguments are the names of the parameters to the procedure.
     */
    public Vector getArguments() {
        return arguments;
    }

    /**
         *
         */
    public String getCreationHeader() {
        return "CREATE PROCEDURE ";
    }

    /**
         *
         */
    public String getDeletionHeader() {
        return "DROP PROCEDURE ";
    }

    /**
         *
         */
    public int getFirstArgumentIndex() {
        return 0;
    }

    /**
         *
         */
    public Vector getArgumentTypes() {
        return argumentTypes;
    }

    /**
     * The statements are the SQL lines of code in procedure.
     */
    public Vector getStatements() {
        return statements;
    }

    /**
     * The variables are the names of the declared variables used in the procedure.
     */
    public Vector getVariables() {
        return variables;
    }

    /**
     * Print the argument and its type.
     * @param argument Stored procedure argument.
     * @param writer   Target writer where to write argument string.
     * @param session  Current session context.
     * @throws IOException When any IO problem occurs.
     */
    protected void printArgument(final FieldDefinition argument, final Writer writer,
            final AbstractSession session) throws IOException {
        final DatabasePlatform platform = session.getPlatform();
        final FieldTypeDefinition fieldType
                = getFieldTypeDefinition(session, argument.type, argument.typeName);

        writer.write(platform.getProcedureArgumentString());

        if (platform.shouldPrintInputTokenAtStart()) {
            writer.write(" ");
            writer.write(platform.getInputProcedureToken());
            writer.write(" ");
        }

        writer.write(argument.name);
        writer.write(" ");
        writer.write(fieldType.getName());

        if (fieldType.isSizeAllowed() && platform.allowsSizeInProcedureArguments()
                && ((argument.size != 0) || (fieldType.isSizeRequired()))) {
            writer.write("(");
            if (argument.size == 0) {
                writer.write(Integer.toString(fieldType.getDefaultSize()));
            } else {
                writer.write(Integer.toString(argument.size));
            }
            if (argument.subSize != 0) {
                writer.write(",");
                writer.write(Integer.toString(argument.subSize));
            } else if (fieldType.getDefaultSubSize() != 0) {
                writer.write(",");
                writer.write(Integer.toString(fieldType.getDefaultSubSize()));
            }
            writer.write(")");
        }
    }

    /**
     * Print the argument and its type.
     * @param argument Stored procedure argument.
     * @param writer   Target writer where to write argument string.
     * @param session  Current session context.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    protected void printInOutputArgument(final FieldDefinition argument, final Writer writer,
            final AbstractSession session) throws ValidationException {
        try {
            final DatabasePlatform platform = session.getPlatform();
            final FieldTypeDefinition fieldType
                    = getFieldTypeDefinition(session, argument.type, argument.typeName);

            writer.write(platform.getProcedureArgumentString());

            if (platform.shouldPrintOutputTokenAtStart()) {
                writer.write(" ");
                writer.write(platform.getCreationInOutputProcedureToken());
                writer.write(" ");
            }
            writer.write(argument.name);
            if ((!platform.shouldPrintOutputTokenAtStart()) && platform.shouldPrintOutputTokenBeforeType()) {
                writer.write(" ");
                writer.write(platform.getCreationInOutputProcedureToken());
            }
            writer.write(" ");
            writer.write(fieldType.getName());
            if (fieldType.isSizeAllowed() && platform.allowsSizeInProcedureArguments()
                    && ((argument.size != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (argument.size == 0) {
                    writer.write(Integer.toString(fieldType.getDefaultSize()));
                } else {
                    writer.write(Integer.toString(argument.size));
                }
                if (argument.subSize != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(argument.subSize));
                } else if (fieldType.getDefaultSubSize() != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(fieldType.getDefaultSubSize()));
                }
                writer.write(")");
            }
            if ((!platform.shouldPrintOutputTokenAtStart()) && (!platform.shouldPrintOutputTokenBeforeType())) {
                writer.write(" ");
                writer.write(platform.getCreationInOutputProcedureToken());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * Print the argument and its type.
     * @param argument Stored procedure argument.
     * @param writer   Target writer where to write argument string.
     * @param session  Current session context.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    protected void printOutputArgument(final FieldDefinition argument, final Writer writer,
            final AbstractSession session) throws ValidationException {
        try {
            final DatabasePlatform platform = session.getPlatform();
            final FieldTypeDefinition fieldType
                    = getFieldTypeDefinition(session, argument.type, argument.typeName);

            writer.write(platform.getProcedureArgumentString());

            if (platform.shouldPrintOutputTokenAtStart()) {
                writer.write(" ");
                writer.write(platform.getCreationOutputProcedureToken());
                writer.write(" ");
            }
            writer.write(argument.name);
            if ((!platform.shouldPrintOutputTokenAtStart()) && platform.shouldPrintOutputTokenBeforeType()) {
                writer.write(" ");
                writer.write(platform.getCreationOutputProcedureToken());
            }
            writer.write(" ");
            writer.write(fieldType.getName());
            if (fieldType.isSizeAllowed() && platform.allowsSizeInProcedureArguments()
                    && ((argument.size != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (argument.size == 0) {
                    writer.write(Integer.toString(fieldType.getDefaultSize()));
                } else {
                    writer.write(Integer.toString(argument.size));
                }
                if (argument.subSize != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(argument.subSize));
                } else if (fieldType.getDefaultSubSize() != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(fieldType.getDefaultSubSize()));
                }
                writer.write(")");
            }
            if ((!platform.shouldPrintOutputTokenAtStart()) && !platform.shouldPrintOutputTokenBeforeType()) {
                writer.write(" ");
                writer.write(platform.getCreationOutputProcedureToken());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * Prints return for stored function, nothing to do for stored procedure
     */
    protected void printReturn(Writer writer, AbstractSession session) throws ValidationException {
    }

    /**
     * The arguments are the field defs of the parameters names and types to the procedure.
     */
    public void setArguments(Vector arguments) {
        this.arguments = arguments;
    }

    /**
     * The statements are the SQL lines of code in procedure.
     */
    public void setStatements(Vector statements) {
        this.statements = statements;
    }

    /**
     * The variables are the field defs of the declared variables used in the procedure.
     */
    public void setVariables(Vector variables) {
        this.variables = variables;
    }
}
