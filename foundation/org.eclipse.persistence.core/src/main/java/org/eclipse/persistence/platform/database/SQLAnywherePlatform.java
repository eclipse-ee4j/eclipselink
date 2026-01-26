/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2008 Markus KARG(markus-karg@users.sourceforge.net).
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
//     Markus Karg and Oracle - initial API and implementation from Oracle TopLink and TopLink Essentials
//     05/16/2008-1.0 Markus Karg
//       - 237843: CONCAT must be translated into || instead of +
//     02/23/2015-2.6 Dalia Abo Sheasha
//       - 460607: Change DatabasePlatform StoredProcedureTerminationToken to be configurable
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Provides SQL Anywhere specific behaviour.
 *
 * @author Markus KARG (markus-karg@users.sourceforge.net)
 */
public class SQLAnywherePlatform extends SybasePlatform {

    public SQLAnywherePlatform() {
        super();
        setStoredProcedureTerminationToken(";");
    }

// TODO: can't use these field types: none of them has sizes.
// That results is using defaults, that seem to be 9 for VARCHAR
// and neither FeatureTestModel nor AggregateTestModel would setup because of that.
// Untill this is fixed have to use the field types inherited from SybasePlatform.
/*    protected Map<Class<?>, FieldDefinition.DatabaseType> buildFieldTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashtMap<>();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("BIT", false));
        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("BIGINT", false));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(String.class, TYPE_VARCHAR);
        fieldTypeMapping.put(Character.class, TYPE_CHAR);
        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("LONG BINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("LONG VARCHAR", false));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("LONG BINARY", false));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("LONG VARCHAR", false));
        fieldTypeMapping.put(Blob.class, new FieldDefinition.DatabaseType("LONG BINARY", false));
        fieldTypeMapping.put(Clob.class, new FieldDefinition.DatabaseType("LONG VARCHAR", false));
        fieldTypeMapping.put(Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(Time.class, new FieldDefinition.DatabaseType("TIME", false));
        fieldTypeMapping.put(Timestamp.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));
        return fieldTypeMapping;
    }*/
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = super.buildDatabaseTypes();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("BIT", false));
        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT @@IDENTITY");
        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    public static ExpressionOperator createCurrentDateOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate, "CURRENT DATE");
    }

    public static ExpressionOperator createCurrentTimeOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentTime, "CURRENT TIME");
    }

    public static ExpressionOperator createLocate2Operator() {
        return ExpressionOperator.simpleThreeArgumentFunction(ExpressionOperator.Locate2, "LOCATE");
    }

    public static ExpressionOperator createConcatOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "||");
    }

    public static ExpressionOperator createLocateOperator() {
        return ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Locate, "LOCATE");
    }

    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "DECLARE LOCAL TEMPORARY TABLE ";
    }

    /**
     * Used for stored procedure creation: Prefix for INPUT parameters.
     * Not required on most platforms.
     */
    @Override
    public String getInputProcedureToken() {
        return "IN";
    }

    @Override
    public String getInOutputProcedureToken() {
        return "";
    }

    /* This method is used to print the output parameter token when stored
     * procedures is created
     */
    @Override
    public String getCreationOutputProcedureToken() {
        return "OUT";
    }

    /* This method is used to print the output parameter token when stored
     * procedures are called
     */
    @Override
    public String getOutputProcedureToken() {
        return "";
    }

    @Override
    public int getMaxFieldNameSize() {
        return 128;
    }

    /**
     * Used for sp defs.
     */
    @Override
    public String getProcedureArgumentString() {
        return "";
    }

    @Override
    public String getStoredProcedureParameterPrefix() {
        return "";
    }

    @Override
    public String getProcedureAsString() {
        return "";
    }

    @Override
    public String getProcedureBeginString() {
        return "BEGIN";
    }

    @Override
    public String getProcedureEndString() {
        return "END";
    }

    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchBeginString() {
        return "BEGIN ";
    }

    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchEndString() {
        return "END;";
    }

    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchDelimiterString() {
        return "; ";
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return "CALL ";
    }

    /**
     * Used for sp calls.
     */
    @Override
    public DatabaseTable getTempTableForTable(DatabaseTable table) {
        return new DatabaseTable("$" + table.getName(), table.getTableQualifier(), table.shouldUseDelimiters(), getStartDelimiter(), getEndDelimiter());
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        this.addOperator(createConcatOperator());
        this.addOperator(createLocateOperator());
        this.addOperator(createLocate2Operator());
//        this.addOperator(createCurrentDateOperator());
//        this.addOperator(createCurrentTimeOperator());
        this.addOperator(ExpressionOperator.charLength());
        this.addOperator(ExpressionOperator.mod());
    }

    @Override
    public boolean isSQLAnywhere() {
        return true;
    }

    @Override
    public boolean isSybase() {
        return false;
    }

    @Override
    public boolean requiresProcedureBrackets() {
        return true;
    }

    @Override
    public boolean requiresProcedureCallBrackets() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether the version of CallableStatement.registerOutputParameter method
     * that takes type name should be used.
     */
    @Override
    public boolean requiresTypeNameToRegisterOutputParameter() {
        return false;
    }

    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" DEFAULT AUTOINCREMENT");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    @Override
    public void printFieldNullClause(Writer writer) throws ValidationException {
        try {
            writer.write(" NULL");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    @Override
    public boolean shouldPrintInputTokenAtStart() {
        return true;
    }

    @Override
    public boolean shouldPrintInOutputTokenBeforeType() {
        return false;
    }

    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        return true;
    }

    @Override
    public boolean shouldPrintOutputTokenBeforeType() {
        return false;
    }

    //**temp
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall() {
        return false;
    }

    @Override
    public boolean shouldPrintStoredProcedureVariablesAfterBeginString() {
        return true;
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public boolean supportsLocalTempTables() {
        return true;
    }

    @Override
    public boolean supportsStoredFunctions() {
        return true;
    }

    /**
     * SQL Anywhere does support cascade on delete, unlike Sybase.
     */
    @Override
    public boolean supportsDeleteOnCascade() {
        return true;
    }
}
