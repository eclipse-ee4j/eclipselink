/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dave McCann - Nov.30, 2012 - 2.4.2 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.jdbc;

import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * DatabaseType implementation to represent an Oracle XMLType type.
 *
 */
public class OracleXMLType implements SimpleDatabaseType, Cloneable {
    public static String XMLTYPE_STR = "XMLTYPE";
    public final String typeName = XMLTYPE_STR;
    private final int typeCode = 2007;

    /**
     * Default constructor.
     */
    public OracleXMLType() {
        super();
    }
    
    @Override
    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        buildInitialDeclare(sb, inArg);
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";");
        sb.append(NL);
    }
    @Override
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        buildInitialDeclare(sb, outArg);
        sb.append(";");
        sb.append(NL);
    }
    
    protected void buildInitialDeclare(StringBuilder sb, PLSQLargument arg) {
        sb.append("  ");
        sb.append(arg.name);
        sb.append(TARGET_SUFFIX);
        sb.append(" ");
        sb.append(getTypeName());
    }
    
    @Override
    public boolean isComplexDatabaseType() {
        return false;
    }

    @Override
    public boolean isJDBCType() {
        return false;
    }

    @Override
    public int getSqlCode() {
        return typeCode;
    }

    @Override
    public int getConversionCode() {
        return getSqlCode();
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    /**
     * Indicates if a given String equals "XMLTYPE".  This method will 
     * typically be used instead of performing an instanceof check to 
     * determine if a given DatabaseType is an OracleXMLType instance. 
     */
    public static boolean isXMLType(String typeName) {
        return typeName.equals(XMLTYPE_STR); 
    }
    
    @Override
    public int computeInIndex(PLSQLargument inArg, int newIndex, ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeInIndex(inArg, newIndex);
    }

    @Override
    public int computeOutIndex(PLSQLargument outArg, int newIndex, ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeOutIndex(outArg, newIndex);
    }

    @Override
    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        // nothing to do for simple types
    }

    @Override
    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        databaseTypeHelper.buildOutAssignment(sb, outArg, call);
    }

    @Override
    public void translate(PLSQLargument arg, AbstractRecord translationRow, AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields, List<DatabaseField> translationRowFields, List translationRowValues, StoredProcedureCall call) {
        databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow, copyOfTranslationFields, translationRowFields, translationRowValues, call);
    }

    @Override
    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow, DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
        databaseTypeHelper.buildOutputRow(outArg, outputRow, newOutputRow, outputRowFields, outputRowValues);
    }

    @Override
    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg, AbstractRecord translationRow, DatabasePlatform platform) {
        databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
    }
}