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

package org.eclipse.persistence.platform.database.oracle.plsql;

import java.util.List;
import java.util.ListIterator;
import static java.sql.Types.OTHER;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.INTEGER_TYPE;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.NUMERIC_TYPE;

/**
 * <b>PUBLIC</b>: Oracle PL/SQL types
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
@SuppressWarnings("unchecked")
public enum OraclePLSQLTypes implements SimpleDatabaseType, OraclePLSQLType {

    BinaryInteger("BINARY_INTEGER"),
    Dec("DEC") ,
    Int("INT"),
    Natural("NATURAL"),
    NaturalN("NATURALN") {
        /**
         * Requires an initial value.
         */
        @Override
        public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
            databaseTypeHelper.declareTarget(sb, outArg, this);
            sb.append(" := 1;");
            // can't use Helper.cr 'cause Oracle PL/SQL parser only likes Unix-style newlines '\n'
            sb.append(NL);
        }
    },
    PLSQLBoolean("BOOLEAN") {
        @Override
        public int getConversionCode() {
            // substitute Integer
            return INTEGER_TYPE.getConversionCode();
        }

        @Override
        public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
            databaseTypeHelper.declareTarget(sb, inArg, this);
            sb.append(" := "); 
            sb.append(PLSQLBoolean_IN_CONV);
            sb.append("(:");
            sb.append(inArg.inIndex);
            sb.append(");");
            sb.append(NL);
        }

        @Override
        public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
            sb.append("  :");
            sb.append(outArg.outIndex);
            sb.append(" := "); 
            sb.append(PLSQLBoolean_OUT_CONV);
            sb.append("(");
            sb.append(databaseTypeHelper.buildTarget(outArg));
            sb.append(");");
            sb.append(NL);
        }
    },
    PLSQLInteger("PLS_INTEGER"),
    Positive("POSITIVE"),
    PositiveN("POSITIVEN") {
        /**
         * Requires an initial value.
         */
        @Override
        public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
            databaseTypeHelper.declareTarget(sb, outArg, this);
            sb.append(" := 1;");
            sb.append(NL);
        }
    },
    SignType("SIGNTYPE"),
    XMLType("XMLTYPE") {
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
        public int getSqlCode() {
            return 2007;
        }
        public int getConversionCode() {
            return getSqlCode();
        }
    },
    ;

    private final String typeName;

    OraclePLSQLTypes(String typeName) {
        this.typeName = typeName;
    }

    public boolean isComplexDatabaseType() {
        return false;
    }
    public int getSqlCode() {
        return OTHER;
    }

    public int getConversionCode() {
        // widest compatible type java.sql.Types.NUMERIC <-> BigDecimal 
        return NUMERIC_TYPE.getConversionCode(); 
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isJDBCType() {
        return false;
    }

    /**
     * INTERNAL:
     * Return the parameter index for the IN parameter.
     */
    public int computeInIndex(PLSQLargument inArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeInIndex(inArg, newIndex);
    }
    
    /**
     * INTERNAL:
     * Return the parameter index for the OUT parameter.
     */
    public int computeOutIndex(PLSQLargument outArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeOutIndex(outArg, newIndex);
    }
    
    /**
     * INTERNAL:
     * Append the variable declaration for the type.
     */
    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        databaseTypeHelper.declareTarget(sb, inArg, this);
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";");
        sb.append(NL);
    }

    /**
     * INTERNAL:
     * Append the variable declaration for the type.
     */
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        databaseTypeHelper.declareTarget(sb, outArg, this);
        sb.append(";");
        sb.append(NL);
    }
    
    /**
     * INTERNAL:
     * Append any code or translation required for the type.
     */
    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        // nothing to do for simple types
    }

    /**
     * INTERNAL:
     * Append any code or translation for assigning the output value.
     */
    public void buildOutAssignment(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        databaseTypeHelper.buildOutAssignment(sb, arg, call);
    }

    /**
     * INTERNAL:
     * Translate the argument value from the query translation row to call translation row.
     */
    public void translate(PLSQLargument arg, AbstractRecord translationRow,
            AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
            List<DatabaseField> translationRowFields, List translationRowValues, StoredProcedureCall call) {
        databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
            copyOfTranslationFields, translationRowFields, translationRowValues, call);
    }

    /**
     * INTERNAL:
     * Build the query output row from the call output row.
     */
    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
            DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
        databaseTypeHelper.buildOutputRow(outArg, outputRow,
            newOutputRow, outputRowFields, outputRowValues);
    }

    /**
     * INTERNAL:
     * Append the parameter for logging purposes.
     */
    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
            AbstractRecord translationRow, DatabasePlatform platform) {
        databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
    }

    public static DatabaseType getDatabaseTypeForCode(String typeName) {
        DatabaseType databaseType = null;
        if (BinaryInteger.typeName.equalsIgnoreCase(typeName)) {
            databaseType = BinaryInteger;
        }
        else if (Dec.typeName.equalsIgnoreCase(typeName)) {
            databaseType = Dec;
        }
        else if (Int.typeName.equalsIgnoreCase(typeName)) {
            databaseType = Int;
        }
        else if (Natural.typeName.equalsIgnoreCase(typeName)) {
            databaseType = Natural;
        }
        else if (NaturalN.typeName.equalsIgnoreCase(typeName)) {
            databaseType = NaturalN;
        }
        else if (PLSQLBoolean.typeName.equalsIgnoreCase(typeName) ||
            "BOOLEAN".equalsIgnoreCase(typeName)) {
            databaseType = PLSQLBoolean;
        }
        else if (PLSQLInteger.typeName.equalsIgnoreCase(typeName)) {
            databaseType = PLSQLInteger;
        }
        else if (Positive.typeName.equalsIgnoreCase(typeName)) {
            databaseType = Positive;
        }
        else if (PositiveN.typeName.equalsIgnoreCase(typeName)) {
            databaseType = PositiveN;
        }
        else if (SignType.typeName.equalsIgnoreCase(typeName)) {
            databaseType = SignType;
        } else if (XMLType.typeName.equalsIgnoreCase(typeName)) {
        	databaseType = XMLType;
        }
        return databaseType;
    }
}
