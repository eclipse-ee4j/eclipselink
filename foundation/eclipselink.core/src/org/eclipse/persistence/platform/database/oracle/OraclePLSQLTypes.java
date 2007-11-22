/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.oracle;

// Javse imports
import java.util.ListIterator;
import java.util.Vector;

import static java.sql.Types.OTHER;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.INTEGER_TYPE;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.NUMERIC_TYPE;

/**
 * <b>PUBLIC</b>: Oracle PL/SQL types
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public enum OraclePLSQLTypes implements SimpleDatabaseType, OraclePLSQLType {

    BinaryInteger("BINARY_INTEGER"),
    Dec("DEC") ,
    Int("INT"),
    Natural("NATURAL"),
    NaturalN("NATURALN"),
    PLSQLBoolean("BOOLEAN") {
        @Override
        public int getConversionCode() {
            // substitute Integer
            return INTEGER_TYPE.getConversionCode();
        }

        @Override
        public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
            databaseTypeHelper.declareTarget(sb, inArg, this);
            sb.append(" := SYS.SQLJUTL.INT2BOOL(");
            sb.append(":");
            sb.append(inArg.inIndex);
            sb.append(");\n"); // can't use Helper.cr 'cause PL/SQL parser only like Unix '\n'
        }

        @Override
        public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg) {
            sb.append("  :");
            sb.append(outArg.outIndex);
            sb.append(" := SYS.SQLJUTL.BOOL2INT(");
            sb.append(databaseTypeHelper.buildTarget(outArg));
            sb.append(");\n");
        }
    },
    PLSQLInteger("PLS_INTEGER"),
    Positive("POSITIVE"),
    PositiveN("POSITIVEN"),
    SignType("SIGNTYPE"),
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
    public int computeInIndex(PLSQLargument inArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeInIndex(inArg, newIndex);
    }

    public int computeOutIndex(PLSQLargument outArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeOutIndex(outArg, newIndex);
    }

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        databaseTypeHelper.declareTarget(sb, inArg, this);
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";\n");
    }

    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        databaseTypeHelper.declareTarget(sb, outArg, this);
        sb.append(";\n");
    }

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg) {
        // nothing to do for simple types
    }
    
    public void buildOutAssignment(StringBuilder sb, PLSQLargument arg) {
        databaseTypeHelper.buildOutAssignment(sb, arg);
    }

    public void translate(PLSQLargument arg, AbstractRecord translationRow,
        AbstractRecord copyOfTranslationRow, Vector copyOfTranslationFields,
        Vector translationRowFields, Vector translationRowValues) {
        databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
            copyOfTranslationFields, translationRowFields, translationRowValues);
    }

    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
        DatabaseRecord newOutputRow, Vector outputRowFields, Vector outputRowValues) {
        databaseTypeHelper.buildOutputRow(outArg, outputRow,
            newOutputRow, outputRowFields, outputRowValues);
    }
    
    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
        AbstractRecord translationRow, DatabasePlatform platform) {
        databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
    }
}
