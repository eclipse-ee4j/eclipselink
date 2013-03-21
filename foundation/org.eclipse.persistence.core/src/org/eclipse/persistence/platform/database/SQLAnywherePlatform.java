/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates, Markus KARG(markus-karg@users.sourceforge.net). All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Markus Karg and Oracle - initial API and implementation from Oracle TopLink and TopLink Essentials
 *          05/16/2008-1.0 Markus Karg - 237843: CONCAT must be translated into || instead of + 
 *        
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * Provides SQL Anywhere specific behaviour.
 * 
 * @author Markus KARG (markus-karg@users.sourceforge.net)
 */
public class SQLAnywherePlatform extends SybasePlatform {

    public SQLAnywherePlatform() {
        super();
    }
    
// TODO: can't use these field types: none of them has sizes.
// That results is using defaults, that seem to be 9 for VARCHAR
// and neither FeatureTestModel nor AggregateTestModel would setup because of that.
// Untill this is fixed have to use the field types inherited from SybasePlatform.
/*    protected Hashtable buildFieldTypes() {
        Hashtable<Class, FieldTypeDefinition> fieldTypeMapping = new Hashtable<Class, FieldTypeDefinition>();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BIT", false));
        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(BigDecimal.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR"));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR"));
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("LONG BINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("LONG VARCHAR", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("LONG BINARY", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("LONG VARCHAR", false));
        fieldTypeMapping.put(Blob.class, new FieldTypeDefinition("LONG BINARY", false));
        fieldTypeMapping.put(Clob.class, new FieldTypeDefinition("LONG VARCHAR", false));
        fieldTypeMapping.put(Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        return fieldTypeMapping;
    }*/
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BIT", false));
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
    public String getStoredProcedureTerminationToken() {
        return ";";
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
