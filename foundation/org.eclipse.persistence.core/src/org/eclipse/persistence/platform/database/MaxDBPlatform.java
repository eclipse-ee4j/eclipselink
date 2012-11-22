/*******************************************************************************
 * Copyright (c) 2009,2010 Markus Karg, SAP. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Markus Karg - initial contribution (bug 284657)
 * SAP AG      - finalized implementation (bug 327778)
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.expressions.ListExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * <b>Database Platform for SAP MaxDB.</b>
 * <p>
 * <b>Wiki page:</b> {@link http://wiki.eclipse.org/EclipseLink/Development/DatabasePlatform/MaxDBPlatform}
 * <p>
 * <b>Usage</b>
 * <p>
 * The MaxDB platform is configured in the persistence.xml by the following property:
 * <p>
 * <code>&lt;property name="eclipselink.target-database" value="MaxDB"/&gt;</code>
 * <p>
 * Forward mapping with EclipseLink assumes that MaxDB is configured for unicode (in version 7.7, this is the default). Unicode mode also needs to be specified in the URL as follows:
 * <p>
 * <code>jdbc:sapdb://localhost/E32?unicode=yes</code>
 * <p>
 * <b>Tested with:</b>
 * <ul>
 * <li>DB: MaxDB, kernel 7.8.01 build 004-123-218-928</li>
 * <li>JDBC driver: MaxDB JDBC Driver, SAP AG, 7.6.06 Build 006-000-009-234 (Make-Version: 7.8.01 Build 003-123-215-703)</li>
 * </ul>
 *
 * <b>Limitations:</b>
 * <br>
 * <ul>
 * <li>The platform class must not be used with XA transactions - see bug 329773.</li>
 * <li>SetQueryTimeout or the hint "javax.persistence.query.timeout" do not work on MaxDB - see bug 326503.</li>
 * <li>The hint "javax.persistence.lock.timeout" has no effect with a positive value; a value of 0 is translated to NOWAIT.</li>
 * <li>The maximum width of an index is 1024 bytes on MaxDB. This also limits the size of a primary key. Moreover the primary key of join tables must not exceed this limit either. As it is composed of the primary key of the two tables that are joined, the combined width of the PKs of these two tables must not exceed this limit. See bug bug 326968.</li>
 * <li>VARCHAR [UNICODE] columns do not preserve trailing spaces - see bug 327435.</li>
 * <li>VARCHAR BYTE columns do not preserve trailing 0 bytes.</li>
 * <li>The hint "javax.persistence.lock.timeout=0" (NOWAIT) has no effect when atempting to pessimistically lock an entity with inheritance type JOINED - see bug 326799.</li>
 * <li>Pessimistic locking with lock scope EXTENDED should be used cautiously in the presence of foreign key constraints - see bug 327472.</li>

 * </ul>
 * <br>
 * @author Markus KARG (markus at headcrashing.eu)
 * @author afischbach
 * @author agoerler
 * @author Sabine Heider (sabine.heider at sap.com)
 * @author Konstantin Schwed (konstantin.schwed at sap.com)
 */
@SuppressWarnings("serial")
public final class MaxDBPlatform extends DatabasePlatform {

    private static final FieldTypeDefinition FIELD_TYPE_DEFINITION_CLOB = new FieldTypeDefinition("LONG UNICODE", false);

    private static final FieldTypeDefinition FIELD_TYPE_DEFINITION_BLOB = new FieldTypeDefinition("LONG BYTE", false);

    /**
     * Maximum length of type VARCHAR UNICODE
     *
     * ({@link http://maxdb.sap.com/doc/7_8/45/33337d9faf2b34e10000000a1553f7/content.htm})
     */
    private static final int MAX_VARCHAR_UNICODE_LENGTH = 4000; //

    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }

    @Override
    public String getSelectForUpdateString() {
        return " WITH LOCK EXCLUSIVE";
    }

    @Override
    public String getSelectForUpdateNoWaitString() {
        return " WITH LOCK (NOWAIT) EXCLUSIVE";
    }

    public MaxDBPlatform(){
        super();
        this.pingSQL = "SELECT 1 FROM DUAL";
    }

    @Override
    protected final Hashtable buildFieldTypes() {
        final Hashtable<Class, FieldTypeDefinition> fieldTypeMapping = new Hashtable<Class, FieldTypeDefinition>();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("SMALLINT", false)); // TODO boolean
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DOUBLE PRECISION", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("FIXED", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE PRECISION", false));

        fieldTypeMapping.put(BigInteger.class, new FieldTypeDefinition("FIXED",19));
        fieldTypeMapping.put(BigDecimal.class, new FieldTypeDefinition("FIXED", 38));

        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1, "UNICODE"));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("VARCHAR", 255, "UNICODE"));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("VARCHAR", 255, "UNICODE"));
        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", 255, "UNICODE"));

        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false)); // can't be mapped to CHAR(1) BYTE as byte in java is signed
        fieldTypeMapping.put(Byte[].class, FIELD_TYPE_DEFINITION_BLOB);
        fieldTypeMapping.put(byte[].class, FIELD_TYPE_DEFINITION_BLOB);
        fieldTypeMapping.put(Blob.class, FIELD_TYPE_DEFINITION_BLOB);
        fieldTypeMapping.put(Clob.class, FIELD_TYPE_DEFINITION_CLOB);

        fieldTypeMapping.put(Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        return fieldTypeMapping;
    }

    @Override
    public boolean supportsIndividualTableLocking() {
        return false;
    }

    @Override
    /**
     * EclipseLink does not support length dependent type mapping.
     * Map VARCHAR types with length > MAX_VARCHAR_UNICODE_LENGTH to LONG UNICODE (i.e clob); shorter types to VARCHAR (n) UNICODE
     * See also bugs 317597, 317448
     */
    protected void printFieldTypeSize(Writer writer, FieldDefinition field, FieldTypeDefinition fieldType) throws IOException {
        String typeName = fieldType.getName();
        if ("VARCHAR".equals(typeName) && "UNICODE".equals(fieldType.getTypesuffix())) {
            if (field.getSize() > MAX_VARCHAR_UNICODE_LENGTH) {
                fieldType = FIELD_TYPE_DEFINITION_CLOB;
            }
        }


        super.printFieldTypeSize(writer, field, fieldType);
        if (fieldType.getTypesuffix() != null) {
            writer.append(" " + fieldType.getTypesuffix());
        }
    }


    @Override
    protected final void initializePlatformOperators() {
        super.initializePlatformOperators();
        this.addOperator(MaxDBPlatform.createConcatExpressionOperator());
        this.addOperator(MaxDBPlatform.createTrim2ExpressionOperator());
        this.addOperator(MaxDBPlatform.createToNumberOperator());
        this.addOperator(MaxDBPlatform.createNullifOperator());
        this.addOperator(MaxDBPlatform.createCoalesceOperator());
        this.addOperator(MaxDBPlatform.createTodayExpressionOperator());
        this.addOperator(MaxDBPlatform.createCurrentDateExpressionOperator());
        this.addOperator(MaxDBPlatform.createCurrentTimeExpressionOperator());
        this.addNonBindingOperator(MaxDBPlatform.createNullValueOperator());
    }

    private static final ExpressionOperator createConcatExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "||");
    }

    /**
     * Creates the expression operator representing the JPQL function current_timestamp as defined by 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_timestamp as defined by 4.6.17.2.3 of the JPA 2.0 specification
     */
    private static final ExpressionOperator createTodayExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Today, "TIMESTAMP");
    }

    /**
     * Creates the expression operator representing the JPQL function current_date as defined by 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_date as defined by 4.6.17.2.3 of the JPA 2.0 specification
     */
    private static final ExpressionOperator createCurrentDateExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.CurrentDate, "DATE");
    }

    /**
     * Creates the expression operator representing the JPQL function current_timestamp as defined by 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_timestamp as defined by 4.6.17.2.3 of the JPA 2.0 specification
     */
    private static final ExpressionOperator createCurrentTimeExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.CurrentTime, "TIME");
    }

    private static final ExpressionOperator createTrim2ExpressionOperator() {
        return ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Trim2, "TRIM");
    }

    private static final ExpressionOperator createNullValueOperator() {
        return ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "VALUE");
    }

    /* see bug 316774 */
    private static final ExpressionOperator createCoalesceOperator() {
        ListExpressionOperator operator = (ListExpressionOperator) ExpressionOperator.coalesce();
        operator.setStartString("VALUE(");
        operator.setSelector(ExpressionOperator.Coalesce);
        return operator;
    }

    private static final ExpressionOperator createToNumberOperator() {
        return ExpressionOperator.simpleFunction(ExpressionOperator.ToNumber, "NUM");
    }

    private static final ExpressionOperator createNullifOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.NullIf);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(4);
        v.addElement(" (CASE WHEN ");
        v.addElement(" = ");
        v.addElement(" THEN NULL ELSE ");
        v.addElement(" END) ");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    @Override
    public boolean shouldOptimizeDataConversion() {
        return true; // TODO is this needed? (seems to default to true)
    }

    private void addNonBindingOperator(ExpressionOperator operator) {
        operator.setIsBindingSupported(false);
        addOperator(operator);
    }

    @Override
    public final boolean supportsNativeSequenceNumbers() {
        return true;
    }

    @Override
    public final ValueReadQuery buildSelectQueryForSequenceObject(final String sequenceName, final Integer size) {
        return new ValueReadQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL");
    }

    @Override
    protected final String getCreateTempTableSqlPrefix() {
        return "CREATE TABLE ";
    }

    @Override
    public final int getMaxFieldNameSize() {
        return 32;
    }

    @Override
    public final boolean supportsLocalTempTables() {
        return true;
    }

    @Override
    public final DatabaseTable getTempTableForTable(final DatabaseTable table) {
        return new DatabaseTable("$" + table.getName(), "TEMP");
    }

    @Override
    public final boolean isMaxDB() {
        return true;
    }

    @Override
    public final boolean shouldAlwaysUseTempStorageForModifyAll() {
        return true;
    }

    @Override
    public final boolean shouldBindLiterals() {
        return false;
    }

    @Override
    public final boolean shouldPrintOuterJoinInWhereClause() {
        return false;
    }

    @Override
    public final boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public final boolean supportsStoredFunctions() {
        return true;
    }

    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call) {
        return true;
    }

}
