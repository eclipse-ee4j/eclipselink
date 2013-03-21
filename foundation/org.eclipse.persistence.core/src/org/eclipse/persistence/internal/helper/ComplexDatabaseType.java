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
 *     Oracle - Dec 2008
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

import java.util.ListIterator;
import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;

/**
 * <b>PUBLIC</b>: Abstract class for Complex Database types
 * (e.g. PL/SQL records, PL/SQL collections)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public abstract class ComplexDatabaseType implements DatabaseType, Cloneable {

    protected String typeName;
    protected String compatibleType;

    /**
     * Defines the Java class that the complex type maps to.
     */
    protected Class javaType;
    protected String javaTypeName;

    public boolean isRecord() {
        return false;
    }

    public boolean isCollection() {
        return false;
    }
    
    public boolean isStruct() {
        return false;
    }
    
    public boolean isArray() {
        return false;
    }

    /**
     * Indicates if a given subclass represents a PL/SQL cursor.
     * 
     * @see org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCursor
     */
    public boolean isCursor() {
        return false;
    }
    
    public int getConversionCode() {
        return getSqlCode();
    }

    public boolean isComplexDatabaseType() {
        return true;
    }

    public boolean isJDBCType() {
        return false;
    }

    public boolean hasCompatibleType() {
        return this.compatibleType != null;
    }

    public String getCompatibleType() {
        return compatibleType;
    }

    public void setCompatibleType(String compatibleType) {
        this.compatibleType = compatibleType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Set the Java class that the complex type maps to.
     * The mapped class for a Record type, and collection class for Collection type.
     */
    public void setJavaType(Class javaType) {
    	this.javaType = javaType;
    	if (javaType != null) {
    	    javaTypeName = javaType.getName();
    	}
    }

    /**
     * Return the Java class that the complex type maps to.
     */
    public Class getJavaType() {
    	return javaType;
    }

    public String getJavaTypeName() {
        if (javaType != null && javaTypeName == null) {
            javaTypeName = javaType.getName();
        }
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public ComplexDatabaseType clone() {
        try {
            ComplexDatabaseType clone = (ComplexDatabaseType)super.clone();
            return clone;
        }
        catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
    }

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
    	// Validate.
    	if (!hasCompatibleType()) {
    		throw QueryException.compatibleTypeNotSet(this);
    	}
    	if ((getTypeName() == null) || getTypeName().equals("")) {
    		throw QueryException.typeNameNotSet(this);
    	}
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(inArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";");
        sb.append(NL);
        sb.append("  ");
        sb.append(databaseTypeHelper.buildCompatible(inArg));
        sb.append(" ");
        sb.append(getCompatibleType());
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";");
        sb.append(NL);
    }

    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {// Validate.
    	if (!hasCompatibleType()) {
    		throw QueryException.compatibleTypeNotSet(this);
    	}
    	if ((getTypeName() == null) || getTypeName().equals("")) {
    		throw QueryException.typeNameNotSet(this);
    	}
    	sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(outArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";");
        sb.append(NL);
    }

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        String sql2PlName = call.getSQL2PlName(this);
        if (sql2PlName == null) {
            // TODO exception
            throw new NullPointerException("no SQL2Pl conversion routine for " + typeName);
        }
        String target = databaseTypeHelper.buildTarget(arg);
        String compat = databaseTypeHelper.buildCompatible(arg);
        sb.append("  ");
        sb.append(target);
        sb.append(" := ");
        sb.append(sql2PlName);
        sb.append("(");
        sb.append(compat);
        sb.append(");");
        sb.append(NL);
    }

    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        String sql2PlName = call.getPl2SQLName(this);
        if (sql2PlName == null) {
            // TODO: Error.
            throw new NullPointerException("no Pl2SQL conversion routine for " + typeName);
        }
        String target = databaseTypeHelper.buildTarget(outArg);
        sb.append("  :");
        sb.append(outArg.outIndex);
        sb.append(" := ");
        sb.append(sql2PlName);
        sb.append("(");
        sb.append(target);
        sb.append(");");
        sb.append(NL);
    }

    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
    				DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
    	databaseTypeHelper.buildOutputRow(outArg, outputRow,
                newOutputRow, outputRowFields, outputRowValues);
    }

    public int computeInIndex(PLSQLargument inArg, int newIndex, ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeInIndex(inArg, newIndex);
    }

    public int computeOutIndex(PLSQLargument outArg, int newIndex, ListIterator<PLSQLargument> i) {
        return databaseTypeHelper.computeOutIndex(outArg, newIndex);
    }

    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
            AbstractRecord translationRow, DatabasePlatform platform) {
        databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
    }

    public void translate(PLSQLargument arg, AbstractRecord translationRow,
	        AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
	        List<DatabaseField> translationRowFields, List translationRowValues, StoredProcedureCall call) {
        databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
            copyOfTranslationFields, translationRowFields, translationRowValues, call);
    }

    public String toString() {
    	return getClass().getSimpleName() + "(" + getTypeName() + ")";
    }
}