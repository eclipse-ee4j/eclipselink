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

// javase imports
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;

/**
 * <b>PUBLIC</b>: describe an Oracle PL/SQL Record type
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public class PLSQLrecord implements ComplexDatabaseType, OraclePLSQLType, Cloneable {

    protected String recordName;
    protected String typeName;
    boolean hasCompatibleType = false;
    protected String compatibleType;
    protected List<PLSQLargument> fields = 
        new ArrayList<PLSQLargument>();
    protected PLSQLStoredProcedureCall call;

    public PLSQLrecord() {
        super();
    }

    @Override
    public PLSQLrecord clone()  {
        try {
            PLSQLrecord clone = (PLSQLrecord)super.clone();
            clone.fields = new ArrayList<PLSQLargument>(fields.size());
            for (PLSQLargument f : fields) {
                clone.fields.add(f.clone());
            }
            return clone;
        }
        catch (CloneNotSupportedException cnse) {
           return null;
        }
    }

    public boolean isComplexDatabaseType() {
        return true;
    }
    
    public boolean isJDBCType() {
        return false;
    }

    public String getRecordName() {
        return recordName;
    }
    public void setRecordName(String name) {
        this.recordName = name;
    }

    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean hasCompatibleType() {
        return hasCompatibleType;
    }
    public void setHasCompatibleType(boolean hasCompatibleType) {
        this.hasCompatibleType = hasCompatibleType;
    }
    public String getCompatibleType() {
        return compatibleType;
    }
    public void setCompatibleType(String compatibleType) {
        this.compatibleType = compatibleType;
        if (compatibleType != null && compatibleType.length() > 0) {
            hasCompatibleType = true;
        }
    }

    public int getSqlCode() {
        if (hasCompatibleType) {
            return STRUCT;
        }
        else {
            return OTHER;
        }
    }

    public int getConversionCode() {
        return getSqlCode();
    }

    public PLSQLStoredProcedureCall getCall() {
        return call;
    }
    public void setCall(PLSQLStoredProcedureCall call) {
        this.call = call;
    }

    public void addField(String fieldName, DatabaseType databaseType) {
        fields.add(new PLSQLargument(fieldName, -1, IN, databaseType));
    }
    public void addField(String fieldName, DatabaseType databaseType, int precision, int scale) {
        fields.add(
            new PLSQLargument(fieldName, -1, IN, databaseType, precision, scale));
    }
    public void addField(String fieldName, DatabaseType databaseType, int length) {
        fields.add(new PLSQLargument(fieldName, -1, IN, databaseType, length));
    }
    
    public int computeInIndex(PLSQLargument inArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        if (hasCompatibleType) {
            return databaseTypeHelper.computeInIndex(inArg, newIndex);
        }
        else {
            i.remove();
            inArg.inIndex = newIndex;
            for (PLSQLargument f : fields) {
                f.inIndex = newIndex++;
                i.add(f);
            }
            return newIndex;
        }
    }

    public int computeOutIndex(PLSQLargument outArg, int newIndex,
        ListIterator<PLSQLargument> i) {
        if (hasCompatibleType) {
            return databaseTypeHelper.computeOutIndex(outArg, newIndex);
        }
        else {
            i.remove();
            outArg.outIndex = newIndex;
            for (PLSQLargument f : fields) {
                f.outIndex = newIndex++;
                f.direction = OUT;
                i.add(f);
            }
            return newIndex;
        }
    }

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(inArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";\n");
        if (hasCompatibleType) {
            sb.append("  ");
            sb.append(databaseTypeHelper.buildCompatible(inArg));
            sb.append(" ");
            sb.append(getCompatibleType());
            sb.append(" := :");
            sb.append(inArg.inIndex);
            sb.append(";\n");
        }
    }
    
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(outArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";\n");
        if (hasCompatibleType) {
            sb.append("  ");
            sb.append(databaseTypeHelper.buildCompatible(outArg));
            sb.append(" ");
            sb.append(getCompatibleType());
            sb.append(" := ");
            sb.append(compatibleType);
            sb.append("(");
            for (int i = 0, len = fields.size(); i < len; i++) {
                sb.append("null");
                if (i < len-1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            sb.append(";\n");
        }
    }

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg) {
        String target = databaseTypeHelper.buildTarget(arg);
        String compat = databaseTypeHelper.buildCompatible(arg);
        if (arg.direction == IN | arg.direction == INOUT) {
            for (PLSQLargument f : fields) {
                sb.append("  ");
                sb.append(target);
                sb.append('.');
                sb.append(f.name);
                sb.append(" := ");
                if (hasCompatibleType) {
                    sb.append(compat);
                    sb.append('.');
                    sb.append(f.name);
                }
                else {
                    sb.append(":");
                    sb.append(f.inIndex);
                }
                sb.append(";\n");
            }
        }
    }

    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg) {
        String target = databaseTypeHelper.buildTarget(outArg);
        String compat = databaseTypeHelper.buildCompatible(outArg);
        for (PLSQLargument f : fields) {
            sb.append("  ");
            if (hasCompatibleType) {
                sb.append(compat);
                sb.append('.');
                sb.append(f.name);
            }
            else {
                sb.append(":");
                sb.append(f.outIndex);
            }
            sb.append(" := ");
            sb.append(target);
            sb.append('.');
            sb.append(f.name);
            sb.append(";\n");
        }
        if (hasCompatibleType) {
            sb.append("  :");
            sb.append(outArg.outIndex);
            sb.append(" := ");
            sb.append(compat);
            sb.append(";\n");
        }
    }

    public void translate(PLSQLargument arg, AbstractRecord translationRow,
        AbstractRecord copyOfTranslationRow, Vector copyOfTranslationFields,
        Vector translationRowFields, Vector translationRowValues) {
        if (hasCompatibleType) {
            databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
                copyOfTranslationFields, translationRowFields, translationRowValues);
        }
        else {
            for (PLSQLargument f : fields) {
                databaseTypeHelper.translate(f, translationRow, copyOfTranslationRow,
                    copyOfTranslationFields, translationRowFields, translationRowValues);
            }
        }
    }

    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
        DatabaseRecord newOutputRow, Vector outputRowFields, Vector outputRowValues) {
        if (hasCompatibleType) {
            databaseTypeHelper.buildOutputRow(outArg, outputRow, newOutputRow,
                outputRowFields, outputRowValues);
            Object value = newOutputRow.get(outArg.name);
            AbstractRecord nestedRow = ((ObjectRelationalDataTypeDescriptor)call.
                getQuery().getDescriptor()).buildRowFromStructure((Struct)value);
            newOutputRow.mergeFrom(nestedRow);
        }
        else {
            for (PLSQLargument f : fields) {
                databaseTypeHelper.buildOutputRow(f, outputRow, newOutputRow,
                    outputRowFields, outputRowValues);
            }
        }
    }

    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
        AbstractRecord translationRow, DatabasePlatform platform) {
        if (hasCompatibleType) {
            databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
        }
        else {
            for (Iterator<PLSQLargument> i = fields.iterator(); i.hasNext(); ) {
                PLSQLargument f = i.next();
                f.databaseTypeWrapper.getWrappedType().logParameter(sb, direction, f, translationRow,
                    platform);
                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
        }
    }
}