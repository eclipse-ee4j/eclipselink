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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.StoredProcedureCall;
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
@SuppressWarnings("unchecked")
public class PLSQLrecord extends ComplexDatabaseType implements OraclePLSQLType, Cloneable {

    protected List<PLSQLargument> fields =  new ArrayList<PLSQLargument>();

    public PLSQLrecord() {
        super();
    }

    @Override
    public PLSQLrecord clone()  {
        PLSQLrecord clone = (PLSQLrecord)super.clone();
        clone.fields = new ArrayList<PLSQLargument>(fields.size());
        for (PLSQLargument f : fields) {
            clone.fields.add(f.clone());
        }
        return clone;
    }

    public boolean isRecord() {
        return true;
    }

    /**
     * Return the record's fields defined as PLSQLargument.
     */
    public List<PLSQLargument> getFields() {
        return fields;
    }

    public int getSqlCode() {
        if (hasCompatibleType()) {
            return STRUCT;
        } else {
            return OTHER;
        }
    }

    public void addField(PLSQLargument field) {
        fields.add(field);
    }

    public void addField(String fieldName, DatabaseType databaseType) {
        fields.add(new PLSQLargument(fieldName, -1, IN, databaseType));
    }
    public void addField(String fieldName, DatabaseType databaseType, int precision, int scale) {
        fields.add(new PLSQLargument(fieldName, -1, IN, databaseType, precision, scale));
    }
    public void addField(String fieldName, DatabaseType databaseType, int length) {
        fields.add(new PLSQLargument(fieldName, -1, IN, databaseType, length));
    }
    
    public int computeInIndex(PLSQLargument inArg, int newIndex, ListIterator<PLSQLargument> iterator) {
        if (hasCompatibleType()) {
            return super.computeInIndex(inArg, newIndex, iterator);
        }
        else {
            iterator.remove();
            inArg.inIndex = newIndex;
            for (PLSQLargument argument : fields) {
            	argument.inIndex = newIndex++;
                iterator.add(argument);
            }
            return newIndex;
        }
    }

    public int computeOutIndex(PLSQLargument outArg, int newIndex,
        ListIterator<PLSQLargument> iterator) {
        if (hasCompatibleType()) {
            return super.computeOutIndex(outArg, newIndex, iterator);
        }
        else {
            iterator.remove();
            outArg.outIndex = newIndex;
            for (PLSQLargument argument : fields) {
            	argument.outIndex = newIndex++;
                argument.direction = OUT;
                iterator.add(argument);
            }
            return newIndex;
        }
    }

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        if (hasCompatibleType()) {
            super.buildInDeclare(sb, inArg);
        } else {
            if ((getTypeName() == null) || getTypeName().equals("")) {
                throw QueryException.typeNameNotSet(this);        
            }                                                         
            sb.append("  ");                                          
            sb.append(databaseTypeHelper.buildTarget(inArg));         
            sb.append(" ");                                           
            sb.append(getTypeName());                                 
            sb.append(";\n");                                         
        }
    }
    
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        if (hasCompatibleType()) {
            super.buildOutDeclare(sb, outArg);
        } else {
            if ((getTypeName() == null) || getTypeName().equals("")) {
                throw QueryException.typeNameNotSet(this);        
            }
            sb.append("  ");
            sb.append(databaseTypeHelper.buildTarget(outArg));
            sb.append(" ");
            sb.append(getTypeName());
            sb.append(";\n");
        }
    }

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        if (hasCompatibleType()) {
            super.buildBeginBlock(sb, arg, call);
        } else {
            String target = databaseTypeHelper.buildTarget(arg);
            if (arg.direction == IN | arg.direction == INOUT) {
                for (PLSQLargument f : fields) {
                    sb.append("  ");
                    sb.append(target);
                    sb.append('.');
                    sb.append(f.name);
                    sb.append(" := ");
                    sb.append(":");
                    sb.append(f.inIndex);
                    sb.append(";\n");
                }
            }
        }
    }

    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        if (hasCompatibleType()) {
            super.buildOutAssignment(sb, outArg, call);
        } else {
            String target = databaseTypeHelper.buildTarget(outArg);
            for (PLSQLargument f : fields) {
                sb.append("  ");
                sb.append(":");
                sb.append(f.outIndex);
                sb.append(" := ");
                sb.append(target);
                sb.append('.');
                sb.append(f.name);
                sb.append(";\n");
            }
        }
    }

    public void translate(PLSQLargument arg, AbstractRecord translationRow,
            AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
            List<DatabaseField> translationRowFields, List translationRowValues,
            StoredProcedureCall call) {
        if (hasCompatibleType()) {
            super.translate(arg, translationRow, copyOfTranslationRow,
                copyOfTranslationFields, translationRowFields, translationRowValues, call);
        } else {
            for (PLSQLargument argument : fields) {
                databaseTypeHelper.translate(argument, translationRow, copyOfTranslationRow,
                    copyOfTranslationFields, translationRowFields, translationRowValues, call);
            }
        }
    }

    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
                DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
        if (hasCompatibleType()) {
            super.buildOutputRow(outArg, outputRow, newOutputRow, outputRowFields, outputRowValues);
        } else {
            for (PLSQLargument field : fields) {
            	databaseTypeHelper.buildOutputRow(field, outputRow, newOutputRow,
                    outputRowFields, outputRowValues);
            }
        }
    }

    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
                AbstractRecord translationRow, DatabasePlatform platform) {
        if (hasCompatibleType()) {
            super.logParameter(sb, direction, arg, translationRow, platform);
        } else {
            for (Iterator<PLSQLargument> iterator = fields.iterator(); iterator.hasNext(); ) {
                PLSQLargument argument = iterator.next();
                argument.databaseType.logParameter(sb, direction, argument, translationRow,
                    platform);
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
    }
}
