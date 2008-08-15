/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

// javase imports
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import static java.lang.Integer.MIN_VALUE;

// TopLink imports
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.getDatabaseTypeForCode;

/**
 * <b>Purpose</b>: 
 * Generates an Anonymous PL/SQL block to invoke the specified Stored Procedure
 * with arguments that may or may not have JDBC equivalents
 */
public class PLSQLStoredProcedureCall extends StoredProcedureCall {

    // can't use Helper.cr, PL/SQL parser only likes Unix '\n'
    public final static String BEGIN_DECLARE_BLOCK = "\nDECLARE\n"; 
    public final static String BEGIN_BEGIN_BLOCK = "BEGIN\n";
    public final static String END_BEGIN_BLOCK = "END;";

    protected List<PLSQLargument> arguments = 
        new ArrayList<PLSQLargument>();
    protected int originalIndex = 0;
    protected AbstractRecord translationRow;

    public PLSQLStoredProcedureCall() {
        super();
    }

    /**
     * PUBLIC:
     * Add a named IN argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex)
     */
    public void addNamedArgument(String procedureParameterName, DatabaseType databaseType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++, IN,
            databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType));
    }

    /**
     * PUBLIC:
     * Add a named IN argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra length parameter
     * indicates that this parameter, when used in an Anonymous PL/SQL block, requires a length.
     */
    public void addNamedArgument(String procedureParameterName, DatabaseType databaseType,
        int length) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++, IN,
            databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, length));
    }

    /**
     * PUBLIC:
     * Add a named IN argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra scale and precision
     * parameters indicates that this parameter, when used in an Anonymous PL/SQL block, requires
     * scale and precision specification
     */
    public void addNamedArgument(String procedureParameterName, DatabaseType databaseType,
        int precision, int scale) {        
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++, IN,
            databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, precision, scale));
    }
    
    @Override
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++, IN,
            getDatabaseTypeForCode(type))); // figure out databaseType from the sqlType
    }

    @Override
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type,
        String typeName) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++, IN,
            getDatabaseTypeForCode(type)));
    }

    /**
     * PUBLIC:
     * Add a named IN OUT argument to the stored procedure. The databaseType parameter classifies
     * the parameter (JDBCType vs. OraclePLSQLType, simple vs. complex)
     */
    public void addNamedInOutputArgument(String procedureParameterName, DatabaseType databaseType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType));
    }

    /**
     * PUBLIC:
     * Add a named IN OUT argument to the stored procedure. The databaseType parameter classifies
     * the parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra length parameter
     * indicates that this parameter, when used in an Anonymous PL/SQL block, requires a length.
     */
    public void addNamedInOutputArgument(String procedureParameterName, DatabaseType databaseType,
        int length) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, length));
    }

    /**
     * PUBLIC:
     * Add a named IN OUT argument to the stored procedure. The databaseType parameter classifies
     * the parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra scale and precision
     * parameters indicates that this parameter, when used in an Anonymous PL/SQL block, requires
     * scale and precision specification
     */
    public void addNamedInOutputArgument(String procedureParameterName, DatabaseType databaseType,
        int precision, int scale) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, precision, scale));
    }

    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, int type) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, getDatabaseTypeForCode(type)));
    }

    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, int type, String typeName) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, getDatabaseTypeForCode(type)));
    }

    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, int type, String typeName, Class classType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, getDatabaseTypeForCode(type)));
    }

    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, int type, String typeName, Class javaType,
        DatabaseField nestedType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            INOUT, getDatabaseTypeForCode(type)));
    }

    /**
     * PUBLIC:
     * Add a named OUT argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex)
     */
    public void addNamedOutputArgument(String procedureParameterName, DatabaseType databaseType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType));
    }

    /**
     * PUBLIC:
     * Add a named OUT argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra length parameter
     * indicates that this parameter, when used in an Anonymous PL/SQL block, requires a length.
     */
    public void addNamedOutputArgument(String procedureParameterName, DatabaseType databaseType,
        int length) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, length));
    }

    /**
     * PUBLIC:
     * Add a named OUT argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex). The extra scale and precision
     * parameters indicates that this parameter, when used in an Anonymous PL/SQL block, requires
     * scale and precision specification
     */
    public void addNamedOutputArgument(String procedureParameterName, DatabaseType databaseType,
        int precision, int scale) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, databaseType.isComplexDatabaseType() ? 
                ((ComplexDatabaseType)databaseType).clone() : databaseType, precision, scale));
    }

    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        int jdbcType, String typeName, Class javaType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, getDatabaseTypeForCode(jdbcType)));
    }
    
    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        int jdbcType, String typeName, Class javaType, DatabaseField nestedType) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, getDatabaseTypeForCode(jdbcType)));
    }
    
    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        int type, String typeName) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, getDatabaseTypeForCode(type)));
    }

    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        int type) {
        arguments.add(new PLSQLargument(procedureParameterName, originalIndex++,
            OUT, getDatabaseTypeForCode(type)));
    }
    
    // un-supported addXXX operations

    @Override
    public void addNamedArgument(String procedureParameterAndArgumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named arguments without DatabaseType classification");
    }
    
    @Override
    public void addNamedArgumentValue(String procedureParameterName, Object argumentValue) {
        throw QueryException.addArgumentsNotSupported(
            "named argument values without DatabaseType classification");
    }

    @Override
    public void addNamedArgument(String procedureParameterName, String argumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named argument values without DatabaseType classification");
    }

    @Override
    public void addNamedInOutputArgument(String procedureParameterAndArgumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named IN OUT argument without DatabaseType classification");
    }
    
    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String argumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named IN OUT arguments without DatabaseType classification");
    }
    
    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String argumentFieldName,
        Class type) {
        throw QueryException.addArgumentsNotSupported(
            "named IN OUT arguments without DatabaseType classification");
    }
    
    @Override
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, Class type) {
        throw QueryException.addArgumentsNotSupported(
            "named IN OUT arguments without DatabaseType classification");
    }

    @Override
    public void addNamedInOutputArgumentValue(String procedureParameterName,
        Object inArgumentValue, String outArgumentFieldName, Class type) {
        throw QueryException.addArgumentsNotSupported(
            "named IN OUT argument values without DatabaseType classification");
    }

    @Override
    public void addNamedOutputArgument(String procedureParameterAndArgumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named OUT arguments without DatabaseType classification");
    }
    
    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName) {
        throw QueryException.addArgumentsNotSupported(
            "named OUT arguments without DatabaseType classification");
    }
    
    @Override
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        Class type) {
        throw QueryException.addArgumentsNotSupported(
            "named OUT arguments without DatabaseType classification");
    }
    
    @Override
    public void useNamedCursorOutputAsResultSet(String argumentName) {
        throw QueryException.addArgumentsNotSupported(
            "named OUT cursor arguments without DatabaseType classification");
    }
    
    // unlikely we will EVER support unnamed parameters
    @Override
    public void addUnamedArgument(String argumentFieldName, Class type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedArgument(String argumentFieldName, int type, String typeName,
        DatabaseField nestedType) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedArgument(String argumentFieldName, int type, String typeName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedArgument(String argumentFieldName, int type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedArgument(String argumentFieldName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedArgumentValue(Object argumentValue) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String argumentFieldName, Class type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName,
        Class type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName,
        int type, String typeName, Class collection, DatabaseField nestedType) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName,
        int type, String typeName, Class collection) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName,
        int type, String typeName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName,
        int type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgument(String argumentFieldName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedInOutputArgumentValue(Object inArgumentValue, String outArgumentFieldName,
        Class type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName, Class type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName, int jdbcType, String typeName,
        Class javaType, DatabaseField nestedType) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName, int jdbcType, String typeName,
        Class javaType) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName, int type, String typeName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName, int type) {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    @Override
    public void addUnamedOutputArgument(String argumentFieldName) {
        throw QueryException.unnamedArgumentsNotSupported();
    }
    
    @Override
    public void useUnnamedCursorOutputAsResultSet() {
        throw QueryException.unnamedArgumentsNotSupported();
    }

    /**
     * PUBLIC:
     * Add a named OUT cursor argument to the stored procedure. The databaseType parameter classifies the
     * parameter (JDBCType vs. OraclePLSQLType, simple vs. complex).
     */
    public void useNamedCursorOutputAsResultSet(String argumentName, DatabaseType databaseType) {
        PLSQLargument newArg = 
            new PLSQLargument(argumentName, originalIndex++, OUT, 
                databaseType.isComplexDatabaseType() ? 
                    ((ComplexDatabaseType)databaseType).clone() : databaseType);
        newArg.cursorOutput = true;
        arguments.add(newArg);
    }

    /**
     * INTERNAL
     * compute the re-ordered indices - Do the IN args first, then the 'IN-half' of the INOUT args
     * next, the OUT args, then the 'OUT-half' of the INOUT args
     */
    protected void assignIndices() {

        List<PLSQLargument> inArguments = getArguments(arguments, IN);
        List<PLSQLargument> inOutArguments = getArguments(arguments, INOUT);
        inArguments.addAll(inOutArguments);
        int newIndex = 1;
        for (ListIterator<PLSQLargument> inArgsIter = inArguments.listIterator();
            inArgsIter.hasNext(); ) {
            PLSQLargument inArg = inArgsIter.next();
            // delegate to arg's DatabaseType - ComplexTypes may expand arguments
            // use ListIterator so that computeInIndex can add expanded args
            newIndex = inArg.databaseTypeWrapper.getWrappedType().computeInIndex(inArg, newIndex,
                inArgsIter);
        }
        for (PLSQLargument inArg : inArguments) {
            if (!inArg.databaseTypeWrapper.getWrappedType().isComplexDatabaseType()) {
                super.addNamedArgument(inArg.name, inArg.name,
                    inArg.databaseTypeWrapper.getWrappedType().getConversionCode());
            }
            else {
                if (inArg.inIndex != MIN_VALUE) {
                    super.addNamedArgument(inArg.name, inArg.name,
                        inArg.databaseTypeWrapper.getWrappedType().getConversionCode());
                }
            }
        }
        List<PLSQLargument> outArguments = getArguments(arguments, OUT);
        outArguments.addAll(inOutArguments);
        for(ListIterator<PLSQLargument> outArgsIter = outArguments.listIterator();
            outArgsIter.hasNext(); ) {
            PLSQLargument outArg = outArgsIter.next();
            newIndex = outArg.databaseTypeWrapper.getWrappedType().computeOutIndex(outArg, newIndex,
                outArgsIter);
        }
        for (PLSQLargument outArg : outArguments) {
            if (!outArg.databaseTypeWrapper.getWrappedType().isComplexDatabaseType()) {
                super.addNamedOutputArgument(outArg.name, outArg.name,
                    outArg.databaseTypeWrapper.getWrappedType().getConversionCode());
            }
            else {
                if (outArg.outIndex != MIN_VALUE) {
                    super.addNamedOutputArgument(outArg.name, outArg.name,
                        outArg.databaseTypeWrapper.getWrappedType().getConversionCode(), 
                        ((ComplexDatabaseType)outArg.databaseTypeWrapper.getWrappedType()).
                            getCompatibleType());
                }
            }
        }
    }
    
    /**
     * INTERNAL
     * Generate portion of the Anonymous PL/SQL block that declares the temporary variables in the
     * DECLARE section
     * @param sb
     */
    protected void buildDeclareBlock(StringBuilder sb) {

        List<PLSQLargument> inArguments = getArguments(arguments, IN);
        List<PLSQLargument> inOutArguments = getArguments(arguments, INOUT);
        inArguments.addAll(inOutArguments);
        List<PLSQLargument> outArguments = getArguments(arguments, OUT);
        for (PLSQLargument arg : inArguments) {
            arg.databaseTypeWrapper.getWrappedType().buildInDeclare(sb, arg);
        }
        for (PLSQLargument arg : outArguments) {
            arg.databaseTypeWrapper.getWrappedType().buildOutDeclare(sb, arg);
        }
    }

    /**
     * INTERNAL
     * Generate portion of the Anonymous PL/SQL block that assigns fields at the beginning of the
     * BEGIN block (before invoking the target procedure)
     * @param sb
     */
    protected void buildBeginBlock(StringBuilder sb) {

        List<PLSQLargument> inArguments = getArguments(arguments, IN);
        inArguments.addAll(getArguments(arguments, INOUT));
        for (PLSQLargument arg : inArguments) {
            arg.databaseTypeWrapper.getWrappedType().buildBeginBlock(sb, arg);
        }
    }

    /**
     * INTERNAL
     * Generate portion of the Anonymous PL/SQL block that invokes the target procedure
     * @param sb
     */
    protected void buildProcedureInvocation(StringBuilder sb) {

        sb.append("  ");
        sb.append(getProcedureName());
        sb.append("(");
        int size = arguments.size(), idx = 1;
        for (PLSQLargument arg : arguments) {
            sb.append(arg.name);
            sb.append("=>");
            sb.append(databaseTypeHelper.buildTarget(arg));
            if (idx < size) {
                sb.append(", ");
                idx++;
            }
        }
        sb.append(");\n");
    }

    /**
     * INTERNAL
     * Generate portion of the Anonymous PL/SQL block after the target procedures
     * has been invoked and OUT parameters must be handled
     * @param sb
     */
    protected void buildOutAssignments(StringBuilder sb) {
        List<PLSQLargument> outArguments = getArguments(arguments, OUT);
        outArguments.addAll(getArguments(arguments, INOUT));
        for (PLSQLargument arg : outArguments) {
            arg.databaseTypeWrapper.getWrappedType().buildOutAssignment(sb, arg);
        }
    }
    /**
     * Generate the Anonymous PL/SQL block 
     */
    @Override
    protected void prepareInternal(AbstractSession session) {
        // create a copy of the arguments re-ordered with different indices
        assignIndices();
        // build the Anonymous PL/SQL block in sections
        StringBuilder sb = new StringBuilder();
        sb.append(BEGIN_DECLARE_BLOCK);
        buildDeclareBlock(sb);
        sb.append(BEGIN_BEGIN_BLOCK);
        buildBeginBlock(sb);
        buildProcedureInvocation(sb);
        buildOutAssignments(sb);
        sb.append(END_BEGIN_BLOCK);
        setSQLStringInternal(sb.toString());
        super.prepareInternalParameters(session);
    }
    
    @Override
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow,
        AbstractSession session) {
        // re-order elements in translationRow to conform to re-ordered indices
        AbstractRecord copyOfTranslationRow = (AbstractRecord)translationRow.clone();
        int len = copyOfTranslationRow.size();
        Vector copyOfTranslationFields = copyOfTranslationRow.getFields();
        translationRow.clear();
        Vector translationRowFields = translationRow.getFields();
        translationRowFields.setSize(len);
        Vector translationRowValues = translationRow.getValues();
        translationRowValues.setSize(len);
        for (PLSQLargument arg : arguments) {
            if (arg.direction == IN || arg.direction == INOUT) {
                arg.databaseTypeWrapper.getWrappedType().translate(arg, translationRow,
                    copyOfTranslationRow, copyOfTranslationFields, translationRowFields,
                    translationRowValues);
            }
        }
        this.translationRow = translationRow; // save a copy for logging
        super.translate(translationRow, modifyRow, session);
    }

    @Override
    public AbstractRecord buildOutputRow(CallableStatement statement) throws SQLException {

        // re-order elements in outputRow to conform to original indices
        AbstractRecord outputRow = super.buildOutputRow(statement);
        Vector outputRowFields = outputRow.getFields();
        Vector outputRowValues = outputRow.getValues();
        DatabaseRecord newOutputRow = new DatabaseRecord();
        List<PLSQLargument> outArguments = getArguments(arguments, OUT);
        outArguments.addAll(getArguments(arguments, INOUT));
        Collections.sort(outArguments, new Comparator<PLSQLargument>() {
            public int compare(PLSQLargument o1, PLSQLargument o2) {
                return o1.originalIndex - o2.originalIndex;
            }
        });
        for (PLSQLargument outArg : outArguments) {
            if (outArg.databaseTypeWrapper.getWrappedType().isComplexDatabaseType()) {
                ((ComplexDatabaseType)outArg.databaseTypeWrapper.getWrappedType()).setCall(this);
            }
            outArg.databaseTypeWrapper.getWrappedType().buildOutputRow(outArg, outputRow,
                newOutputRow, outputRowFields, outputRowValues);
        }
        return newOutputRow;
    }

    @Override
    public String getLogString(Accessor accessor) {

        StringBuilder sb = new StringBuilder(getSQLString());
        sb.append(Helper.cr());
        sb.append("\tbind => [");
        List<PLSQLargument> inArguments = getArguments(arguments, IN);
        inArguments.addAll(getArguments(arguments, INOUT));
        Collections.sort(inArguments, new Comparator<PLSQLargument>() {
            public int compare(PLSQLargument o1, PLSQLargument o2) {
                return o1.inIndex - o2.inIndex;
            }
        });
        for (Iterator<PLSQLargument> i = inArguments.iterator(); i.hasNext(); ) {
            PLSQLargument inArg = i.next();
            inArg.databaseTypeWrapper.getWrappedType().logParameter(sb, IN, inArg, translationRow, 
                getQuery().getSession().getPlatform());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        List<PLSQLargument> outArguments = getArguments(arguments, OUT);
        outArguments.addAll(getArguments(arguments, INOUT));
        Collections.sort(outArguments, new Comparator<PLSQLargument>() {
            public int compare(PLSQLargument o1, PLSQLargument o2) {
                return o1.outIndex - o2.outIndex;
            }
        });
        if (!inArguments.isEmpty() && !outArguments.isEmpty()) {
            sb.append(", ");
        }
        for (Iterator<PLSQLargument> i = outArguments.iterator(); i.hasNext(); ) {
            PLSQLargument outArg = i.next();
            outArg.databaseTypeWrapper.getWrappedType().logParameter(sb, OUT, outArg, translationRow,
                getQuery().getSession().getPlatform());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * INTERNAL
     * @param args
     * @param direction
     * @return list of arguments with the specified direction
     */
    protected static List<PLSQLargument> 
        getArguments(List<PLSQLargument> args, Integer direction) {
        List<PLSQLargument> inArgs = new ArrayList<PLSQLargument>();
        for (PLSQLargument arg : args) {
            if (arg.direction == direction) {
                inArgs.add(arg);
            }
        }
        return inArgs;
    }
}