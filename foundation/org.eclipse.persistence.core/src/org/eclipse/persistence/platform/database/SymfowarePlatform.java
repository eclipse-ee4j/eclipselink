/*******************************************************************************
 * Copyright (c) 2009, 2010 Fujitsu Limited. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Fujitsu Ltd. (Dies Koper) - based on TLE-based implementation in Fujitsu's
 *                             Interstage Application Server V9.2 (JPA 1.0)
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 *
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 *  Symfoware Server<br/>
 *  http://wiki.eclipse.org/EclipseLink/Development/Incubator/Extensions/SymfowarePlatform <br/>
 *  Test results: http://wiki.eclipse.org/EclipseLink/Development/DatabasePlatform/SymfowarePlatform/TestResults <br/>
 *  Contributed by: Fujitsu Ltd.<br/>
 *  Contributed under bug: 288715
 *  <p/>
 *
 *  Developed on Symfoware Server V10<br/>
 *  Initial SRG Passes on Symfoware Server V10<br/>
 *  
 *  <p/><b>Feature Testing</b><br/>
 * ----------------------
 * <ul>
 * <li> DDL Generation - Succeeds
 * <li> Outer Join - Succeeds
 * <li> Subquery - Succeeds with Limitations
 * <li> Stored Procedure Calls - Succeeds
 * <li> Stored Procedure Generation - Succeeds
 * <li> Native Sequences/Identifier fields - Succeeds
 * <li> JPA Bulk Update/Delete - Succeeds with Limitations
 * <li> Batch Reading - Succeeds
 * <li> Batch Writing - Succeeds
 * <li> Pessimistic Locking - Succeeds with Limitations
 * <li> First Result/Limit - Succeeds with Limitations
 * <li> Expression Framework - Succeeds with Limitations
 * <li> Delimiters - Succeeds
 * <li> Auto Detection - Succeeds
 * </ul>
 * <br/>
 * <p/><b>Limitations</b><br/>
 * ----------------
 * <ul>
 * <li> Reserved SQL keywords cannot be used as table, column or sequence names. Use a different name, or enclose the name in double quotes. For example: @Column(name="\"LANGUAGE\"")
 * <li> Spaces cannot be used in table, column or sequence names. (bug 304906)
 * <li> Input parameters cannot be used as first two arguments to the LOCATE function at the same time. (bug 304897)
 * <li> The first argument to the SUBSTRING function cannot be an input parameter. (bug 304897)
 * <li> Input parameters cannot be used as adjacent arguments to the CONCAT function. Concatenate the values in Java code first and pass the result to the query instead. (bug 304897)
 * <li> Input parameters cannot be used at both sides of an operand at the same time in an SQL statement (e.g. '? * ?'). Perform the operation in Java code first and pass the result to the query instead.
 * <li> Identity fields cannot be used. When primary key generation type IDENTITY is specified, a database sequence will be used instead.
 * <li> Pessimistic Locking adds 'FOR UPDATE' to the SELECT statement, and cannot be used with queries that use DISTINCT. (bug 304903)
 * <li> Pessimistic Locking cannot be used with queries that select from multiple tables. (bug 304903)
 * <li> The LockNoWait option of Pessimistic Locking cannot be used; it is ignored when specified (i.e. only 'FOR UPDATE' is added to the SELECT statement).
 * <li> Query timeout cannot be used; the timeout value is silently ignored. (bug 304905)
 * <li> Bulk update and delete operations that require multiple tables to be accessed cannot be used (e.g. bulk operation on an entity that is part of an inheritance hierarchy, UpdateAll and DeleteAll queries). (See bug 298193).
 * <li> Dropping of tables, sequences and procedures while the database connection is still open can fail due to unreleased locks. Shut down the Java process that executed the create operation before performing the drop operation, or have the create operation use an unpooled connection that is closed after use (GlassFish's deploy-time table generation function uses an unpooled connection). 
 * <li> The standard deviation (STDDEV) and variance (VARIANCE) functions cannot be used. (bug 304909)
 * <li> '= NULL' and '<> NULL' cannot be used for null comparisons in the WHERE clause. Use 'IS (NOT) NULL' instead.
 * <li> A scrollable cursor policy of CONCUR_UPDATABLE mode cannot be used with queries that select from multiple tables.
 * <li> Columns and literals of different type may need casting to allow them to be compared or assigned. (bug 372178) For example: 
 *   'SELECT ... WHERE CAST(PHONE_ORDER_VARCHAR AS INTEGER) BETWEEN 0 AND 1'
 * <li> Subqueries with joins to the outer query are not supported. (see rfe 298193)
 * <li> Stored functions are not supported. (bug 342409)
 * <li> Using subquery in select list for query specification or single-row SELECT statement is not allowed. (bug 372172)
 * <li> The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified. (bug 342413)
 * <li> A subquery cannot be specified on both sides of a comparison predicate or a quantified predicate. (bug 378313)
 * <li> A base table name to be updated cannot be identical to table name in from clause in query or subquery specification (bug 381302) 
 * </ul>
 * <p/><b>Additional Notes</b><br/>
 * ----------------
 * <ul>
 * <li> When using DDL generation, indices are automatically generated for primary and unique keys.
 * <li> The MOD(x, y) function is executed as 'CASE WHEN y = 0 THEN x ELSE (x - y * TRUNC( x / y )) END' on Symfoware database, which gives the same result as the MOD function on Oracle database. Input parameters cannot be used for both its arguments at the same time. In such case, calculate the modulus in Java code first and pass the result to the query instead.
 * <li> When input parameters are used as arguments to the TRIM function, they are substituted with their values before the SQL statement is sent to the JDBC driver. 
 * <li> When an input parameter is used as argument to the UPPER, LOWER or LENGTH functions, it is substituted with its value before the SQL statement is sent to the JDBC driver.
 * </ul>
 * <p/>
 *  
 *  @author Dies Koper
 *  @author Wu Jie
 *  @since EclipseLink 2.1
 */
@SuppressWarnings("serial")
public class SymfowarePlatform extends DatabasePlatform {

    public SymfowarePlatform() {
        super();
        this.pingSQL = "SELECT 1 FROM RDBII_SYSTEM.RDBII_ASSISTTABLE";
    }

    /**
     * Appends a Date in Symfoware specific format.<br/>
     * Symfoware: DATE'YYYY-MM-DD'
     */
    @Override
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        writer.write("DATE'" + Helper.printDate(date) + "'");
    }

    /**
     * Appends a Time in Symfoware specific format.<br/>
     * Symfoware: TIME'hh:mm:ss'
     */
    @Override
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        writer.write("TIME'" + Helper.printTime(time) + "'");
    }

    /**
     * Appends a TimeStamp in Symfoware specific format.<br/>
     * Note that Symfoware does not support the milli- and nanoseconds.<br/>
     * Symfoware: TIMESTAMP'YYYY-MM-DD hh:mm:ss'
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        writer.write("TIMESTAMP'" + Helper.printTimestampWithoutNanos(timestamp) + "'" );
    }

    /**
     * Write a Calendar in Symfoware specific format.<br/>
     * Note that Symfoware does not support nanoseconds.<br/>
     * Symfoware: CNV_TIMESTAMP(calendar, 'YYYY-MM-DD hh24:mm:ss')
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        writer.write("TIMESTAMP'" + Helper.printCalendarWithoutNanos(calendar) + "'");
    }

    /**
     * Return the mapping of Java class types to database types for the schema
     * framework.
     * 
     * @return hashtable of Java types to FieldTypeDefinition instances
     *         containing Symfoware SQL types.
     */
    @Override
    protected Hashtable<Class<?>, FieldTypeDefinition> buildFieldTypes() {
        Hashtable<Class<?>, FieldTypeDefinition> fieldTypeMapping = new Hashtable<Class<?>, FieldTypeDefinition>();

        // boolean type
        fieldTypeMapping.put(java.lang.Boolean.class, new FieldTypeDefinition(
                "SMALLINT default 0", false));

        // numeric types
        fieldTypeMapping.put(java.lang.Byte.class, new FieldTypeDefinition(
                "SMALLINT", false));
        fieldTypeMapping.put(java.lang.Short.class, new FieldTypeDefinition(
                "SMALLINT", false));
        fieldTypeMapping.put(java.lang.Integer.class, new FieldTypeDefinition(
                "INTEGER", false));
        fieldTypeMapping.put(java.lang.Long.class, new FieldTypeDefinition(
                "NUMERIC", 18));
        fieldTypeMapping.put(java.lang.Float.class, new FieldTypeDefinition(
                "NUMERIC", 18, 4));
        fieldTypeMapping.put(java.lang.Double.class, new FieldTypeDefinition(
                "NUMERIC", 18, 4));
        fieldTypeMapping.put(java.math.BigDecimal.class,
                new FieldTypeDefinition("DECIMAL", 18).setLimits(18, -18, 18));
        fieldTypeMapping.put(java.math.BigInteger.class,
                new FieldTypeDefinition("NUMERIC", 18).setLimits(18, -18, 18));
        fieldTypeMapping.put(java.lang.Number.class, new FieldTypeDefinition(
                "DECIMAL", 18));

        // character types
        fieldTypeMapping.put(java.lang.String.class, new FieldTypeDefinition(
                "VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(java.lang.Character.class,
                new FieldTypeDefinition("CHARACTER", 1));

        // array, binary and Lob types
        fieldTypeMapping.put(byte[].class,
                new FieldTypeDefinition("BLOB", 1024));
        fieldTypeMapping.put(java.lang.Byte[].class, new FieldTypeDefinition(
                "BLOB", 1024));
        // Symfoware's range for a VARCHAR is 1-32000, but the default value of
        // @javax.persistence.Column#length is 255
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("VARCHAR",
                255));
        fieldTypeMapping.put(java.lang.Character[].class,
                new FieldTypeDefinition("VARCHAR", 255));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition(
                "BLOB", 1024));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition(
                "VARCHAR", 255));

        // temporal types
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition(
                "DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition(
                "TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition(
                "TIMESTAMP", false));

        fieldTypeMapping.put(java.util.Calendar.class, new FieldTypeDefinition(
                "TIMESTAMP", false));
        fieldTypeMapping.put(java.util.Date.class, new FieldTypeDefinition(
                "TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
     * Return the mapping of database types to class types for the schema
     * framework.
     * 
     * @return the mappings.
     */
    @Override
    protected Map<String, Class> buildClassTypes() {
        // use what is defined in DatabasePlatform and override those entries
        Map<String, Class> classTypeMapping = super.buildClassTypes();
        classTypeMapping.put("SMALLINT", java.lang.Short.class);
        classTypeMapping.put("INTEGER", java.lang.Integer.class);
        classTypeMapping.put("NUMERIC", java.lang.Long.class);
        classTypeMapping.put("REAL", java.lang.Float.class);
        classTypeMapping.put("DECIMAL", java.math.BigDecimal.class);
        classTypeMapping.put("DATE", java.sql.Date.class);
        classTypeMapping.put("TIME", java.sql.Time.class);
        classTypeMapping.put("TIMESTAMP", java.sql.Timestamp.class);
        classTypeMapping.put("BLOB", java.lang.Byte[].class);
        classTypeMapping.put("BINARY LARGE OBJECT", java.lang.Byte[].class);
        classTypeMapping.put("CHARACTER", java.lang.String.class);
        classTypeMapping.put("VARCHAR", java.lang.String.class);
        classTypeMapping.put("CHAR VARYING", java.lang.String.class);
        classTypeMapping.put("NCHAR", java.lang.String.class);
        classTypeMapping.put("NCHAR VARYING", java.lang.String.class);
        classTypeMapping.put("FLOAT", java.lang.Double.class);
        classTypeMapping.put("DOUBLE PRECISION", java.lang.Double.class);

        return classTypeMapping;
    }

    /**
     * Initialize Symfoware platform-specific operators.
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        // Text
        addNonBindingOperator(ExpressionOperator.toUpperCase());
        addNonBindingOperator(ExpressionOperator.toLowerCase());
        addNonBindingOperator(ExpressionOperator.trim());
        addNonBindingOperator(ExpressionOperator.trim2());
        addNonBindingOperator(leftTrim());
        addNonBindingOperator(leftTrim2());
        addNonBindingOperator(rightTrim());
        addNonBindingOperator(rightTrim2());
        addNonBindingOperator(ExpressionOperator.ascii());
        addNonBindingOperator(charLength());
        addNonBindingOperator(length());
        addNonBindingOperator(ExpressionOperator.leftPad());
        addNonBindingOperator(ExpressionOperator.rightPad());
        // SUBSTRING's 1st argument cannot be an input parameter
        addOperator(substring());
        addOperator(singleArgumentSubstring());
        addOperator(toNumber());
        // maximum of one input parameter allowed as argument to POSITION
        addOperator(locate());
        addOperator(locate2());
        addOperator(instring());
        // arguments cannot both be input parameters
        addOperator(ExpressionOperator.simpleLogicalNoParens(
                ExpressionOperator.Concat, "||"));

        // Date
        addOperator(ExpressionOperator.simpleFunction(
                ExpressionOperator.DateToString, "CNV_CHAR"));
        addOperator(monthsBetween());
        addOperator(roundDate());
        addOperator(toDate());
        addOperator(addDate());
        addOperator(truncateDate());

        // Math
        addNonBindingOperator(ExpressionOperator.ceil());
        addNonBindingOperator(ExpressionOperator.floor());
        addNonBindingOperator(greatest());
        addNonBindingOperator(least());
        addOperator(logOperator());
        addOperator(mod());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(
                ExpressionOperator.Atan2, "ATAN2"));

        // General
        addNonBindingOperator(ExpressionOperator.coalesce());
        addNonBindingOperator(ExpressionOperator.nullIf());
        addNonBindingOperator(ExpressionOperator.isNull());
        addNonBindingOperator(ExpressionOperator.notNull());
        addNonBindingOperator(nvl());
    }

    /**
     * Symfoware does not support the use of parameters in certain functions.<br/>
     * This adds the operator and disables binding support at the same time.
     * 
     * @see #addOperator(ExpressionOperator)
     * @see ExpressionOperator#setIsBindingSupported(boolean)
     */
    protected void addNonBindingOperator(ExpressionOperator operator) {
        operator.setIsBindingSupported(false);
        addOperator(operator);
    }

    /**
     * Symfoware does not support the use of multiple parameters in certain functions.<br/>
     * This allows statements to disable binding only in these cases.
     */
    @Override
    public boolean isDynamicSQLRequiredForFunctions() {
        return true;
    }

    /**
     * Indicates whether SELECT DISTINCT ... FOR UPDATE is allowed by the
     * platform. (Symfoware doesn't allow this).
     */
    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }

    /**
     * Identifies this database platform as Symfoware's.
     */
    public boolean isSymfoware() {
        return true;
    }

    /**
     * Obtains the number of characters in the data string value expression.<br/>
     * Builds Symfoware equivalent to length(string_exp).<br/>
     * 
     * Symfoware: CHAR_LENGTH(string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator length() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Length);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("CHAR_LENGTH(");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Obtains the number of characters in the data string value expression.<br/>
     * Builds Symfoware equivalent to length(string_exp).<br/>
     * 
     * Symfoware: CHAR_LENGTH(string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator charLength() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.CharLength);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("CHAR_LENGTH(");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Evaluates the location of the "tofind" text within the string_exp text.<br/>
     * Builds Symfoware equivalent to locate(string_exp, tofind).<br/>
     * 
     * Symfoware: POSITION(tofind IN string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator locate() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Locate);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("POSITION(");
        v.addElement(" IN ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0 };
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Evaluates the location of the "tofind" text within the "string_exp" text,
     * starting from the given start position.<br/>
     * Builds Symfoware equivalent to locate(string_exp, tofind, startpos).<br/>
     * 
     * Symfoware: LOCATE(tofind IN string_exp, startpos)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator locate2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Locate2);
        Vector<String> v = NonSynchronizedVector.newInstance(4);
        v.addElement("POSITION(");
        v.addElement(" IN ");
        v.addElement(",");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0, 2};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the log10 operator.<br/>
     * Builds Symfoware equivalent to LOG(x).<br/>
     * 
     * Symfoware: (LN(x)/LN(10))
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator logOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Log);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("(LN(");
        v.addElement(")/LN(10))");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(FunctionExpression.class);
        return exOperator;
    }

    /**
     * Returns the string left trimmed for white space.<br/>
     * Builds Symfoware equivalent to LTRIM(string_exp).<br/>
     * 
     * Symfoware: TRIM(LEADING FROM string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator leftTrim() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.LeftTrim);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("TRIM(LEADING FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the string left trimmed for white space.<br/>
     * Builds Symfoware equivalent to LTRIM(string_exp, character).<br/>
     * 
     * Symfoware: TRIM(LEADING character FROM string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator leftTrim2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.LeftTrim2);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("TRIM(LEADING ");
        v.addElement(" FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0 };
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the remainder of a division.<br/>
     * Builds Symfoware equivalent to Oracle's MOD(x, y).<br/>
     * Note that it returns x when y is 0, and the sign of the remainder is
     * taken from x.
     * 
     * Symfoware: CASE WHEN y = 0 THEN x ELSE (x - y * TRUNC( x / y )) END
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator mod() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Mod);
        Vector<String> v = NonSynchronizedVector.newInstance(7);
        v.addElement("(CASE WHEN ");
        v.addElement(" = 0 THEN ");
        v.addElement(" ELSE (");
        v.addElement(" - ");
        v.addElement(" * TRUNC( ");
        v.addElement(" / ");
        v.addElement(")) END)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0, 0, 1, 0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(org.eclipse.persistence.internal.expressions.FunctionExpression.class);
        return exOperator;
    }

    /**
     * Returns the string right trimmed for white space.<br/>
     * Builds Symfoware equivalent to RTRIM(character).<br/>
     * 
     * Symfoware: TRIM(TRAILING FROM string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator rightTrim() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.RightTrim);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("TRIM(TRAILING FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the string right trimmed for white space.<br/>
     * Builds Symfoware equivalent to RTRIM(string_exp, character).<br/>
     * 
     * Symfoware: TRIM(TRAILING character FROM string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator rightTrim2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.RightTrim2);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("TRIM(TRAILING ");
        v.addElement(" FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0 };
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Gets the substring of source bounded by start location to end location. <br/>
     * Builds Symfoware equivalent to SUBSTRING(string_exp, startpos, length).<br/>
     * 
     * Symfoware: SUBSTRING(string_exp FROM startpos FOR length)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator substring() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Substring);
        Vector<String> v = NonSynchronizedVector.newInstance(4);
        v.addElement("SUBSTRING(");
        v.addElement(" FROM ");
        v.addElement(" FOR ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Gets the substring of a string starting from the specified start
     * position.<br/>
     * Builds Symfoware equivalent to SUBSTRING(string_exp, startpos).<br/>
     * 
     * Symfoware: SUBSTRING(string_exp FROM startpos)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator singleArgumentSubstring() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.SubstringSingleArg);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("SUBSTRING(");
        v.addElement(" FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the number converted from the string.<br/>
     * Builds Symfoware equivalent to ToNumber(string_exp).<br/>
     * 
     * Symfoware: CAST(string_exp AS SMALLINT)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator toNumber() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.ToNumber);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("CAST(");
        v.addElement(" AS SMALLINT)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Evaluates the location of the "tofind" text within the string_exp text.<br/>
     * Builds Symfoware equivalent to Instring(string_exp, tofind).<br/>
     * 
     * Symfoware: POSITION(tofind In string_exp)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator instring() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.Instring);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("POSITION(");
        v.addElement(" IN ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 1, 0 };
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the decimal number of months between the two dates.<br/>
     * Builds Symfoware equivalent to monthsBetween(Date, Date).<br/>
     * 
     * Symfoware: SPAN_DATE(Date, Date, 'MONTH')
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator monthsBetween() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.MonthsBetween);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("SPAN_DATE(");
        v.addElement(" , ");
        v.addElement(",'MONTH')");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the date rounded to the year, month or day.<br/>
     * Builds Symfoware equivalent to ROUNDDATE(Date, rounded).<br/>
     * 
     * Symfoware: ROUND_DATE(Date, rounded)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator roundDate() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.RoundDate);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("ROUND_DATE(");
        v.addElement(" , ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Converts the character value expression of the conversion source to the
     * DATE type in accordance.<br/>
     * Builds Symfoware equivalent to toDate(Date, format).<br/>
     * 
     * Symfoware: CNV_DATE(Date, 'YYYY-MM-DD')
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator toDate() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.ToDate);
        Vector<String> v = NonSynchronizedVector.newInstance(2);
        v.addElement("CNV_DATE(");
        v.addElement(", 'YYYY-MM-DD')");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Adds to a date the specified addition number as the specified interval
     * unit and returns the resulting date.<br/>
     * Builds Symfoware equivalent to addDate(unit, number).<br/>
     * 
     * Symfoware: ADD_DATE(date, number, unit)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator addDate() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.AddDate);
        Vector<String> v = NonSynchronizedVector.newInstance(4);
        v.addElement("ADD_DATE(");
        v.addElement(", ");
        v.addElement(", '");
        v.addElement("')");
        exOperator.printsAs(v);
        exOperator.bePrefix();

        int[] indices = new int[3];
        indices[0] = 0;
        indices[1] = 2;
        indices[2] = 1;
        exOperator.setArgumentIndices(indices);
        return exOperator;
    }

    /**
     * Truncates a date by using the truncating unit.<br/>
     * Builds Symfoware equivalent to truncateDate(date, unit).<br/>
     * 
     * Symfoware: TRUNC_DATE(date, unit)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator truncateDate() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setSelector(ExpressionOperator.TruncateDate);
        Vector<String> v = NonSynchronizedVector.newInstance(3);
        v.addElement("TRUNC_DATE(");
        v.addElement(",");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Substitute a value when a null value is encountered.<br/>
     * Builds Symfoware equivalent to NVL(string, replace_with).<br/>
     * 
     * Symfoware: COALESCE(string, replace_with)
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator nvl() {
        return ExpressionOperator.simpleTwoArgumentFunction(
                ExpressionOperator.Nvl, "COALESCE");
    }

    /**
     * Returns the greatest of two values.<br/>
     * Builds Symfoware equivalent to GREATEST(x, y).<br/>
     * 
     * Symfoware: CASE WHEN x >= y THEN x ELSE y END
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator greatest() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Greatest);
        Vector<String> v = NonSynchronizedVector.newInstance(5);
        v.addElement("(CASE WHEN ");
        v.addElement(" >= ");
        v.addElement(" THEN ");
        v.addElement(" ELSE ");
        v.addElement(" END)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Returns the smallest of two values.<br/>
     * Builds Symfoware equivalent to LEAST(x, y).<br/>
     * 
     * Symfoware: CASE WHEN x <= y THEN x ELSE y END
     * 
     * @return the defined expression operator.
     */
    protected static ExpressionOperator least() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Least);
        Vector<String> v = NonSynchronizedVector.newInstance(5);
        v.addElement("(CASE WHEN ");
        v.addElement(" <= ");
        v.addElement(" THEN ");
        v.addElement(" ELSE ");
        v.addElement(" END)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Use the JDBC maxResults and firstResultIndex setting to compute a value
     * to use when limiting the results of a query in SQL. These limits tend to
     * be used in two ways.
     * 
     * <pre>
     * 1. MaxRows is the index of the last row to be returned (like JDBC
     * maxResults)
     * 2. MaxRows is the number of rows to be returned
     * </pre>
     * 
     * Symfoware uses case #2 and therefore the maxResults has to be altered
     * based on the firstResultIndex.
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
         // In Symfoware, this API is used in the follow scenario.
         //   1. construct SELECT ... WITH OPTION LIMIT(comupteMaxRowForSQL());
         //   2. move the cursor of ResultSet to the row number index of firstResultIndex;
         //   3. get (computeMaxRowForSQL() - firstResultIndex) number of rows from row number index of firstResultIndex.
         // There are two different Queries which depend on this API.
         //   Query#setFirstResult(), Query#setMaxResults() in JPA
         //   ReadQuery#setFirstResult, ReadQuery#setMaxRows() in EclipseLink specific API.
         // Note that each method of Query delegates one of ReadQuery respectively.
         // ReadQuery#setMaxRow() is always passed by a converted maxRows value according to below condition.
         //   int maxRows = maxResults + ((firstResultIndex >= 0) ? firstResultIndex : 0);
         // Actually Query#setFirstResult(3) and Query#setMaxResults(6) is equals to ReadQuery#setFirstResult(3) and ReadQuery#setMaxRows(9).
         // Therefore we don't need to compute a value of MaxRows here and just return maxResults.
        return maxResults;
    }

    /**
     * Produce a DataReadQuery which updates(!) the sequence number in the DB
     * and returns it.
     * 
     * @param seqName
     *            known by Symfoware to be a defined sequence
     * @param size
     *            size of sequence
     * @return ValueReadQuery class to perform a read of a single data value.
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName,
            Integer size) {
        return new ValueReadQuery("SELECT " + getQualifiedName(seqName)
                + ".NEXTVAL FROM RDBII_SYSTEM.RDBII_ASSISTTABLE");
    }

    /**
     * Return the CREATE INDEX string for this platform.
     * 
     * @param fullTableName
     *            qualified name of the table the index is to be created on
     * @param indexName
     *            name of the index
     * @param qualifier
     *            qualifier to construct qualified name of index if needed
     * @param isUnique
     *            Indicates whether uniqe index is created
     * @param columnNames
     *            one or more columns the index is created for
     */
    @Override
    public String buildCreateIndex(String fullTableName, String indexName, String qualifier, 
            boolean isUnique, String... columnNames) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("CREATE INDEX ");
        queryString.append(fullTableName).append(".").append(indexName).append(" KEY (");
        queryString.append(columnNames[0]);
        for (int i = 1; i < columnNames.length; i++) {
            queryString.append(", ").append(columnNames[i]);
        }
        queryString.append(")");
        return queryString.toString();
    }

    /**
     * Return the DROP INDEX string for this platform.
     * 
     * @param fullTableName
     *            qualified name of the table the index is to be created on
     * @param indexName
     *            name of the index
     * @param qualifier
     *            qualifier to construct qualified name of index if needed
     */
    @Override
    public String buildDropIndex(String fullTableName, String indexName, String qualifier) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("DROP INDEX ").append(fullTableName).append(".").append(indexName);
        return queryString.toString();
    }

    /**
     * Returns the beginning of the SQL string to create a temporary table.
     * 
     * @see #shouldAlwaysUseTempStorageForModifyAll()
     */
    @Override
    public String getCreateTempTableSqlPrefix() {
        return "CREATE GLOBAL TEMPORARY TABLE ";
    }

    /**
     * Returns the ending of the SQL string to create a temporary table.<br/>
     * Symfoware V10 requires table space name and number of concurrent users.<br/>
     * Maybe this will be implemented in the future, for now it invokes the
     * super class's method.
     */
    @Override
    protected String getCreateTempTableSqlSuffix() {
        return super.getCreateTempTableSqlSuffix();
    }

    /**
     * Returns the table name used by TableSequence by default. Symfoware does
     * not allow reserved keyword 'SEQUENCE' as table name, so returns
     * "SEQUENCE" (with double-quotes) instead.
     */
    @Override
    public String getDefaultSequenceTableName() {
        return "\"SEQUENCE\"";
    }

    /**
     * Used to allow platforms to define their own index prefixes
     * @param isUniqueField
     * @return
     */
    public String getIndexNamePrefix(boolean isUniqueSetOnField){
        if (isUniqueSetOnField){
            return "UIX_";
        }
        return super.getIndexNamePrefix(isUniqueSetOnField);
    }
    
    /**
     * This method is used to print the output parameter token when stored
     * procedures are called.
     */
    @Override
    public String getInOutputProcedureToken() {
        return "INOUT";
    }

    /**
     * Used for stored procedure creation: Prefix for INPUT parameters.
     */
    @Override
    public String getInputProcedureToken() {
        return "IN";
    }

    /**
     * Returns the maximum number of single byte characters that can be used in
     * a field name on this platform, assuming DEFAULT_DSI_NAME=CODE is
     * specified in Symfoware's operating environment file for the system (see
     * Symfoware manual).
     */
    @Override
    public int getMaxFieldNameSize() {
        return 36;
    }
    
    /**
     * Symfoware does not use the AS token.
     */
    @Override
    public String getProcedureAsString() {
        return "";
    }

    /**
     * Used for stored procedure calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return "{CALL ";
    }

    /**
     * Used for stored procedure calls.
     */
    @Override
    public String getProcedureCallTail() {
        return "}";
    }

    /**
     * Used for stored procedure definitions.
     */
    @Override
    public String getProcedureBeginString() {
        return "BEGIN";
    }

    /**
     * Used for stored procedure definitions.
     */
    @Override
    public String getProcedureEndString() {
        return "END";
    }

    /**
     * Symfoware supports "for update" for row locking.
     * 
     * @return the string of "for update".
     */
    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }

    /**
     * This method returns the query to select the timestamp
     * from the database.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT CURRENT_TIMESTAMP FROM RDBII_SYSTEM.RDBII_ASSISTTABLE");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    /**
     * INTERNAL:
     * Returns the minimum time increment supported by the platform.
     */
    public long minimumTimeIncrement() {
        return 1000;
    }
    
    /**
     * Print the pagination SQL using Symfoware syntax
     * " WITH OPTION LIMIT (<max>)". There is no equivalent to 'OFFSET'.<br/>
     * Even though most performance benefit comes from including the offset in
     * the SQL statement, for this platform the benefit of including LIMIT is
     * that it allows pagination with result sets with scrollable cursors too
     * (which the Symfoware JDBC driver's setMaxRows method does not support).
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call,
            ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        int max = 0;

        if (statement.getQuery() != null) {
            max = statement.getQuery().getMaxRows();
        }

        // only MaxRows can be worked into the statement
        if (max > 0 && this.shouldUseRownumFiltering()) {
            statement.setUseUniqueFieldAliases(true);
            call.setFields(statement.printSQL(printer));
            printer.printString(" WITH OPTION LIMIT (");
            printer.printParameter(DatabaseCall.MAXROW_FIELD);
            printer.printString(")");

            call.setIgnoreMaxResultsSetting(true);
        } else {
            // use JDBC absolute (and setMaxRows) methods for pagination
            super.printSQLSelectStatement(call, printer, statement);
        }
    }

    /**
     * Used for stored procedure creation: Symfoware needs brackets around
     * arguments declaration even if no arguments exist, and so returns true.
     */
    @Override
    public boolean requiresProcedureBrackets() {
        return true;
    }

    /**
     * Used for table creation. Symfoware does not support the ALTER TABLE
     * syntax to add/drop unique constraints, but it does allow declaration of
     * (single and multi-column) unique constraints as part of the CREATE TABLE
     * statement.
     * 
     * @return whether unique constraints should be declared as part of the
     *         CREATE TABLE statement instead of in separate ALTER TABLE
     *         ADD/DROP statements.
     */
    @Override
    public boolean requiresUniqueConstraintCreationOnTableCreate() {
        return true;
    }

    
    /**
     * INTERNAL:
     * Used by Exists queries because they just need to select a single row.
     * In most databases, we will select one of the primary key fields.
     * 
     * On Syfoware, there are situations where the key cannot be used.
     * 
     * See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=303396
     * @param subselect
     * 
     * @see SymfowarePlatform
     */
    public void retrieveFirstPrimaryKeyOrOne(ReportQuery subselect){
        subselect.selectValue1();
    }
    
    /**
     * Symfoware does not support the default syntax generated for update-all
     * and delete-all queries as they can include the same table in the FROM
     * clause of the main and the sub-queries. However, the alternative of using
     * global temporary tables leads to table locking issues, so returns false
     * to prevent the use of global temporary tables.
     */
    @Override
    public boolean shouldAlwaysUseTempStorageForModifyAll() {
        return false;
    }

    /**
     * Allows platform to choose whether to bind literals in SQL statements or
     * not.<br/>
     * Literal binding is enabled for Symfoware in general. As a number of
     * functions and operators have restrictions on the use of input parameters,
     * it is disabled for those.
     * 
     * @see #isDynamicSQLRequiredForFunctions()
     */
    @Override
    public boolean shouldBindLiterals() {
        return false;
    }

    /**
     * Used for table creation. Symfoware needs an index to be created
     * explicitly for columns with a primary key constraint.
     * 
     * @return true: indices should be created during table generation
     */
    @Override
    public boolean shouldCreateIndicesForPrimaryKeys() {
        return true;
    }

    /**
     * Used for table creation. Symfoware needs an index to be created
     * explicitly for columns with a unique constraint.
     * 
     * @return true: indices should be created during table generation
     */
    @Override
    public boolean shouldCreateIndicesOnUniqueKeys() {
        return true;
    }

    /**
     * Used for stored procedure creation: Some platforms want to print prefix
     * for INPUT arguments BEFORE NAME. If wanted, override and return true.
     */
    @Override
    public boolean shouldPrintInputTokenAtStart() {
        return true;
    }

    /**
     * This is required in the construction of the stored procedures with output
     * parameters
     */
    @Override
    public boolean shouldPrintOutputTokenBeforeType() {
        return true;
    }

    /**
     * This is required in the construction of the stored procedures with output
     * parameters
     */
    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        return true;
    }

    /**
     * Symfoware stored procedure calls do not require the argument name be
     * printed in the call string.<br/>
     * E.g. call MyStoredProc(?) instead of call MyStoredProc(myvariable = ?)
     */
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall() {
        return false;
    }

    /**
     * Used for stored procedure creation: Symfoware declares variables AFTER
     * the procedure body's BEGIN string.
     */
    @Override
    public boolean shouldPrintStoredProcedureVariablesAfterBeginString() {
        return true;
    }

    /**
     * Indicates whether the ANSI syntax for inner joins (e.g. SELECT FROM t1
     * JOIN t2 ON t1.pk = t2.fk) is supported by this platform. Symfoware does
     * not.
     */
    @Override
    public boolean supportsANSIInnerJoinSyntax() {
        return false;
    }

    /**
     * Symfoware does not support foreign key constraint syntax, so returns
     * false.
     */
    @Override
    public boolean supportsForeignKeyConstraints() {
        return false;
    }

    /**
     * Indicates whether the platform supports global temporary tables. Although
     * Symfoware does, it leads to table locking issues when used from
     * EclipseLink.
     * 
     * @see #shouldAlwaysUseTempStorageForModifyAll()
     */
    @Override
    public boolean supportsGlobalTempTables() {
        return false;
    }

    /**
     * Indicates whether locking clause could be selectively applied only to
     * some tables in a ReadQuery.<br/>
     * Symfoware platform currently only supports FOR UPDATE locking, which
     * cannot be applied to queries that select from multiple tables. Use of
     * other locking strategies (LOCK_MODE) are yet to be explored.
     */
    @Override
    public boolean supportsIndividualTableLocking() {
        return false;
    }

    /**
     * Indicates whether locking clause could be applied to the query that has
     * more than one table.<br/>
     * Symfoware platform currently only supports FOR UPDATE locking, which
     * cannot be applied to queries that select from multiple tables. Use of
     * other locking strategies (LOCK_MODE) are yet to be explored.
     */
    @Override
    public boolean supportsLockingQueriesWithMultipleTables() {
        return false;
    }

    /**
     * Indicates whether the platform supports sequence objects.<br/>
     * Symfoware does through global sequence objects.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    /**
     * Indicates whether the platform supports stored functions.<br/>
     * Although Symfoware supports some stored functions as function routines,
     * their functionality is incompatible with the one EclipseLink provides.
     * So, return false;
     */
    @Override
    public boolean supportsStoredFunctions() {
        return false;
    }

    /**
     * Symfoware supports unique key constraints, so returns true.<br/>
     * Do note that unique constraints cannot be added/removed using
     * "ALTER TABLE ADD/DROP CONSTRAINT" syntax.
     * 
     * @see #requiresUniqueConstraintCreationOnTableCreate()
     */
    @Override
    public boolean supportsUniqueKeyConstraints() {
        return true;
    }

    /**
     * Fail-over is not implemented on platform. This method returns false no
     * matter what caused the failure.
     * 
     * @returns false
     */
    @Override
    public boolean wasFailureCommunicationBased(SQLException exception,
            Connection connection, AbstractSession sessionForProfile) {
        return false;
    }
}

