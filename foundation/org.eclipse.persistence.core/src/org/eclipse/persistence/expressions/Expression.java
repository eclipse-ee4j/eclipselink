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
 *     05/24/2011-2.3 Guy Pelletier 
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 ******************************************************************************/  
package org.eclipse.persistence.expressions;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Define an object-level representation of a database query where clause.</p>
 * <p>
 * <b>Description</b>: An expression is a tree-like structure that defines the selection
 * criteria for a query against objects in the database.  The expression has the advantage
 * over SQL by being at the object-level, i.e. the object model attributes and relationships
 * can be used to be query on instead of the database field names.
 * Because it is an object, not a string the expression has the advantage that is can be
 * easily manipulated through code to easily build complex selection criterias.</p>
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Store the selection criteria in a tree-like structure.
 * <li> Support public manipulation protocols for all comparison and function operators.
 * <li> Use operator overloading to support all primitive types as well as objects.
 * </ul></p>
 */
public abstract class Expression implements Serializable, Cloneable {

    /** Required for serialization compatibility. */
    static final long serialVersionUID = -5979150600092006081L;

    /** Temporary values for table aliasing */
    protected transient DatabaseTable lastTable;
    protected transient DatabaseTable currentAlias;
    protected boolean selectIfOrderedBy = true;
    /** PERF: Cache the hashCode. */
    protected int hashCode = 0;
    
    /** Use the upper() function for case insensitive expression operations (default). 
        Seting this flag to false will use the lower() function instead. */
    public static boolean shouldUseUpperCaseForIgnoreCase = true;

    /**
     * Base Expression Constructor.  Not generally used by Developers
     */
    public Expression() {
        super();
    }

    /**
     * PUBLIC:
     * Function, return an expression that adds to a date based on
     * the specified datePart.  This is equivalent to the Sybase DATEADD function.
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").addDate("year", 2)
     * Java: NA
     * SQL: DATEADD(date, 2, year)
     * </blockquote></pre>
     */
    public Expression addDate(String datePart, int numberToAdd) {
        return addDate(datePart, Integer.valueOf(numberToAdd));
    }

    /**
     * PUBLIC:
     * Function, return an expression that adds to a date based on
     * the specified datePart.  This is equivalent to the Sybase DATEADD function.
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").addDate("year", 2)
     * Java: NA
     * SQL: DATEADD(date, 2, year)
     * </blockquote></pre>
     */
    public Expression addDate(String datePart, Object numberToAdd) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.AddDate);
        List args = new ArrayList(2);
        args.add(Expression.fromLiteral(datePart, this));
        args.add(numberToAdd);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, to add months to a date.
     */
    public Expression addMonths(int months) {
        return addMonths(Integer.valueOf(months));
    }

    /**
     * PUBLIC:
     * Function, to add months to a date.
     */
    public Expression addMonths(Object months) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.AddMonths);
        return anOperator.expressionFor(this, months);
    }

    /**
     * INTERNAL:
     * Find the alias for a given table
     */
    public DatabaseTable aliasForTable(DatabaseTable table) {
        return null;
    }

    /**
     * PUBLIC: Returns an expression equivalent to all of <code>attributeName</code>
     * holding true for <code>criteria</code>.
     * <p>
     * For every expression with an anyOf, its negation has either an allOf or a
     * noneOf.  The following two examples will illustrate as the second is the
     * negation of the first:
     * <p>
     * AnyOf Example: Employees with a non '613' area code phone number.
     * <blockquote><pre>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);
     * ExpressionBuilder employee = new ExpressionBuilder();
     * Expression exp = employee.anyOf("phoneNumbers").get("areaCode").notEqual("613");
     * </blockquote></pre>
     * <p>
     * AllOf Example: Employees with all '613' area code phone numbers.
     * <blockquote><pre>
     * ExpressionBuilder employee = new ExpressionBuilder();
     * ExpressionBuilder phones = new ExpressionBuilder();
     * Expression exp = employee.allOf("phoneNumbers", phones.get("areaCode").equal("613"));
     * SQL:
     * SELECT ... EMPLOYEE t0 WHERE NOT EXISTS (SELECT ... PHONE t1 WHERE
     *                     (t0.EMP_ID = t1.EMP_ID) AND NOT (t1.AREACODE = '613'))
     * </blockquote></pre>
     * <p>
     * allOf is the universal counterpart to the existential anyOf.  To have the
     * condition evaluated for each instance it must be put inside of a subquery,
     * which can be expressed as not exists (any of attributeName some condition).
     * (All x such that y = !Exist x such that !y).
     * <p>Likewise the syntax employee.allOf("phoneNumbers").get("areaCode").equal("613")
     * is not supported for the <pre>equal</pre> must go inside a subQuery.
     * <p>
     * This method saves you from writing the sub query yourself.  The above is
     * equivalent to the following expression:
     * <blockquote><pre>
     * ExpressionBuilder employee = new ExpressionBuilder();
     * ExpressionBuilder phone = new ExpressionBuilder();
     * ReportQuery subQuery = new ReportQuery(Phone.class, phone);
     * subQuery.retreivePrimaryKeys();
     * subQuery.setSelectionCriteria(phone.equal(employee.anyOf("phoneNumbers").and(
     *         phone.get("areaCode").notEqual("613")));
     * Expression exp = employee.notExists(subQuery);
     * </blockquote></pre>
     * <p>
     * Note if employee has no phone numbers allOf ~ noneOf.
     * @param criteria must have its own builder, as it will become the
     * separate selection criteria of a subQuery.
     * @return a notExists subQuery expression
     */
    public Expression allOf(String attributeName, Expression criteria) {
        ReportQuery subQuery = new ReportQuery();
        subQuery.setShouldRetrieveFirstPrimaryKey(true);
        Expression builder = criteria.getBuilder();
        criteria = builder.equal(anyOf(attributeName)).and(criteria.not());
        subQuery.setSelectionCriteria(criteria);
        return notExists(subQuery);
    }

    /**
     * PUBLIC:
     * Return an expression that is the boolean logical combination of both expressions.
     * This is equivalent to the SQL "AND" operator and the Java "&&" operator.
     * <p>Example:
     * <blockquote><pre>
     *  EclipseLink: employee.get("firstName").equal("Bob").and(employee.get("lastName").equal("Smith"))
     *  Java: (employee.getFirstName().equals("Bob")) && (employee.getLastName().equals("Smith"))
     *  SQL: F_NAME = 'Bob' AND L_NAME = 'Smith'
     * </blockquote></pre>
     */
    public Expression and(Expression theExpression) {
        // Allow ands with null.
        if (theExpression == null) {
            return this;
        }

        ExpressionBuilder base = getBuilder();
        Expression expressionToUse = theExpression;

        // Ensure the same builder, unless a parralel query is used.
        // For flashback added an extra condition: if left side is a 'NullExpression'
        // then rebuild on it regardless.
        if ((theExpression.getBuilder() != base) && ((base == this) || (theExpression.getBuilder().getQueryClass() == null))) {
            expressionToUse = theExpression.rebuildOn(base);
        }

        if (base == this) {// Allow and to be sent to the builder.
            return expressionToUse;
        }

        ExpressionOperator anOperator = getOperator(ExpressionOperator.And);
        return anOperator.expressionFor(this, expressionToUse);
    }

    /**
     * PUBLIC:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <p>
     * <table border=0 summary="This table compares an example EclipseLink anyOf Expression to Java and SQL">
     * <tr>
     * <th id="c1">Format</th>
     * <th id="c2">Equivalent</th>
     * </tr>
     * <tr>
     * <td headers="c1">EclipseLink</td>
     * <td headers="c2">
     * <pre>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);</br>
     * ExpressionBuilder builder = new ExpressionBuilder();</br>
     * Expression exp = builder.get("id").equal("14858");</br>
     * exp = exp.or(builder.anyOf("managedEmployees").get("firstName").equal("Bob"));</br>
     * </pre>
     * </td>
     * </tr>
     * <tr>
     * <td headers="c1">Java</td>
     * <td headers="c2">No direct equivalent</td>
     * </tr>
     * <tr>
     * <td headers="c1">SQL</td>
     * <td headers="c2">SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')</td>
     * </tr>
     * </table>
     */
    public Expression anyOf(String attributeName) {
        return anyOf(attributeName, true);
    }

    /**
     * ADVANCED:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <p>
     * <table border=0 summary="This table compares an example EclipseLink anyOf Expression to Java and SQL">
     * <tr>
     * <th id="c1">Format</th>
     * <th id="c2">Equivalent</th>
     * </tr>
     * <tr>
     * <td headers="c1">EclipseLink</td>
     * <td headers="c2">
     * <pre>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);</br>
     * ExpressionBuilder builder = new ExpressionBuilder();</br>
     * Expression exp = builder.get("id").equal("14858");</br>
     * exp = exp.or(builder.anyOf("managedEmployees").get("firstName").equal("Bob"));</br>
     * </pre>
     * </td>
     * </tr>
     * <tr>
     * <td headers="c1">Java</td>
     * <td headers="c2">No direct equivalent</td>
     * </tr>
     * <tr>
     * <td headers="c1">SQL</td>
     * <td headers="c2">SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')</td>
     * </tr>
     * </table>
     * @parameter shouldJoinBeIndependent indicates whether a new expression should be created.
     */
    public Expression anyOf(String attributeName, boolean shouldJoinBeIndependent) {
        throw new UnsupportedOperationException("anyOf");
    }

    /**
     * ADVANCED:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * This version of the anyOf operation performs an outer join.
     * Outer joins allow the join to performed even if the target of the relationship is empty.
     * NOTE: outer joins are not supported on all database and have differing semantics.
     * <p>Example:
     * <p>
     * <table border=0 summary="This table compares an example EclipseLink anyOfAllowingNone Expression to Java and SQL">
     * <tr>
     * <th id="c1">Format</th>
     * <th id="c2">Equivalent</th>
     * </tr>
     * <tr>
     * <td headers="c1">EclipseLink</td>
     * <td headers="c2">
     * <pre>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);</br>
     * ExpressionBuilder builder = new ExpressionBuilder();</br>
     * Expression exp = builder.get("id").equal("14858");</br>
     * exp = exp.or(builder.anyOfAllowingNone("managedEmployees").get("firstName").equal("Bob"));</br>
     * </pre>
     * </td>
     * </tr>
     * <tr>
     * <td headers="c1">Java</td>
     * <td headers="c2">No direct equivalent</td>
     * </tr>
     * <tr>
     * <td headers="c1">SQL</td>
     * <td headers="c2">SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')</td>
     * </tr>
     * </table>
     */
    public Expression anyOfAllowingNone(String attributeName) {
        return anyOfAllowingNone(attributeName, true);
    }
    
    /**
     * ADVANCED:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * This version of the anyOf operation performs an outer join.
     * Outer joins allow the join to performed even if the target of the relationship is empty.
     * NOTE: outer joins are not supported on all database and have differing semantics.
     * <p>Example:
     * <p>
     * <table border=0 summary="This table compares an example EclipseLink anyOfAllowingNone Expression to Java and SQL">
     * <tr>
     * <th id="c1">Format</th>
     * <th id="c2">Equivalent</th>
     * </tr>
     * <tr>
     * <td headers="c1">EclipseLink</td>
     * <td headers="c2">
     * <pre>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);</br>
     * ExpressionBuilder builder = new ExpressionBuilder();</br>
     * Expression exp = builder.get("id").equal("14858");</br>
     * exp = exp.or(builder.anyOfAllowingNone("managedEmployees").get("firstName").equal("Bob"));</br>
     * </pre>
     * </td>
     * </tr>
     * <tr>
     * <td headers="c1">Java</td>
     * <td headers="c2">No direct equivalent</td>
     * </tr>
     * <tr>
     * <td headers="c1">SQL</td>
     * <td headers="c2">SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')</td>
     * </tr>
     * </table>
     * @parameter shouldJoinBeIndependent indicates whether a new expression should be created.
     */
    public Expression anyOfAllowingNone(String attributeName, boolean shouldJoinBeIndependent) {
        throw new UnsupportedOperationException("anyOfAllowingNone");
    }
    
    /**
     * ADVANCED:
     * Return an expression that allows you to treat its base as if it were a subclass of the class returned by the base.
     * @deprecated replaced by {@link #treat(Class)}
     */
    @Deprecated
    public Expression as(Class castClass) {
        return treat(castClass);
    }
    
    /**
     * ADVANCED:
     * Assign an alias to the expression in the select clause.
     */
    public Expression as(String alias) {
        ExpressionOperator operator = getOperator(ExpressionOperator.As);
        return operator.expressionFor(this, literal(alias));
    }
    
    /**
     * ADVANCED:
     * Return an expression that allows you to treat its base as if it were a subclass of the class returned by the base
     * This can only be called on an ExpressionBuilder, the result of expression.get(String), expression.getAllowingNull(String),
     * the result of expression.anyOf("String") or the result of expression.anyOfAllowingNull("String")
     * 
     * as does not guarantee the results of the downcast will be of the specified class and should be used in conjunction
     * with a Expression.type()
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("project").as(LargeProject.class).get("budget").equal(1000)
     *     Java: ((LargeProject)employee.getProjects().get(0)).getBudget() == 1000
     *     SQL: LPROJ.PROJ_ID (+)= PROJ.PROJ_ID AND L_PROJ.BUDGET = 1000
     * </blockquote></pre>
     */
    public Expression treat(Class castClass) {
        return treat(castClass);
    }
    
    /**
     * PUBLIC:
     * This can only be used within an ordering expression.
     * It will order the result ascending.
     * Example:
     * <blockquote><pre>
     *  readAllQuery.addOrderBy(expBuilder.get("address").get("city").ascending())
     * </blockquote></pre>
     */
    public Expression ascending() {
        return getFunction(ExpressionOperator.Ascending);
    }
    
    /**
     * PUBLIC:
     * This can only be used within an ordering expression.
     * Null results will be ordered first.
     * Example:
     * <blockquote><pre>
     *  readAllQuery.addOrderBy(expBuilder.get("address").get("city").ascending().nullsFirst())
     * </blockquote></pre>
     */
    public Expression nullsFirst() {
        return getFunction(ExpressionOperator.NullsFirst);
    }
    
    /**
     * PUBLIC:
     * This can only be used within an ordering expression.
     * Null results will be ordered last.
     * Example:
     * <blockquote><pre>
     *  readAllQuery.addOrderBy(expBuilder.get("address").get("city").ascending().nullsLast())
     * </blockquote></pre>
     */
    public Expression nullsLast() {
        return getFunction(ExpressionOperator.NullsLast);
    }

    /**
     * PUBLIC:
     * Function, returns the single character strings ascii value.
     */
    public Expression asciiValue() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Ascii);
        return anOperator.expressionFor(this);
    }

    /**
     * Sets all tables represented by this expression to be queried as of a past
     * time.
     * <p>
     * Example:
     * <p>
     * <pre>
     *  EclipseLink: employee.asOf(new AsOfClause(pastTime))
     *  Java: None
     *  SQL (Flashback): SELECT ... FROM EMPLOYEE AS OF TIMESTAMP (pastTime) t0 ...
     *  SQL (Generic): .. WHERE (t1.START <= pastTime) AND ((t1.END IS NULL) OR t1.END > pastTime)
     * </pre>
     * <p>
     * Set an as of clause at the expression level to still query for current objects
     * while expressing selection criteria like:
     * <ul>
     * <li>query objects as of one time that met some condition at another time.
     * <li>query objects that changed a certain way over a certain interval (querying for change).
     * </ul>
     * <p>
     * Simultaneously querying on two versions of the same object (one past
     * one present) lets you express these advanced selection criteria.
     * <p>
     * Example: Querying on past attributes using parallel expressions.
     * <p>
     * <blockquote><pre>
     *   // Finds all employees who lived in Ottawa as of a past time.
     *   ExpressionBuilder employee = new ExpressionBuilder();
     *   ExpressionBuilder pastEmployee = new ExpressionBuilder(Employee.class);
     *   pastEmployee.asOf(pastTime);
     *   Expression pastAddress = pastEmployee.get("address"); // by default address will also be as of past time.
     *   Expression selectionCriteria = pastAddress.get("city").equal("Ottawa").and(
     *       employee.equal(pastEmployee));
     * </blockquote></pre>
     * <p>
     * The advantage of the parallel expression is that you can still read current
     * objects, the as of clause will affect only the where clause / selection criteria.
     * <p>
     * You may be tempted to rewrite the above as employee.get("address").asOf(pastTime).
     * That is allowed but see below for the finer points involved in this.
     * <p>
     * Example: Querying on object changes using parallel expressions.
     * <p>
     * <blockquote><pre>
     *   // Finds all employees who recently received a raise.  Note that current
     *   // objects are returned, so can be cached normally.
     *   ExpressionBuilder employee = new ExpressionBuilder();
     *   Expression pastEmployee = new ExpressionBuilder(Employee.class);
     *   pastEmployee.asOf(yesterday);
     *   Expression parallelJoin = employee.equal(pastEmployee);
     *   Expression selectionCriteria = parallelJoin.and(
     *       employee.get("salary").greaterThan(pastEmployee.get("salary")));
     * </blockquote></pre>
     * <p>
     * Example: Querying on object changes using custom query keys
     * <p><blockquote><pre>
     *   // First define the custom query key and add it to your descriptor.
     *   ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
     *   Expression joinCriteria = builder.getField("EMPLOYEE.EMP_ID").equal(builder.getParameter("EMPLOYEE.EMP_ID"));
     *   OneToOneQueryKey selfReferential = new OneToOneQueryKey();
     *   selfReferential.setName("this");
     *   selfReferential.setJoinCriteria(joinCriteria);
     *   selfReferential.setReferenceClass(Employee.class);
     *   getSession().getDescriptor(Employee.class).addQueryKey(selfReferential);
     *
     *   // Now build query as before.
     *   Expression employee = new ExpessionBuilder();
     *   Expression pastEmployee = employee.get("this").asOf(yesterday);
     *   Expression selectionCriteria = employee.get("salary").greaterThan(pastEmployee.get("salary"));
     * </blockquote></pre>
     * <p>
     * Note in general that any parallel expression can be rewritten using a custom query key.
     * EclipseLink will even automatically interpret x.get("this") for you so you do
     * not need to define the above query key first.
     * <p>
     * <b>Full Reference:</b>
     * <p>
     * If an object is mapped to multiple tables, then each table will be as of
     * the same time.  Two objects mapped to the same table can not have different
     * as of times.  Conversely only expressions which have associated tables can have an as
     * of clause.
     * <p>
     * If an as of clause is not explicitly set an expression will use the clause
     * of its base expression, and so on recursively until one is found or an
     * ExpressionBuilder is reached.  Some usage scenarios follow:
     * <ul>
     * <li>employee.asOf(pastTime).anyOf("projects"): projects as of past time.
     * <li>expressionBuilder.asOf(pastTime): entire expression as of past time.
     * <li>employee.asOf(pastTime).anyOf("projects").asOf(null): projects as of current time.
     * <li>employee.anyOf("projects").asOf(pastTime): projects only as of past time.
     * </ul>
     * <p>
     * Watch out for x.asOf(oneTime).get("y").asOf(anotherTime).
     * <ul>
     * <li>emp.anyOf("phoneNumbers").asOf(yesterday) = emp.asOf(yesterday).anyOf("phoneNumbers") but:
     * <li>emp.get("address").asOf(yesterday) != emp.asOf(yesterday).get("address").
     * </ul>
     * Whether the join is also as of yesterday depends on which table the foreign
     * key field resides on.  In an anyOf the foreign key is always on the right, but
     * in a get (1-1) it could be on either side.  For this
     * reason employee.get("address").asOf(yesterday) is undefined as it can mean
     * either 'my address as of yesterday', or 'my address, as of yesterday.'
     * @param pastTime A read only data object used to represent a past time.
     * @return <code>this</code>
     * @since OracleAS EclipseLink 10<i>g</i> (10.0.3)
     * @see org.eclipse.persistence.history.AsOfClause
     * @see #hasAsOfClause
     * @see org.eclipse.persistence.sessions.Session#acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause)
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setAsOfClause(org.eclipse.persistence.history.AsOfClause))
     */
    public Expression asOf(AsOfClause pastTime) {
        throw new UnsupportedOperationException("anyOfAllowingNone");
    }

    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    protected void assignAlias(String name, DatabaseTable tableOrExpression) {
        // By default, do nothing.	
    }

    /**
     * INTERNAL:
     * Assign aliases to any tables which I own. Start with t(initialValue),
     * and return the new value of  the counter , i.e. if initialValue is one
     * and I have tables ADDRESS and EMPLOYEE I will assign them t1 and t2 respectively, and return 3.
     */
    public int assignTableAliasesStartingAt(int initialValue) {
        if (hasBeenAliased()) {
            return initialValue;
        }
        int counter = initialValue;
        List<DatabaseTable> ownedTables = getOwnedTables();
        if (ownedTables != null) {
            for (DatabaseTable table : ownedTables) {
                assignAlias("t" + counter, table);
                counter++;
            }
        }
        return counter;
    }

    /**
     * PUBLIC:
     * Function, This represents the aggregate function Average. Can be used only within Report Queries.
     */
    public Expression average() {
        return getFunction(ExpressionOperator.Average);
    }

    /**
     * PUBLIC:
     * Function, between two bytes
     */
    public Expression between(byte leftValue, byte rightValue) {
        return between(Byte.valueOf(leftValue), Byte.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Function, between two chars
     */
    public Expression between(char leftChar, char rightChar) {
        return between(Character.valueOf(leftChar), Character.valueOf(rightChar));
    }

    /**
     * PUBLIC:
     * Function, between two doubles
     */
    public Expression between(double leftValue, double rightValue) {
        return between(Double.valueOf(leftValue), Double.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Function, between two floats
     */
    public Expression between(float leftValue, float rightValue) {
        return between(Float.valueOf(leftValue), Float.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Function, between two ints
     */
    public Expression between(int leftValue, int rightValue) {
        return between(Integer.valueOf(leftValue), Integer.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Function, between two longs
     */
    public Expression between(long leftValue, long rightValue) {
        return between(Long.valueOf(leftValue), Long.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receiver's value is between two other values.
     * This means the receiver's value is greater than or equal to the leftValue argument and less than or equal to the
     * rightValue argument.
     * <p>
     * This is equivalent to the SQL "BETWEEN AND" operator and Java ">=", "<=;" operators.
     * <p>Example:
     * <pre>
     *     EclipseLink: employee.get("age").between(19,50)
     *     Java: (employee.getAge() >= 19) && (employee.getAge() <= 50)
     *     SQL: AGE BETWEEN 19 AND 50
     * </pre>
     */
    public Expression between(Object leftValue, Object rightValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Between);
        List args = new ArrayList(2);
        args.add(leftValue);
        args.add(rightValue);
        return anOperator.expressionForArguments(this, args);
    }

    public Expression between(Expression leftExpression, Expression rightExpression) {
        return between((Object)leftExpression, (Object)rightExpression);

    }

    /**
     * PUBLIC:
     * Function, between two shorts
     */
    public Expression between(short leftValue, short rightValue) {
        return between(Short.valueOf(leftValue), Short.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Function Convert values returned by the query to values
     * given in the caseItems Map.  The equivalent of
     * the Oracle CASE function
     * <p>Example:
     * <blockquote><pre>
     * Map caseTable = new HashMap();
     * caseTable.put("Robert", "Bob");
     * caseTable.put("Susan", "Sue");
     *
     * EclipseLink: employee.get("name").caseStatement(caseTable, "No-Nickname")
     * Java: NA
     * SQL: CASE name WHEN "Robert" THEN "Bob"
     *     WHEN "Susan" THEN "Sue"
     *  ELSE "No-Nickname"
     * </blockquote></pre>
     * @param caseItems java.util.Map
     * A Map containing the items to be processed.
     * Keys represent the items to match coming from the query.
     * Values represent what a key will be changed to.
     * @param defaultItem java.lang.String  the default value that will be used if none of the keys in the
     * hashtable match
     */
    public Expression caseStatement(Map caseItems, Object defaultItem) {
        FunctionExpression expression = caseStatement();
        expression.addChild(this);
        Iterator iterator = caseItems.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            expression.addChild(Expression.from(key, this));
            expression.addChild(Expression.from(caseItems.get(key), this));
        }
        expression.addChild(Expression.from(defaultItem, this));
        return expression;
    }

    /**
     * INTERNAL:
     * Creates an ArgumentListFunctionExpression that is capable of creating a case statement of the form:
     * <pre><blockquote>
     * SQL: CASE name WHEN "Robert" THEN "Bob"
     *     WHEN "Susan" THEN "Sue"
     *  ELSE "No-Nickname"
     * </blockquote></pre>
     * 
     * This expression must be manipulated to successfully build a case statement by adding appropriate 
     * children to it.
     * 
     * A child must be added for the "case expression" (name above), a pair of children must be added for 
     * each "when then" expression and a child must be added for the else.
     * 
     * @see ArgumentListFunctionExpression
     */
    public ArgumentListFunctionExpression caseStatement() {
        
        ListExpressionOperator caseOperator = (ListExpressionOperator)getOperator(ExpressionOperator.Case);
        ListExpressionOperator clonedCaseOperator = new ListExpressionOperator();
        caseOperator.copyTo(clonedCaseOperator);

        ArgumentListFunctionExpression expression = new ArgumentListFunctionExpression();
        expression.setBaseExpression(this);

        expression.setOperator(clonedCaseOperator);
        return expression;
    }
    
    /**
     * PUBLIC:
     * Function Convert values returned by the query to values
     * given in the caseConditions Map.  The equivalent of
     * the SQL CASE function
     * <p>Example:
     * <blockquote><pre>
     * Map caseTable = new HashMap();
     * caseTable.put(employee.get("name").equals("Robert"), "Bob");
     * caseTable.put(employee.get("name").equals("Susan"), "Sue");
     *
     * EclipseLink: expressionBuilder.caseConditionStatement(caseTable, "No-Nickname")
     * Java: NA
     * SQL: CASE WHEN name = "Robert" THEN "Bob"
     *     WHEN name = "Susan" THEN "Sue"
     *  ELSE "No-Nickname" 
     * </blockquote></pre>
     * @param caseItems java.util.Map
     * A Map containing the items to be processed.
     * Keys represent the items to match coming from the query.
     * Values represent what a key will be changed to.
     * @param defaultItem java.lang.Object  the default value that will be used if none of the keys in the
     * Map match
     */
    public Expression caseConditionStatement(Map<Expression, Object> caseConditions, Object defaultItem) {
        FunctionExpression expression = caseConditionStatement();
        Iterator<Expression> iterator = caseConditions.keySet().iterator();
        if (iterator.hasNext()){
            Expression key = iterator.next();
            expression.addChild(key);
            expression.setBaseExpression(key);//base needs to be the same as the first child for reportQuery items. 
            expression.addChild(Expression.from(caseConditions.get(key), this));
            
            while (iterator.hasNext()) {
                key = iterator.next();
                expression.addChild(key);
                expression.addChild(Expression.from(caseConditions.get(key), this));
            }
        }
        expression.addChild(Expression.from(defaultItem, this));
        return expression;
    }
    
    /**
     * INTERNAL:
     * Creates an ArgumentListFunctionExpression that is capable of creating a case statement of the form:
     * <pre><blockquote>
     * SQL: CASE WHEN name = "Robert" THEN "Bob"
     *     WHEN name = "Susan" THEN "Sue"
     *  ELSE "No-Nickname" 
     * </blockquote></pre>
     * 
     * This expression must be manipulated to successfully build a case statement by adding appropriate 
     * children to it.
     * 
     * A pair of children must be added for  each "when then" expression and a child must be added for the else.
     * 
     * @see ArgumentListFunctionExpression
     */
    public ArgumentListFunctionExpression caseConditionStatement() {
        ListExpressionOperator caseOperator = (ListExpressionOperator)getOperator(ExpressionOperator.CaseCondition);
        ListExpressionOperator clonedCaseOperator = new ListExpressionOperator();
        caseOperator.copyTo(clonedCaseOperator);

        ArgumentListFunctionExpression expression = new ArgumentListFunctionExpression();
        expression.setBaseExpression(this);

        expression.setOperator(clonedCaseOperator);
        return expression;
    }
    
    /**
     * PUBLIC:
     * Function Test if arguments are equal, returning null if they are and the value of the
     * first expression otherwise.
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: builder.get("name").nullIf( "Bobby")
     * Java: NA
     * SQL: NULLIF(name, "Bobby") 
     * </blockquote></pre>
     * @param defaultItem java.lang.Object  the value/expression that will be compared to the base expression
     */
    public Expression nullIf(Object object) {
            ExpressionOperator anOperator = getOperator(ExpressionOperator.NullIf);
            List args = new ArrayList(1);
            args.add(Expression.from(object, this));
            return anOperator.expressionForArguments(this, args);
    }
    
    /**
     * PUBLIC:
     * Function Return null if all arguments are null and the first non-null argument otherwise 
     * The equivalent of the COALESCE SQL function
     * <p>Example:
     * <blockquote><pre>
     * List list = new ArrayList(3);
     * list.add(builder.get("firstName"));
     * list.add(builder.get("lastName"));
     * list.add(builder.get("nickname"));
     *
     * EclipseLink: expressionBuilder.coalesce(caseTable)
     * Java: NA
     * SQL: COALESCE(firstname, lastname, nickname)
     * </blockquote></pre>
     * @param caseItems java.util.Collection
     * A Collection containing the items to check if null
     */
    public ArgumentListFunctionExpression coalesce(Collection expressions) {
        ArgumentListFunctionExpression expression = coalesce();
        Iterator iterator = expressions.iterator();
        if (iterator.hasNext()){
            Expression base = Expression.from(iterator.next(), this);
            expression.addChild(base);
            expression.setBaseExpression(base);//base needs to be the same as the first child for reportQuery items. 
            while (iterator.hasNext()) {
                expression.addChild(Expression.from(iterator.next(), this));
            }
        }
        return expression;
    }
    
    public ArgumentListFunctionExpression coalesce() {
        ListExpressionOperator coalesceOperator = (ListExpressionOperator)getOperator(ExpressionOperator.Coalesce);
        ListExpressionOperator clonedCoalesceOperator = new ListExpressionOperator();
        coalesceOperator.copyTo(clonedCoalesceOperator);

        ArgumentListFunctionExpression expression = new ArgumentListFunctionExpression();
        expression.setBaseExpression(this);

        expression.setOperator(clonedCoalesceOperator);
        return expression;
    }

    /**
     * INTERNAL:
     * Clone the expression maintaining clone identity in the inter-connected expression graph.
     */
    public Object clone() {
        // 2612538 - the default size of Map (32) is appropriate
        Map alreadyDone = new IdentityHashMap();
        return copiedVersionFrom(alreadyDone);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree.
     * This method will rebuildOn the receiver even it is a parallel select or a
     * sub select: it will not replace every base with newBase.
     * Also it will rebuild using anyOf as appropriate not get.
     * @see org.eclipse.persistence.mappings.ForeignReferenceMapping#batchedValueFromRow
     * @see #rebuildOn(Expression)
     * @bug 2637484 INVALID QUERY KEY EXCEPTION THROWN USING BATCH READS AND PARALLEL EXPRESSIONS
     * @bug 2612567 CR4298- NULLPOINTEREXCEPTION WHEN USING SUBQUERY AND BATCH READING IN 4.6
     * @bug 2612140 CR2973- BATCHATTRIBUTE QUERIES WILL FAIL WHEN THE INITIAL QUERY HAS A SUBQUERY
     * @bug 2720149 INVALID SQL WHEN USING BATCH READS AND MULTIPLE ANYOFS
     */
    public Expression cloneUsing(Expression newBase) {
        // 2612538 - the default size of Map (32) is appropriate
        Map alreadyDone = new IdentityHashMap();

        // cloneUsing is identical to cloning save that the primary builder 
        // will be replaced not with its clone but with newBase.
        // ExpressionBuilder.registerIn() will check for this newBase with
        // alreadyDone.get(alreadyDone);
        // copiedVersionFrom() must be called on the primary builder before
        // other builders.
        alreadyDone.put(alreadyDone, newBase);
        return copiedVersionFrom(alreadyDone);
    }

    /**
     * PUBLIC:
     * Function, returns the concatenation of the two string values.
     */
    public Expression concat(Object left) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Concat);
        return anOperator.expressionFor(this, left);
    }

    /**
     * PUBLIC:
     * Return an expression that performs a key word search.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: project.get("description").containsAllKeyWords("EclipseLink rdbms java")
     * </blockquote></pre>
     */
    public Expression containsAllKeyWords(String spaceSeparatedKeyWords) {
        StringTokenizer tokenizer = new StringTokenizer(spaceSeparatedKeyWords);
        Expression expression = null;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (expression == null) {
                expression = containsSubstringIgnoringCase(token);
            } else {
                expression = expression.and(containsSubstringIgnoringCase(token));
            }
        }

        if (expression == null) {
            return like("%");
        } else {
            return expression;
        }
    }

    /**
     * PUBLIC:
     * Return an expression that performs a key word search.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: project.get("description").containsAllKeyWords("EclipseLink rdbms java")
     * </blockquote></pre>
     */
    public Expression containsAnyKeyWords(String spaceSeparatedKeyWords) {
        StringTokenizer tokenizer = new StringTokenizer(spaceSeparatedKeyWords);
        Expression expression = null;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (expression == null) {
                expression = containsSubstringIgnoringCase(token);
            } else {
                expression = expression.or(containsSubstringIgnoringCase(token));
            }
        }

        if (expression == null) {
            return like("%");
        } else {
            return expression;
        }
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value contains the substring.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: employee.get("firstName").containsSubstring("Bob")
     *     Java: employee.getFirstName().indexOf("Bob") != -1
     *     SQL: F_NAME LIKE '%BOB%'
     * </blockquote></pre>
     */
    public Expression containsSubstring(String theValue) {
        return like("%" + theValue + "%");
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value contains the substring.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: employee.get("firstName").containsSubstring("Bob")
     *     Java: employee.getFirstName().indexOf("Bob") != -1
     *     SQL: F_NAME LIKE '%BOB%'
     * </blockquote></pre>
     */
    public Expression containsSubstring(Expression expression) {
        return like((value("%").concat(expression)).concat("%"));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value contains the substring, ignoring case.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: employee.get("firstName").containsSubstringIgnoringCase("Bob")
     *     Java: employee.getFirstName().toUpperCase().indexOf("BOB") != -1
     *     SQL: UPPER(F_NAME) LIKE '%BOB%'
     * </blockquote></pre>
     */
    public Expression containsSubstringIgnoringCase(String theValue) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().containsSubstring(theValue.toUpperCase());
        } else {
            return toLowerCase().containsSubstring(theValue.toLowerCase());
        }
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value contains the substring, ignoring case.
     * <p>Example:
     * <blockquote><pre>
     *     EclipseLink: employee.get("firstName").containsSubstringIgnoringCase("Bob")
     *     Java: employee.getFirstName().toUpperCase().indexOf("BOB") != -1
     *     SQL: UPPER(F_NAME) LIKE '%BOB%'
     * </blockquote></pre>
     */
    public Expression containsSubstringIgnoringCase(Expression expression) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().containsSubstring(expression.toUpperCase());
        } else {
            return toLowerCase().containsSubstring(expression.toLowerCase());
        }
    }

    /*
     * Modify this individual expression node to use outer joins wherever there are
     * equality operations between two field nodes.
     */
    protected void convertNodeToUseOuterJoin() {
    }

    /**
     * INTERNAL:
     * Modify this expression to use outer joins wherever there are
     * equality operations between two field nodes.
     */
    public Expression convertToUseOuterJoin() {
        ExpressionIterator iterator = new ExpressionIterator() {
            public void iterate(Expression each) {
                each.convertNodeToUseOuterJoin();
            }
        };
        iterator.iterateOn(this);
        return this;
    }

    /**
     * INTERNAL:
     */
    public Expression copiedVersionFrom(Map alreadyDone) {
        if (alreadyDone == null) {
            // For sub-selects no cloning is done.
            return this;
        }
        Expression existing = (Expression)alreadyDone.get(this);
        if (existing == null) {
            return registerIn(alreadyDone);
        } else {
            return existing;
        }
    }

    /**
     * PUBLIC:
     * This represents the aggregate function Average. Can be used only within Report Queries.
     */
    public Expression count() {
        return getFunction(ExpressionOperator.Count);
    }

    /**
     * INTERNAL:
     */
    public Expression create(Expression base, Object singleArgument, ExpressionOperator anOperator) {
        // This is a replacement for real class methods in Java. Instead of returning a new instance we create it, then
        // mutate it using this method.
        return this;
    }

    /**
     * INTERNAL:
     */
    public Expression createWithBaseLast(Expression base, Object singleArgument, ExpressionOperator anOperator) {
        // This is a replacement for real class methods in Java. Instead of returning a new instance we create it, then
        // mutate it using this method.
        return this;
    }

    /**
     * INTERNAL:
     */
    public Expression create(Expression base, List arguments, ExpressionOperator anOperator) {
        // This is a replacement for real class methods in Java. Instead of returning a new instance we create it, then
        // mutate it using this method.
        return this;
    }

    /**
     * PUBLIC:
     * This gives access to the current timestamp on the database through expression.
     * Please note, this method is added for consistency and returns the same
     * result as currentDate. 
     */
    public Expression currentTimeStamp() {
        return currentDate();
    }
    
    /**
     * PUBLIC:
     * This gives access to the current date on the database through expression.
     */
    public Expression currentDate() {
        return getFunction(ExpressionOperator.Today);
    }

   /**
    * PUBLIC:
    * This gives access to the current date only on the database through expression.
    * Note the difference between currentDate() and this method. This method does
    * not return the time portion of current date where as currentDate() does.
    */
   public Expression currentDateDate() {
       return getFunction(ExpressionOperator.CurrentDate);
   }

   /**
    * PUBLIC:
    * This gives access to the current time only on the database through expression.
    * Note the difference between currentDate() and this method. This method does
    * not return the date portion where as currentDate() does.
    */
   public Expression currentTime() {
       return getFunction(ExpressionOperator.CurrentTime);
   }

    /**
     * PUBLIC:
     * Function, Return the difference between the queried part of a date(i.e. years, days etc.)
     * and same part of the given date. The equivalent of the Sybase function DateDiff
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").dateDifference("year", new Date(System.currentTimeMillis()))
     * Java: NA
     * SQL: DATEADD(date, 2, GETDATE)
     * </blockquote></pre> *
     */
    public Expression dateDifference(String datePart, java.util.Date date) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.DateDifference);
        FunctionExpression expression = new FunctionExpression();
        expression.setBaseExpression(this);
        expression.addChild(Expression.fromLiteral(datePart, this));
        expression.addChild(Expression.from(date, this));
        expression.addChild(this);
        expression.setOperator(anOperator);
        return expression;
    }

    /**
     * PUBLIC:
     * Function, Return the difference between the queried part of a date(i.e. years, days etc.)
     * and same part of the given date. The equivalent of the Sybase function DateDiff
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").dateDifference("year", new Date(System.currentTimeMillis()))
     * Java: NA
     * SQL: DATEADD(date, 2, GETDATE)
     * </blockquote></pre> *
     */
    public Expression dateDifference(String datePart, Expression comparisonExpression) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.DateDifference);
        FunctionExpression expression = new FunctionExpression();
        expression.setBaseExpression(this);
        expression.addChild(Expression.fromLiteral(datePart, this));
        expression.addChild(comparisonExpression);
        expression.addChild(this);
        expression.setOperator(anOperator);
        return expression;
    }

    /**
     * PUBLIC:
     * return a string that represents the given part of a date. The equivalent
     * of the Sybase DATENAME function
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").dateName("year")
     * Java: new String(date.getYear())
     * SQL: DATENAME(date, year)
     * </blockquote></pre> *
     */
    public Expression dateName(String datePart) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.DateName);
        FunctionExpression expression = new FunctionExpression();
        expression.setBaseExpression(this);
        expression.addChild(Expression.fromLiteral(datePart, this));
        expression.addChild(this);
        expression.setOperator(anOperator);
        return expression;
    }

    /**
     * PUBLIC:
     * Function return an integer which represents the requested
     * part of the date.  Equivalent of the Sybase function DATEPART
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("date").datePart("year")
     * Java: date.getYear()
     * SQL: DATEPART(date, year)
     * </blockquote></pre> *  */
    public Expression datePart(String datePart) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.DatePart);
        FunctionExpression expression = new FunctionExpression();
        expression.setBaseExpression(this);
        expression.addChild(Expression.fromLiteral(datePart, this));
        expression.addChild(this);
        expression.setOperator(anOperator);
        return expression;
    }

    /**
     * PUBLIC:
     * Function, returns the date converted to the string value in the default database format.
     */
    public Expression dateToString() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.DateToString);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function Convert values returned by the query to values given in the decodeableItems Map.
     * The equivalent of the Oracle DECODE function.
     * Note: This will only work on databases that support Decode with the syntax below.
     * <p>Example:
     * <blockquote><pre>
     * Map decodeTable = new HashMap();
     * decodeTable.put("Robert", "Bob");
     * decodeTable.put("Susan", "Sue");
     *
     * EclipseLink: employee.get("name").Decode(decodeTable, "No-Nickname")
     * Java: NA
     * SQL: DECODE(name, "Robert", "Bob", "Susan", "Sue", "No-Nickname")
     * </blockquote></pre>
     * @param decodeableItems java.util.Map
     *   a Map containing the items to be decoded.  Keys represent
     * the items to match coming from the query.  Values represent what
     * a key will be changed to.
     * @param defaultItem
     *  the default value that will be used if none of the keys in the
     * Map match
     **/
    public Expression decode(Map decodeableItems, String defaultItem) {

        /**
         * decode works differently than most of the functionality in the expression framework.
         * It takes a variable number of arguments and as a result, the printed strings for
         * a decode call have to be built when the number of arguments are known.
         * As a result, we do not look up decode in the ExpressionOperator.  Instead we build
         * the whole operator here.  The side effect of this is that decode will not thrown
         * an invalid operator exception for any platform.  (Even the ones that do not support
         * decode)
         */
        ExpressionOperator anOperator = new ExpressionOperator();
        anOperator.setSelector(ExpressionOperator.Decode);
        anOperator.setName("DECODE");
        anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        anOperator.setType(ExpressionOperator.FunctionOperator);
        anOperator.bePrefix();

        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(decodeableItems.size() + 1);
        v.add("DECODE(");
        for (int i = 0; i < ((decodeableItems.size() * 2) + 1); i++) {
            v.add(", ");
        }
        v.add(")");
        anOperator.printsAs(v);

        FunctionExpression expression = new FunctionExpression();
        expression.setBaseExpression(this);
        expression.addChild(this);
        Iterator iterator = decodeableItems.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            expression.addChild(Expression.from(key, this));
            expression.addChild(Expression.from(decodeableItems.get(key), this));
        }
        expression.addChild(Expression.from(defaultItem, this));
        expression.setOperator(anOperator);
        return expression;
    }

    /**
     * PUBLIC:
     * This can only be used within an ordering expression.
     * It will order the result descending.
     * <p>Example:
     * <blockquote><pre>
     * readAllQuery.addOrderBy(expBuilder.get("address").get("city").descending())
     * </blockquote></pre>
     */
    public Expression descending() {
        return getFunction(ExpressionOperator.Descending);
    }

    /**
     * INTERNAL:
     * Used in debug printing of this node.
     */
    public String descriptionOfNodeType() {
        return "Expression";
    }
    
    /**
     * PUBLIC:
     * Function return a value which indicates how much difference there is
     * between two expressions.  Equivalent of the Sybase DIFFERENCE function
     * <p>Example:
     * <blockquote><pre>
     * EclipseLink: employee.get("name").difference("Frank")
     * SQL: DIFFERENCE(name, 'Frank')
     * </blockquote></pre> *  */
    public Expression difference(String expression) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Difference);
        return anOperator.expressionFor(this, expression);
    }

    /**
     * PUBLIC:
     * Function, This represents the distinct option inside an aggregate function. Can be used only within Report Queries.
     */
    public Expression distinct() {
        return getFunction(ExpressionOperator.Distinct);
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying.
     * By default throw an exception as all valid root expressions must override.
     * If the expression in not able to determine if the object conform throw a not supported exception.
     */
    public boolean doesConform(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy) throws QueryException {
        return doesConform(object, session, translationRow, valueHolderPolicy, false);
    }

    /**
     * INTERNAL:
     * New parameter added to doesConform for feature 2612601
     * @param objectIsUnregistered true if object possibly not a clone, but is being
     * conformed against the unit of work cache; if object is not in the UOW cache
     * but some of its attributes are, use the registered versions of
     * object's attributes for the purposes of this method.
     */
    public boolean doesConform(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean objectIsUnregistered) throws QueryException {
        throw QueryException.cannotConformExpression();
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     * Two expressions should be considered equal if they have the same "parameterized" SQL.
     * This must be over written by each subclass.
     */
    public boolean equals(Object expression) {
        return (this == expression) || ((expression != null) && getClass().equals(expression.getClass()) && (hashCode() == expression.hashCode()));
    }

    /**
     * INTERNAL:
     * Return a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     * Two expressions should have the same hashCode if they have the same "parameterized" SQL.
     * This should be over written by each subclass to provide a consistent value.
     */
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
        }
        return this.hashCode;
    }
    
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     * Two expressions should have the same hashCode if they have the same "parameterized" SQL.
     * This should be over written by each subclass to provide a consistent value.
     */
    public int computeHashCode() {
        return 32;
    }
    
    public Expression equal(byte theValue) {
        return equal(Byte.valueOf(theValue));
    }

    public Expression equal(char theChar) {
        return equal(Character.valueOf(theChar));
    }

    public Expression equal(double theValue) {
        return equal(Double.valueOf(theValue));
    }

    public Expression equal(float theValue) {
        return equal(Float.valueOf(theValue));
    }

    public Expression equal(int theValue) {
        return equal(Integer.valueOf(theValue));
    }

    public Expression equal(long theValue) {
        return equal(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receiver's value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").equal("Bob")
     *     Java: employee.getFirstName().equals("Bob")
     *     SQL: F_NAME = 'Bob'
     * </blockquote></pre>
     */
    public Expression equal(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Equal);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * Returns an expression that compares if the receiver's value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     * <p>Since OracleAS EclipseLink 10<i>g</i> (9.0.4) if <code>this</code> is an <code>ExpressionBuilder</code> and <code>theValue</code>
     * is not used elsewhere, both will be translated to the same table.  This can
     * generate SQL with one less join for most exists subqueries.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("manager").equal(employee)
     *     Java: employee.getManager().equals(employee)
     *     SQL (optimized): EMP_ID = MANAGER_ID
     *     SQL (unoptimized): t0.MANAGER_ID = t1.EMP_ID AND t0.EMP_ID = t1.EMP_ID
     * </blockquote></pre>
     * @see #equal(Object)
     */
    public Expression equal(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Equal);
        return anOperator.expressionFor(this, theValue);

    }

    public Expression equal(short theValue) {
        return equal(Short.valueOf(theValue));
    }

    public Expression equal(boolean theBoolean) {
        return equal(Boolean.valueOf(theBoolean));
    }

    /**
     * INTERNAL:
     * Return an expression representing an outer join comparison
     */

    // cr3546
    public Expression equalOuterJoin(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.EqualOuterJoin);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * INTERNAL:
     * Return an expression representing an outer join comparison
     */
    public Expression equalOuterJoin(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.EqualOuterJoin);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receiver's value is equal to the other value, ignoring case.
     * This is equivalent to the Java "equalsIgnoreCase" method.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").equalsIgnoreCase("Bob")
     *     Java: employee.getFirstName().equalsIgnoreCase("Bob")
     *     SQL: UPPER(F_NAME) = 'BOB'
     * </blockquote></pre>
     */
    public Expression equalsIgnoreCase(String theValue) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().equal(theValue.toUpperCase());
        } else {
            return toLowerCase().equal(theValue.toLowerCase());
        }
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receiver's value is equal to the other value, ignoring case.
     * This is equivalent to the Java "equalsIgnoreCase" method.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").equalsIgnoreCase("Bob")
     *     Java: employee.getFirstName().equalsIgnoreCase("Bob")
     *     SQL: UPPER(F_NAME) = 'BOB'
     * </blockquote></pre>
     */
    public Expression equalsIgnoreCase(Expression theValue) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().equal(theValue.toUpperCase());
        } else {
            return toLowerCase().equal(theValue.toLowerCase());
        }
    }

    /**
     * PUBLIC:
     * Return a sub query expression.
     * A sub query using a report query to define a subselect within another queries expression or select's where clause.
     * The sub query (the report query) will use its own expression builder be can reference expressions from the base expression builder.
     * <p>Example:
     * <pre><blockquote>
     * ExpressionBuilder builder = new ExpressionBuilder();
     * ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
     * subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("name").equal(builder.get("name")));
     * builder.exists(subQuery);
     * </blockquote></pre>
     */
    public Expression exists(ReportQuery subQuery) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Exists);
        return anOperator.expressionFor(subQuery(subQuery));
    }

    /**
     * INTERNAL:
     * Extract the primary key from the expression into the row.
     * Ensure that the query is querying the exact primary key.
     * Return false if not on the primary key.
     */
    public boolean extractPrimaryKeyValues(boolean requireExactMatch, ClassDescriptor descriptor, AbstractRecord primaryKeyRow, AbstractRecord translationRow) {
        return extractValues(true, requireExactMatch, descriptor, primaryKeyRow, translationRow);
    }

    /**
     * INTERNAL:
     * Extract the primary key from the expression into the row.
     * Ensure that the query is querying the exact primary key.
     * Return false if not on the primary key.
     */
    public boolean extractValues(boolean primaryKeyOnly, boolean requireExactMatch, ClassDescriptor descriptor, AbstractRecord primaryKeyRow, AbstractRecord translationRow) {
        return false;
    }

    /**
     * INTERNAL:
     * Return if the expression is not a valid primary key expression and add all primary key fields to the set.
     */
    public boolean extractFields(boolean requireExactMatch, boolean primaryKey, ClassDescriptor descriptor, List<DatabaseField> searchFields, Set<DatabaseField> foundFields) {
        return false;
    }

    /**
     * INTERNAL:
     * Create an expression node.
     */
    public static Expression from(Object value, Expression base) {
        //CR#... null value used to return null, must build a null constant expression.
        if (value instanceof Expression) {
            Expression exp = (Expression)value;
            if (exp.isValueExpression() || base.isExpressionBuilder()) {
                exp.setLocalBase(base);
            } else {
                // We don't know which side of the relationship cares about the other one, so notify both
                // However for 3107049 value.equal(value) would cause infinite loop if did that.
                base.setLocalBase(exp);
            }
            return exp;
        }
        if (value instanceof ReportQuery) {
            Expression exp = base.subQuery((ReportQuery)value);
            exp.setLocalBase(base);// We don't know which side of the relationship cares about the other one, so notify both
            base.setLocalBase(exp);
            return exp;
        }
        return fromConstant(value, base);

    }

    /**
     * INTERNAL:
     * Create an expression node.
     */
    public static Expression fromConstant(Object value, Expression base) {
        return new ConstantExpression(value, base);
    }

    /**
     * INTERNAL:
     * Create an expression node.
     */
    public static Expression fromLiteral(String value, Expression base) {
        return new LiteralExpression(value, base);
    }
    
    /**
     * PUBLIC:
     * Return an expression that wraps the attribute or query key name.
     * This method is used to construct user-defined queries containing joins.
     * <p>Example:
     * <pre><blockquote>
     *  builder.get("address").get("city").equal("Ottawa");
     * </blockquote></pre>
     */
    public Expression get(String attributeName) {
        throw new UnsupportedOperationException("get");
    }

    /**
     * ADVANCED:
     * Return an expression that wraps the attribute or query key name.
     * This is only applicable to 1:1 relationships, and allows the target of
     * the relationship to be null if there is no corresponding relationship in the database.
     * Implemented via an outer join in the database.
     * <p>Example:
     * <pre><blockquote>
     *  builder.getAllowingNull("address").get("city").equal("Ottawa");
     * </blockquote></pre>
     */
    public Expression getAllowingNull(String attributeName) {
        throw new UnsupportedOperationException("getAllowingNull");
    }

    /**
     * Answers the past time the expression is explicitly as of.
     * @return An immutable object representation of the past time.
     * <code>null</code> if no clause set, <code>AsOfClause.NO_CLAUSE</code> if
     * clause explicitly set to <code>null</code>.
     * @see #asOf(org.eclipse.persistence.history.AsOfClause)
     * @see #hasAsOfClause()
     */
    public AsOfClause getAsOfClause() {
        return null;
    }

    /**
     * INTERNAL:
     * For Flashback: If this expression is not already as of some timestamp
     * gets the clause from the base expression.  Allows a clause to be set
     * only on the builder and then propogated during normalize.
     */
    public AsOfClause getAsOfClauseRecursively() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root)
     */
    public abstract ExpressionBuilder getBuilder();

    /**
     * INTERNAL:
     * If there are any fields associated with this expression, return them
     */
    public DatabaseField getClonedField() {
        return null;
    }

    /**
     * ADVANCED:
     * Return an expression representing a field in a data-level query.
     * This is used internally in EclipseLink, or to construct queries involving
     * fields and/or tables that are not mapped.
     * <p> Example:
     * <blockquote><pre>
     *  builder.getField("ADDR_ID").greaterThan(100);
     *  builder.getTable("PROJ_EMP").getField("TYPE").equal("S");
     * </blockquote></pre>
     */
    public Expression getField(String fieldName) {
        throw QueryException.illegalUseOfGetField(fieldName);
    }

    /**
     * ADVANCED: Return an expression representing a field in a data-level query.
     * This is used internally in EclipseLink, or to construct queries involving
     * fields and/or tables that are not mapped.
     * <p> Example:
     * <blockquote><pre>
     *  builder.getField(aField).greaterThan(100);
     * </blockquote></pre>
     */
    public Expression getField(DatabaseField field) {
        throw QueryException.illegalUseOfGetField(field);
    }

    /**
     * INTERNAL:
     */
    public Vector getFields() {
        return org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
    }

    /**
     * INTERNAL:
     * Transform the object-level value into a database-level value
     */
    public Object getFieldValue(Object objectValue, AbstractSession session) {
        // Enums default to their ordinal
        if (objectValue instanceof Enum){
            return ((Enum)objectValue).ordinal();
        }
        return objectValue;

    }

    /**
     * ADVANCED:
     * Defines a join between the two objects based on the specified ON clause.
     * This can be used to define a join condition on two unrelated objects,
     * or to qualify a relationship join with additional criteria.
     * <p> Example:
     * <pre><blockquote>
     *  Expression address = employee.getAllowingNull("address");
     *  employee.join(address, address.get("city").equal("Ottawa"));
     *  query.addNonFetchJoin(address);
     * </blockquote></pre>
     */
    public Expression join(Expression target, Expression onClause) {
        throw new UnsupportedOperationException("join");
    }

    /**
     * ADVANCED:
     * Defines an outer join between the two objects based on the specified ON clause.
     * This can be used to define a join condition on two unrelated objects,
     * or to qualify a relationship join with additional criteria.
     * <p> Example:
     * <pre><blockquote>
     *  Expression address = employee.getAllowingNull("address");
     *  employee.leftJoin(address, address.get("city").equal("Ottawa"));
     *  query.addNonFetchJoin(address);
     * </blockquote></pre>
     */
    public Expression leftJoin(Expression target, Expression onClause) {
        throw new UnsupportedOperationException("leftJoin");
    }
    
    /**
     * ADVANCED:
     * This can be used for accessing user defined functions.
     * The operator must be defined in ExpressionOperator to be able to reference it.
     * @see ExpressionOperator
     * <p> Example:
     * <pre><blockquote>
     *  builder.get("name").getFunction(MyFunctions.FOO_BAR).greaterThan(100);
     * </blockquote></pre>
     */
    public Expression getFunction(int selector) {
        ExpressionOperator anOperator = getOperator(selector);
        return anOperator.expressionFor(this);
    }

    /**
     * ADVANCED:
     * This can be used for accessing user defined functions that have arguments.
     * The operator must be defined in ExpressionOperator to be able to reference it.
     * @see ExpressionOperator
     * <p> Example:
     * <pre><blockquote>
     *    List arguments = new ArrayList();
     *    arguments.add("blee");
     *  builder.get("name").getFunction(MyFunctions.FOO_BAR, arguments).greaterThan(100);
     * </blockquote></pre>
     */
    public Expression getFunction(int selector, List arguments) {
        ExpressionOperator anOperator = getOperator(selector);
        return anOperator.expressionForArguments(this, arguments);
    }

    /**
     * ADVANCED:
     * This can be used for accessing user defined operators that have arguments.
     * The operator must be defined in ExpressionOperator to be able to reference it.
     * @see ExpressionOperator
     * <p> Example:
     * <pre><blockquote>
     *  List arguments = new ArrayList();
     *  arguments.add("blee");
     *  builder.get("name").operator("FOO_BAR", arguments).greaterThan(100);
     * </blockquote></pre>
     */
    public Expression operator(String name, List arguments) {
        Integer selector = ExpressionOperator.getPlatformOperatorSelectors().get(name);
        if (selector == null) {
            return getFunctionWithArguments(name, arguments);
        }
        return getFunction(selector, arguments);
    }

    /**
     * ADVANCED:
     * This can be used for accessing user defined functions that have arguments.
     * The operator must be defined in ExpressionOperator to be able to reference it.
     * @see ExpressionOperator
     * <p> Example:
     * <pre><blockquote>
     *    List arguments = new ArrayList();
     *    arguments.add("blee");
     *  builder.get("name").getFunction(MyFunctions.FOO_BAR, arguments).greaterThan(100);
     * </blockquote></pre>
     */
    @Deprecated
    public Expression getFunction(int selector, Vector arguments) {
        return getFunction(selector, (List)arguments);
    }

    /**
     * ADVANCED:
     * Return a user defined function accepting the argument.
     * The function is assumed to be a normal prefix function and will print like, UPPER(base).
     * <p> Example:
     * <pre><blockquote>
     *  builder.get("firstName").getFunction("UPPER");
     * </blockquote></pre>
     */
    public Expression getFunction(String functionName) {
        ExpressionOperator anOperator = ExpressionOperator.simpleFunction(0, functionName);
        return anOperator.expressionFor(this);
    }

    /**
     * ADVANCED:
     * Return a user defined function accepting the argument.
     * The function is assumed to be a normal prefix function and will print like, CONCAT(base, argument).
     */
    public Expression getFunction(String functionName, Object argument) {
        ExpressionOperator anOperator = ExpressionOperator.simpleTwoArgumentFunction(0, functionName);
        return anOperator.expressionFor(this, argument);
    }

    /**
     * ADVANCED:
     * Return a user defined function accepting all of the arguments.
     * The function is assumed to be a normal prefix function like, CONCAT(base, value1, value2, value3, ...).
     */
    @Deprecated
    public Expression getFunctionWithArguments(String functionName, Vector arguments) {
        return getFunctionWithArguments(functionName, (List)arguments);
    }

    /**
     * ADVANCED:
     * Return a user defined function accepting all of the arguments.
     * The function is assumed to be a normal prefix function like, CONCAT(base, value1, value2, value3, ...).
     */
    public Expression getFunctionWithArguments(String functionName, List arguments) {
        ExpressionOperator anOperator = new ExpressionOperator();
        anOperator.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(arguments.size());
        v.add(functionName + "(");
        for (int index = 0; index < arguments.size(); index++) {
            v.add(", ");
        }
        v.add(")");
        anOperator.printsAs(v);
        anOperator.bePrefix();
        anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);

        return anOperator.expressionForArguments(this, arguments);
    }

    /**
     * ADVANCED:
     * Parse the SQL for parameter and return a custom function expression
     * using a custom operator that will print itself as the SQL.
     * Arguments are passed using '?', and must match the number of arguments.
     */
    public Expression sql(String sql, List arguments) {
        ExpressionOperator anOperator = new ExpressionOperator();
        anOperator.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(arguments.size());
        int start = 0;
        int index = sql.indexOf('?');
        while (index != -1) {
            v.add(sql.substring(start, index));
            start = index + 1;
            index = sql.indexOf('?', start);
        }
        if (start <= sql.length()) {
            v.add(sql.substring(start, sql.length()));
        }
        anOperator.printsAs(v);
        anOperator.bePrefix();
        anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);

        return anOperator.expressionForArguments(this, arguments);
    }
    
    /**
     * PUBLIC:
     * Return an expression that wraps the inheritance type field in an expression.
     * <p>Example:
     * <pre><blockquote>
     *  builder.getClassForInheritance().equal(SmallProject.class);
     *  builder.anyOf("projects").getClassForInheritance().equal(builder.getParameter("projectClass"));
     * </blockquote></pre>
     */
    public Expression type() {
        //Only valid on an ObjectExpression
        return null;
    }

    /**
     * INTERNAL:
     */
    public String getName() {
        return "";
    }

    /**
     * INTERNAL:
     * Most expression have operators, so this is just a convenience method.
     */
    public ExpressionOperator getOperator() {
        return null;
    }

    /**
     * INTERNAL:
     * Create a new expression tree with the named operator. Part of the implementation of user-level "get"
     */
    public ExpressionOperator getOperator(int selector) {
        ExpressionOperator result = ExpressionOperator.getOperator(Integer.valueOf(selector));
        if (result != null) {
            return result;
        }

        // Make a temporary operator which we expect the platform
        // to supply later.
        result = new ExpressionOperator();
        result.setSelector(selector);
        result.setNodeClass(ClassConstants.FunctionExpression_Class);
        return result;
    }

    /**
     * INTERNAL:
     * Return the tables that this node owns for purposes of table aliasing.
     */
    public List<DatabaseTable> getOwnedTables() {
        return null;
    }

    /**
     * INTERNAL:
     * Return an expression representing a parameter with the given name and type
     */
    public Expression getParameter(String parameterName, Object type) {
        return new ParameterExpression(parameterName, getBuilder(), type);

    }

    /**
     * ADVANCED:
     * Return an expression representing a parameter with the given name.
     */
    public Expression getParameter(String parameterName) {
        return new ParameterExpression(parameterName, getBuilder(), null);

    }

    /**
     * ADVANCED:
     * Return an expression representing a parameter with the given name.
     */
    public Expression getParameter(DatabaseField field) {
        return new ParameterExpression(field, getBuilder());

    }
    
    /**
     * ADVANCED:
     * Return an expression representing a property with the given name.
     */
    public Expression getProperty(DatabaseField field) {
        ParameterExpression paramExpression = new ParameterExpression(field, this);
        paramExpression.setIsProperty(true);
        return paramExpression;
    }

    /**
     * INTERNAL:
     */
    public AbstractSession getSession() {
        return getBuilder().getSession();
    }

    /**
     * ADVANCED: Return an expression representing a table in a data-level query.
     * This is used internally in EclipseLink, or to construct queries involving
     * fields and/or tables that are not mapped.
     * <p> Example:
     * <pre><blockquote>
     *  builder.getTable("PROJ_EMP").getField("TYPE").equal("S");
     * </blockquote></pre>
     */
    public Expression getTable(String tableName) {
        DatabaseTable table = new DatabaseTable(tableName);
        return getTable(table);
    }

    /**
     * ADVANCED: Return an expression representing a table in a data-level query.
     * This is used internally in EclipseLink, or to construct queries involving
     * fields and/or tables that are not mapped.
     * <p> Example:
     * <pre><blockquote>
     *  builder.getTable(linkTable).getField("TYPE").equal("S");
     * </blockquote></pre>
     */
    public Expression getTable(DatabaseTable table) {
        throw QueryException.illegalUseOfGetTable(table);
    }

    /**
     * ADVANCED: Return an expression representing a sub-select in the from clause.
     * <p> Example:
     * <pre><blockquote>
     *  builder.getAlias(builder.subQuery(reportQuery)).get("type").equal("S");
     * </blockquote></pre>
     */
    public Expression getAlias(Expression subSelect) {
        throw QueryException.illegalUseOfGetTable(subSelect);
    }

    /**
     * INTERNAL:
     * Return the aliases used. By default, return null, since we don't have tables.
     */
    public TableAliasLookup getTableAliases() {
        return null;

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(byte theValue) {
        return greaterThan(Byte.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(char theChar) {
        return greaterThan(Character.valueOf(theChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(double theValue) {
        return greaterThan(Double.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(float theValue) {
        return greaterThan(Float.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(int theValue) {
        return greaterThan(Integer.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(long theValue) {
        return greaterThan(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receiver's value is greater than the other value.
     * This is equivalent to the SQL ">" operator.
     */
    public Expression greaterThan(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GreaterThan);
        return anOperator.expressionFor(this, theValue);
    }

    public Expression greaterThan(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GreaterThan);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(short theValue) {
        return greaterThan(Short.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is equal to the other value.
     * This is equivalent to the SQL "=" operator and Java "equals" method.
     */
    public Expression greaterThan(boolean theBoolean) {
        return greaterThan(Boolean.valueOf(theBoolean));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(byte theValue) {
        return greaterThanEqual(Byte.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(char theChar) {
        return greaterThanEqual(Character.valueOf(theChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(double theValue) {
        return greaterThanEqual(Double.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(float theValue) {
        return greaterThanEqual(Float.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(int theValue) {
        return greaterThanEqual(Integer.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(long theValue) {
        return greaterThanEqual(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GreaterThanEqual);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GreaterThanEqual);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(short theValue) {
        return greaterThanEqual(Short.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is greater and equal to the other value.
     * This is equivalent to the SQL ">=" operator .
     */
    public Expression greaterThanEqual(boolean theBoolean) {
        return greaterThanEqual(Boolean.valueOf(theBoolean));
    }

    /**
     * ADVANCED:
     * Answers true if <code>this</code> is to be queried as of a past time.
     * @return false from <code>asOf(null); hasAsOfClause()</code>.
     * @see #getAsOfClause
     */
    public boolean hasAsOfClause() {
        return false;
    }

    /**
     * INTERNAL:
     * Answers if the database tables associated with this expression have been
     * aliased.  This insures the same tables are not aliased twice.
     */
    public boolean hasBeenAliased() {
        return false;
    }

    /**
     * PUBLIC:
     * Function, returns binary array value for the hex string.
     */
    public Expression hexToRaw() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.HexToRaw);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function  return a specific value if item returned from the
     * query is null.  Equivalent of the oracle NVL function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").ifNull("no-name")
     * Java: NA
     * SQL: NVL(name, 'no-name')
     * </blockquote></pre>
     */
    public Expression ifNull(Object nullValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Nvl);
        return anOperator.expressionFor(this, nullValue);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(byte[] theBytes) {
        List values = new ArrayList(theBytes.length);

        for (int index = 0; index < theBytes.length; index++) {
            values.add(Byte.valueOf(theBytes[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(char[] theChars) {
        List values = new ArrayList(theChars.length);

        for (int index = 0; index < theChars.length; index++) {
            values.add(Character.valueOf(theChars[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(double[] theDoubles) {
        List values = new ArrayList(theDoubles.length);

        for (int index = 0; index < theDoubles.length; index++) {
            values.add(Double.valueOf(theDoubles[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(float[] theFloats) {
        List values = new ArrayList(theFloats.length);

        for (int index = 0; index < theFloats.length; index++) {
            values.add(Float.valueOf(theFloats[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(int[] theInts) {
        List values = new ArrayList(theInts.length);

        for (int index = 0; index < theInts.length; index++) {
            values.add(Integer.valueOf(theInts[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(long[] theLongs) {
        List values = new ArrayList(theLongs.length);

        for (int index = 0; index < theLongs.length; index++) {
            values.add(Long.valueOf(theLongs[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(Object[] theObjects) {
        List values = new ArrayList(theObjects.length);

        for (int index = 0; index < theObjects.length; index++) {
            values.add(theObjects[index]);
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(short[] theShorts) {
        List values = new ArrayList(theShorts.length);

        for (int index = 0; index < theShorts.length; index++) {
            values.add(Short.valueOf(theShorts[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression in(boolean[] theBooleans) {
        List values = new ArrayList(theBooleans.length);

        for (int index = 0; index < theBooleans.length; index++) {
            values.add(Boolean.valueOf(theBooleans[index]));
        }

        return in(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     */
    public Expression in(Collection theObjects) {
        return in(new CollectionExpression(theObjects, this));
    }

    public Expression in(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.In);
        return anOperator.expressionFor(this, arguments);
    }

    public Expression in(ReportQuery subQuery) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.InSubQuery);
        return anOperator.expressionFor(this, subQuery);
    }

    /*
     * PUBLIC:
     * Index method could be applied to QueryKeyExpression corresponding to CollectionMapping
     * that has non-null listOrderField (the field holding the index values).
     * <p>Example:
     * <pre><blockquote>
     *    ReportQuery query = new ReportQuery();
     *    query.setReferenceClass(Employee.class);
     *    ExpressionBuilder builder = query.getExpressionBuilder();
     *    Expression firstNameJohn = builder.get("firstName").equal("John");
     *    Expression anyOfProjects = builder.anyOf("projects");
     *    Expression exp = firstNameJohn.and(anyOfProjects.index().between(2, 4));
     *    query.setSelectionCriteria(exp);
     *    query.addAttribute("projects", anyOfProjects);
     *       
     *    SELECT DISTINCT t0.PROJ_ID, t0.PROJ_TYPE, t0.DESCRIP, t0.PROJ_NAME, t0.LEADER_ID, t0.VERSION, t1.PROJ_ID, t1.BUDGET, t1.MILESTONE 
     *    FROM OL_PROJ_EMP t4, OL_SALARY t3, OL_EMPLOYEE t2, OL_LPROJECT t1, OL_PROJECT t0 
     *    WHERE ((((t2.F_NAME = 'John') AND (t4.PROJ_ORDER BETWEEN 2 AND 4)) AND (t3.OWNER_EMP_ID = t2.EMP_ID)) AND
     *    (((t4.EMP_ID = t2.EMP_ID) AND (t0.PROJ_ID = t4.PROJ_ID)) AND (t1.PROJ_ID (+) = t0.PROJ_ID)))
     * </blockquote></pre>
     */
    public Expression index() {
        throw QueryException.indexRequiresQueryKeyExpression(this);
    }
    
    /**
     * PUBLIC:
     * Function, returns the integer index of the substring within the source string.
     */
    public Expression indexOf(Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Instring);
        return anOperator.expressionFor(this, substring);
    }
    
    /**
     * INTERNAL:
     */
    public boolean isClassTypeExpression(){
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isCompoundExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isConstantExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isDataExpression() {
        return false;
    }

    /**
     * PUBLIC: A logical expression for the collection <code>attributeName</code>
     * being empty.
     * Equivalent to <code>size(attributeName).equal(0)</code>
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.isEmpty("phoneNumbers")
     *     Java: employee.getPhoneNumbers().size() == 0
     *     SQL: SELECT ... FROM EMP t0 WHERE (
     *      (SELECT COUNT(*) FROM PHONE t1 WHERE (t0.EMP_ID = t1.EMP_ID)) = 0)
     * </blockquote></pre>
     * This is a case where a fast operation in java does not translate to an
     * equally fast operation in SQL, requiring a correlated subselect.
     * @see #size(java.lang.String)
     */
    public Expression isEmpty(String attributeName) {
        return size(attributeName).equal(0);
    }

    /**
     * INTERNAL:
     */
    public boolean isExpressionBuilder() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isFieldExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isFunctionExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isLiteralExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isLogicalExpression() {
        return false;
    }

    /**
     * PUBLIC:
     * Compare to null.
     */
    public Expression isNull() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.IsNull);
        return anOperator.expressionFor(this);
    }

    /**
     * INTERNAL:
     */
    public boolean isObjectExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isParameterExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isQueryKeyExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationExpression() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isSubSelectExpression() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isTableExpression() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isMapEntryExpression(){
        return false;
    }

    /**
     * INTERNAL:
     * Subclasses implement (isParameterExpression() || isConstantExpression())
     */
    public boolean isValueExpression() {
        return false;
    }

    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    public void iterateOn(ExpressionIterator iterator) {
        iterator.iterate(this);
    }

    /**
     * PUBLIC:
     * Function, returns the date with the last date in the months of this source date.
     */
    public Expression lastDay() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LastDay);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns the string padded with the substring to the size.
     */
    public Expression leftPad(int size, Object substring) {
        return leftPad(Integer.valueOf(size), substring);
    }

    /**
     * PUBLIC:
     * Function, returns the string padded with the substring to the size.
     */
    public Expression leftPad(Object size, Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LeftPad);
        List args = new ArrayList(2);
        args.add(size);
        args.add(substring);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, returns the string left trimmed for white space.
     */
    public Expression leftTrim() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LeftTrim);
        return anOperator.expressionFor(this);
    }
    
    /**
     * PUBLIC:
     * Function, returns the string with the substring trimed from the left.
     */
    public Expression leftTrim(Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LeftTrim2);
        return anOperator.expressionFor(this, substring);
    }

    /**
     * PUBLIC:
     * Function, returns the size of the string.
     */
    public Expression length() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Length);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(byte theValue) {
        return lessThan(Byte.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(char theChar) {
        return lessThan(Character.valueOf(theChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(double theValue) {
        return lessThan(Double.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(float theValue) {
        return lessThan(Float.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(int theValue) {
        return lessThan(Integer.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(long theValue) {
        return lessThan(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LessThan);
        return anOperator.expressionFor(this, theValue);
    }

    public Expression lessThan(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LessThan);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(short theValue) {
        return lessThan(Short.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than the other value.
     * This is equivalent to the SQL "<" operator.
     */
    public Expression lessThan(boolean theBoolean) {
        return lessThan(Boolean.valueOf(theBoolean));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(byte theValue) {
        return lessThanEqual(Byte.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(char theChar) {
        return lessThanEqual(Character.valueOf(theChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(double theValue) {
        return lessThanEqual(Double.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(float theValue) {
        return lessThanEqual(Float.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(int theValue) {
        return lessThanEqual(Integer.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(long theValue) {
        return lessThanEqual(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LessThanEqual);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LessThanEqual);
        return anOperator.expressionFor(this, theValue);

    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(short theValue) {
        return lessThanEqual(Short.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is less than and equal to the other value.
     * This is equivalent to the SQL "<=" operator.
     */
    public Expression lessThanEqual(boolean theBoolean) {
        return lessThanEqual(Boolean.valueOf(theBoolean));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like other value.
     * This is equivalent to the SQL "LIKE" operator that except wildcards.
     * The character "%" means any sequence of characters and the character "_" mean any character.
     * i.e. "B%" == "Bob", "B_B" == "BOB"
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").like("B%")
     *     Java: NA
     *     SQL: F_NAME LIKE 'B%'
     * </blockquote></pre>
     */
    public Expression like(String value) {
        return like(new ConstantExpression(value, this));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like other value.
     * This is equivalent to the SQL "LIKE ESCAPE" operator that except wildcards.
     * The character "%" means any sequence of characters and the character "_" mean any character.
     * i.e. "B%" == "Bob", "B_B" == "BOB"
     * The escape sequence specifies a set of characters the may be used to indicate that
     * an one of the wildcard characters should be interpreted literally.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").like("B\_SMITH", "\")
     *     Java: NA
     *     SQL: F_NAME LIKE 'B\_SMITH ESCAPE '\''
     * </blockquote></pre>
     */
    public Expression like(String value, String escapeSequence) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LikeEscape);
        List args = new ArrayList(2);
        args.add(value);
        args.add(escapeSequence);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like other value.
     * This is equivalent to the SQL "LIKE" operator that except wildcards.
     * The character "%" means any sequence of characters and the character "_" mean any character.
     * i.e. "B%" == "Bob", "B_B" == "BOB"
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").like("B%")
     *     Java: NA
     *     SQL: F_NAME LIKE 'B%'
     * </blockquote></pre>
     */
    public Expression like(Expression argument) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Like);
        return anOperator.expressionFor(this, argument);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value matches the regular expression.
     * This uses the databases support for regular expression.
     * Regular expressions are similar to LIKE except support a much larger scope of comparisons.
     * i.e. "^B.*" == "Bob", "^B.B$" == "BOB"
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").regexp("^B.*")
     *     Java: Pattern.compile("^B.*").matcher(employee.getFirstName()).matches()
     *     SQL: F_NAME REGEXP '^B.*'
     * </blockquote></pre>
     */
    public Expression regexp(String regexp) {
        return regexp(new ConstantExpression(regexp, this));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value matches the regular expression.
     * This uses the databases support for regular expression.
     * Regular expressions are similar to LIKE except support a much larger scope of comparisons.
     * i.e. "^B.*" == "Bob", "^B.B$" == "BOB"
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").regexp("^B.*")
     *     Java: Pattern.compile("^B.*").matcher(employee.getFirstName()).matches()
     *     SQL: F_NAME REGEXP '^B.*'
     * </blockquote></pre>
     */
    public Expression regexp(Expression regexp) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Regexp);
        return anOperator.expressionFor(this, regexp);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like other value.
     * This is equivalent to the SQL "LIKE ESCAPE" operator that except wildcards.
     * The character "%" means any sequence of characters and the character "_" mean any character.
     * i.e. "B%" == "Bob", "B_B" == "BOB"
     * The escape sequence specifies a set of characters the may be used to indicate that
     * an one of the wildcard characters should be interpreted literally.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").like("B\_SMITH", "\")
     *     Java: NA
     *     SQL: F_NAME LIKE 'B\_SMITH ESCAPE '\''
     * </blockquote></pre>
     */
    public Expression like(Expression value, Expression escapeSequence) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.LikeEscape);
        List args = new ArrayList(2);
        args.add(value);
        args.add(escapeSequence);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like the other value, ignoring case.
     * This is a case in-sensitive like.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").likeIgnoreCase("%Bob%")
     *     Java: none
     *     SQL: UPPER(F_NAME) LIKE 'BOB'
     * </blockquote></pre>
     */
    public Expression likeIgnoreCase(String theValue) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().like(theValue.toUpperCase());
        } else {
            return toLowerCase().like(theValue.toLowerCase());
        }
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is like the other value, ignoring case.
     * This is a case in-sensitive like.
     */
    public Expression likeIgnoreCase(Expression theValue) {
        if (shouldUseUpperCaseForIgnoreCase) {
            return toUpperCase().like(theValue.toUpperCase());
        } else {
            return toLowerCase().like(theValue.toLowerCase());
        }
    }

    /**
     * PUBLIC:
     * Function, returns the position of <code>str</code> in <code>this</code>
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("firstName").locate("ob")
     * Java: employee.getFirstName().indexOf("ob") + 1
     * SQL: LOCATE('ob', t0.F_NAME)
     * </blockquote></pre>
     * <p>
     * Note that while in String.locate(str) -1 is returned if not found, and the
     * index starting at 0 if found, in SQL it is 0 if not found, and the index
     * starting at 1 if found.
     */
    public Expression locate(Object str) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Locate);
        List args = new ArrayList(1);
        args.add(str);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, returns the position of <code>str</code> in <code>this</code>,
     * starting the search at <code>fromIndex</code>.
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("firstName").locate("ob", 1)
     * Java: employee.getFirstName().indexOf("ob", 1) + 1
     * SQL: LOCATE('ob', t0.F_NAME, 1)
     * </blockquote></pre>
     * <p>
     * Note that while in String.locate(str) -1 is returned if not found, and the
     * index starting at 0 if found, in SQL it is 0 if not found, and the index
     * starting at 1 if found.
     */
    public Expression locate(String str, int fromIndex) {
        return locate(str, Integer.valueOf(fromIndex));
    }

    /**
     * PUBLIC:
     * Function, returns the position of <code>str</code> in <code>this</code>,
     * starting the search at <code>fromIndex</code>.
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("firstName").locate("ob", 1)
     * Java: employee.getFirstName().indexOf("ob", 1) + 1
     * SQL: LOCATE('ob', t0.F_NAME, 1)
     * </blockquote></pre>
     * <p>
     * Note that while in String.locate(str) -1 is returned if not found, and the
     * index starting at 0 if found, in SQL it is 0 if not found, and the index
     * starting at 1 if found.
     */
    public Expression locate(Object str, Object fromIndex) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Locate2);
        List args = new ArrayList(2);
        args.add(str);
        args.add(fromIndex);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * This represents the aggregate function Maximum. Can be used only within Report Queries.
     */
    public Expression maximum() {
        return getFunction(ExpressionOperator.Maximum);
    }

    /**
     * PUBLIC:
     * This represents the aggregate function Minimum. Can be used only within Report Queries.
     */
    public Expression minimum() {
        return getFunction(ExpressionOperator.Minimum);
    }

    /**
     * PUBLIC:
     * Function, returns the decimal number of months between the two dates.
     */
    public Expression monthsBetween(Object otherDate) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.MonthsBetween);
        return anOperator.expressionFor(this, otherDate);
    }
    
    /**
     * PUBLIC:
     * Return a Map.Entry containing the key and the value from a mapping that maps to a java.util.Map
     * This expression can only be used as a return value in a ReportQuery and cannot be used as part of
     * the WHERE clause in any query
     * 
     * EclipseLink: eb.get("mapAttribute").mapEntry()
     * @return
     */
    public Expression mapEntry(){
        MapEntryExpression expression = new MapEntryExpression(this);
        expression.returnMapEntry();
        return expression;
    }
    
    /**
     * PUBLIC:
     * Return the key from a mapping that maps to a java.util.Map
     * This expression can be used either in as a return value in a ReportQuery or in the WHERE clause in a query
     * 
     * EclipseLink: eb.get("mapAttribute").mapKey()
     * @return
     */
    public Expression mapKey(){
        return new MapEntryExpression(this);
    }

    /**
     * PUBLIC:
     * funcation return a date converted to a new timezone. Equivalent of the Oracle NEW_TIME function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("date").newTime("EST", "PST")
     * Java: NA
     * SQL: NEW_TIME(date, 'EST', 'PST')
     * </blockquote></pre> *
     */
    public Expression newTime(String timeZoneFrom, String timeZoneTo) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NewTime);
        List args = new ArrayList(2);
        args.add(timeZoneFrom);
        args.add(timeZoneTo);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, returns the date with the next day from the source date as the day name given.
     */
    public Expression nextDay(Object dayName) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NextDay);
        return anOperator.expressionFor(this, dayName);
    }

    /**
     * PUBLIC: Returns an expression equivalent to none of <code>attributeName</code>
     * holding true for <code>criteria</code>.
     * <p>
     * For every expression with an anyOf, its negation has either an allOf or a
     * noneOf.  The following two examples will illustrate as the second is the
     * negation of the first:
     * <p>
     * AnyOf Example: Employees with a '613' area code phone number.
     * <pre><blockquote>
     * ReadAllQuery query = new ReadAllQuery(Employee.class);
     * ExpressionBuilder employee = new ExpressionBuilder();
     * Expression exp = employee.anyOf("phoneNumbers").get("areaCode").equal("613");
     * </blockquote></pre>
     * <p>
     * NoneOf Example: Employees with no '613' area code phone numbers.
     * <blockquote><pre>
     * ExpressionBuilder employee = new ExpressionBuilder();
     * ExpressionBuilder phones = new ExpressionBuilder();
     * Expression exp = employee.noneOf("phoneNumbers", phones.get("areaCode").equal("613"));
     * SQL:
     * SELECT ... EMPLOYEE t0 WHERE NOT EXISTS (SELECT ... PHONE t1 WHERE
     *                         (t0.EMP_ID = t1.EMP_ID) AND (t1.AREACODE = '613'))
     * </blockquote></pre>
     * <p>
     * noneOf is the universal counterpart to the existential anyOf.  To have the
     * condition evaluated for each instance it must be put inside of a subquery,
     * which can be expressed as not exists (any of attributeName some condition).
     * (All x such that !y = !Exist x such that y).
     * <p>Likewise the syntax employee.noneOf("phoneNumbers").get("areaCode").equal("613")
     * is not supported for the <code>equal</code> must go inside a subQuery.
     * <p>
     * This method saves you from writing the sub query yourself.  The above is
     * equivalent to the following expression:
     * <pre><blockquote>
     * ExpressionBuilder employee = new ExpressionBuilder();
     * ExpressionBuilder phone = new ExpressionBuilder();
     * ReportQuery subQuery = new ReportQuery(Phone.class, phone);
     * subQuery.retreivePrimaryKeys();
     * subQuery.setSelectionCriteria(phone.equal(employee.anyOf("phoneNumbers").and(
     *         phone.get("areaCode").equal("613")));
     * Expression exp = employee.notExists(subQuery);
     * </blockquote></pre>
     * @param criteria must have its own builder, as it will become the
     * separate selection criteria of a subQuery.
     * @return a notExists subQuery expression
     */
    public Expression noneOf(String attributeName, Expression criteria) {
        ReportQuery subQuery = new ReportQuery();
        subQuery.setShouldRetrieveFirstPrimaryKey(true);
        Expression builder = criteria.getBuilder();
        criteria = builder.equal(anyOf(attributeName)).and(criteria);
        subQuery.setSelectionCriteria(criteria);
        return notExists(subQuery);
    }

    /**
     * INTERNAL:
     * Normalize into a structure that is printable.
     * Also compute printing information such as outer joins.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        //This class has no validation but we should still make the method call for consistency
        //bug # 2956674
        //validation is moved into normalize to ensure that expressions are valid before we attempt to work with them
        validateNode();
        return this;
    }

    /**
     * PUBLIC:
     * Return an expression that is the boolean logical negation of the expression.
     * This is equivalent to the SQL "NOT" operator and the Java "!" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").equal(24).not()
     *     Java: (! (employee.getAge() == 24))
     *     SQL: NOT (AGE = 24)
     * </blockquote></pre>
     */
    public Expression not() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Not);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(byte leftValue, byte rightValue) {
        return notBetween(Byte.valueOf(leftValue), Byte.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(char leftChar, char rightChar) {
        return notBetween(Character.valueOf(leftChar), Character.valueOf(rightChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(double leftValue, double rightValue) {
        return notBetween(Double.valueOf(leftValue), Double.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(float leftValue, float rightValue) {
        return notBetween(Float.valueOf(leftValue), Float.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(int leftValue, int rightValue) {
        return notBetween(Integer.valueOf(leftValue), Integer.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(long leftValue, long rightValue) {
        return notBetween(Long.valueOf(leftValue), Long.valueOf(rightValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(Object leftValue, Object rightValue) {
        return between(leftValue, rightValue).not();
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(Expression leftExpression, Expression rightExpression) {
        return between(leftExpression, rightExpression).not();
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not between two other values.
     * Equivalent to between negated.
     * @see #between(Object, Object)
     */
    public Expression notBetween(short leftValue, short rightValue) {
        return notBetween(Short.valueOf(leftValue), Short.valueOf(rightValue));
    }

    /**
     * PUBLIC: A logical expression for the collection <code>attributeName</code>
     * not being empty.
     * Equivalent to <code>size(attributeName).greaterThan(0)</code>
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.notEmpty("phoneNumbers")
     *     Java: employee.getPhoneNumbers().size() > 0
     *     SQL: SELECT ... FROM EMP t0 WHERE (
     *      (SELECT COUNT(*) FROM PHONE t1 WHERE (t0.EMP_ID = t1.EMP_ID)) > 0)
     * </blockquote></pre>
     * This is a case where a fast operation in java does not translate to an
     * equally fast operation in SQL, requiring a correlated subselect.
     * @see #size(java.lang.String)
     */
    public Expression notEmpty(String attributeName) {
        return size(attributeName).greaterThan(0);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(byte theValue) {
        return notEqual(Byte.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(char theChar) {
        return notEqual(Character.valueOf(theChar));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(double theValue) {
        return notEqual(Double.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(float theValue) {
        return notEqual(Float.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(int theValue) {
        return notEqual(Integer.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(long theValue) {
        return notEqual(Long.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotEqual);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(Expression theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotEqual);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(short theValue) {
        return notEqual(Short.valueOf(theValue));
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not equal to the other value.
     * This is equivalent to the SQL "<>" operator
     *
     * @see #equal(Object)
     */
    public Expression notEqual(boolean theBoolean) {
        return notEqual(Boolean.valueOf(theBoolean));
    }

    /**
     * PUBLIC:
     * Return a sub query expression.
     * A sub query using a report query to define a subselect within another queries expression or select's where clause.
     * The sub query (the report query) will use its own expression builder be can reference expressions from the base expression builder.
     * <p>Example:
     * <pre><blockquote>
     * ExpressionBuilder builder = new ExpressionBuilder();
     * ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
     * subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("name").equal(builder.get("name")));
     * builder.notExists(subQuery);
     * </blockquote></pre>
     */
    public Expression notExists(ReportQuery subQuery) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotExists);
        return anOperator.expressionFor(subQuery(subQuery));
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(byte[] theBytes) {
        List values = new ArrayList(theBytes.length);

        for (int index = 0; index < theBytes.length; index++) {
            values.add(Byte.valueOf(theBytes[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(char[] theChars) {
        List values = new ArrayList(theChars.length);

        for (int index = 0; index < theChars.length; index++) {
            values.add(Character.valueOf(theChars[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(double[] theDoubles) {
        List values = new ArrayList(theDoubles.length);

        for (int index = 0; index < theDoubles.length; index++) {
            values.add(Double.valueOf(theDoubles[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(float[] theFloats) {
        List values = new ArrayList(theFloats.length);

        for (int index = 0; index < theFloats.length; index++) {
            values.add(Float.valueOf(theFloats[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(int[] theInts) {
        List values = new ArrayList(theInts.length);

        for (int index = 0; index < theInts.length; index++) {
            values.add(Integer.valueOf(theInts[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(long[] theLongs) {
        List values = new ArrayList(theLongs.length);

        for (int index = 0; index < theLongs.length; index++) {
            values.add(Long.valueOf(theLongs[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(Object[] theObjects) {
        List values = new ArrayList(theObjects.length);

        for (int index = 0; index < theObjects.length; index++) {
            values.add(theObjects[index]);
        }

        return notIn(values);
    }

    public Expression notIn(ReportQuery subQuery) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotInSubQuery);
        return anOperator.expressionFor(this, subQuery);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(short[] theShorts) {
        List values = new ArrayList(theShorts.length);

        for (int index = 0; index < theShorts.length; index++) {
            values.add(Short.valueOf(theShorts[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression notIn(boolean[] theBooleans) {
        List values = new ArrayList(theBooleans.length);

        for (int index = 0; index < theBooleans.length; index++) {
            values.add(Boolean.valueOf(theBooleans[index]));
        }

        return notIn(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * The collection can be a collection of constants or expressions.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     */
    public Expression notIn(Collection theObjects) {
        return notIn(new CollectionExpression(theObjects, this));
    }

    public Expression notIn(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotIn);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not like the other value.
     * Equivalent to like negated.
     * @see #like(String)
     */
    public Expression notLike(String aString) {
        return notLike(new ConstantExpression(aString, this));
    }


    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not like the other value.
     * Equivalent to like negated.
     * @see #like(String)
     */
    public Expression notLike(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotLike);
        return anOperator.expressionFor(this, arguments);
    }
    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not like the other value.
     * Equivalent to like negated.  
     * @param value string to compare
     * @param escapeSequence the escape character to use
     * @see #like(String)
     */
    public Expression notLike(String value, String escapeSequence) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotLikeEscape);
        List args = new ArrayList(2);
        args.add(value);
        args.add(escapeSequence);
        return anOperator.expressionForArguments(this, args);
    }
    /**
     * PUBLIC:
     * Return an expression that compares if the receivers value is not like the other value.
     * Equivalent to like negated.  
     * @param value string to compare
     * @param escapeSequence the escape character to use
     * @see #like(String)
     */
    public Expression notLike(Expression value, Expression escapeSequence) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotLikeEscape);
        List args = new ArrayList(2);
        args.add(value);
        args.add(escapeSequence);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Return an expression representing a comparison to null
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").notNull()
     *     Java: employee.getAge() != null
     *     SQL: AGE IS NOT NULL
     * </blockquote></pre>
     */
    public Expression notNull() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.NotNull);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression that is the boolean logical combination of both expressions.
     * This is equivalent to the SQL "OR" operator and the Java "||" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").equal("Bob").OR(employee.get("lastName").equal("Smith"))
     *     Java: (employee.getFirstName().equals("Bob")) || (employee.getLastName().equals("Smith"))
     *     SQL: F_NAME = 'Bob' OR L_NAME = 'Smith'
     * </blockquote></pre>
     */
    public Expression or(Expression theExpression) {
        // Allow ands with null.
        if (theExpression == null) {
            return this;
        }

        ExpressionBuilder base = getBuilder();
        Expression expressionToUse = theExpression;

        // Ensure the same builder, unless a parralel query is used.
        if ((theExpression.getBuilder() != base) && (theExpression.getBuilder().getQueryClass() == null)) {
            expressionToUse = theExpression.rebuildOn(base);
        }

        if (base == this) {// Allow and to be sent to the builder.
            return expressionToUse;
        }

        ExpressionOperator anOperator = getOperator(ExpressionOperator.Or);
        return anOperator.expressionFor(this, expressionToUse);
    }

    /**
     * INTERNAL:
     */
    public Expression performOperator(ExpressionOperator anOperator, List args) {
        return anOperator.expressionForArguments(this, args);
    }

    protected void postCopyIn(Map alreadyDone) {
    }

    /**
     * ADVANCED:
     * Inserts the SQL as is directly into the expression.
     * The sql will be printed immediately after (postfixed to) the sql for
     * <b>this</b>.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     */
    public Expression postfixSQL(String sqlString) {
        ExpressionOperator anOperator = new ExpressionOperator();
        anOperator.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        v.add(sqlString);
        anOperator.printsAs(v);
        anOperator.bePostfix();
        anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);

        return anOperator.expressionFor(this);
    }

    /**
     * ADVANCED:
     * Insert the SQL as is directly into the expression.
     * The sql will be printed immediately before (prefixed to) the sql for
     * <b>this</b>.
      * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     */
    public Expression prefixSQL(String sqlString) {
        ExpressionOperator anOperator = new ExpressionOperator();
        anOperator.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        v.add(sqlString);
        anOperator.printsAs(v);
        anOperator.bePrefix();
        anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);

        return anOperator.expressionFor(this);
    }

    /**
     * INTERNAL:
     * Print SQL
     */
    public abstract void printSQL(ExpressionSQLPrinter printer);

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        //do nothing
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     * If receiver is a complex expression, use cloneUsing(newBase) instead.
     * @see #cloneUsing(Expression newBase)
     */
    public abstract Expression rebuildOn(Expression newBase);

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    public abstract void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder);

    /**
     * ADVANCED:
     * For Object-relational support.
     */
    public Expression ref() {
        return getFunction(ExpressionOperator.Ref);
    }

    protected Expression registerIn(Map alreadyDone) {
        Expression copy = shallowClone();
        alreadyDone.put(this, copy);
        copy.postCopyIn(alreadyDone);
        return copy;

    }

    /**
     * PUBLIC:
     * Function, returns the string with occurances of the first substring replaced with the second substring.
     */
    public Expression replace(Object stringToReplace, Object stringToReplaceWith) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Replace);
        List args = new ArrayList(2);
        args.add(stringToReplace);
        args.add(stringToReplaceWith);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * return the result of this query repeated a given number of times.
     * Equivalent of the Sybase REPLICATE function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").replicate(2)
     * Java: NA
     * SQL: REPLICATE(name, 2)
     * </blockquote></pre>
     */
    public Expression replicate(int constant) {
        return replicate(Integer.valueOf(constant));
    }

    /**
     * PUBLIC:
     * return the result of this query repeated a given number of times.
     * Equivalent of the Sybase REPLICATE function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").replicate(2)
     * Java: NA
     * SQL: REPLICATE(name, 2)
     * </blockquote></pre>
     */
    public Expression replicate(Object theValue) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Replicate);
        return anOperator.expressionFor(this, theValue);
    }

    /**
     * Reset cached information here so that we can be sure we're accurate.
     */
    protected void resetCache() {
    }

    /**
     * PUBLIC:
     * Function return the reverse of the query result. Equivalent of the
     * Sybase REVERSE function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").reverse()
     * Java: NA
     * SQL: REVERSE(name)
     * </blockquote></pre>
     */
    public Expression reverse() {
        return getFunction(ExpressionOperator.Reverse);
    }

    /**
     * PUBLIC:
     * Function return a given number of characters starting at the
     * right of a string. Equivalent to the Sybase RIGHT function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").right(2)
     * Java: NA
     * SQL: RIGHT(name, 2)
     * </blockquote></pre>
     */
    public Expression right(int characters) {
        return right(Integer.valueOf(characters));
    }

    /**
     * PUBLIC:
     * Function return a given number of characters starting at the
     * right of a string. Equivalent to the Sybase RIGHT function
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("name").right(2)
     * Java: NA
     * SQL: RIGHT(name, 2)
     * </blockquote></pre>
     */
    public Expression right(Object characters) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Right);
        return anOperator.expressionFor(this, characters);
    }

    /**
     * PUBLIC:
     * Function, returns the string padded with the substring to the size.
     */
    public Expression rightPad(int size, Object substring) {
        return rightPad(Integer.valueOf(size), substring);
    }

    /**
     * PUBLIC:
     * Function, returns the string padded with the substring to the size.
     */
    public Expression rightPad(Object size, Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.RightPad);
        List args = new ArrayList(2);
        args.add(size);
        args.add(substring);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, returns the string right trimmed for white space.
     */
    public Expression rightTrim() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.RightTrim);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns the string with the substring trimed from the right.
     */
    public Expression rightTrim(Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.RightTrim2);
        return anOperator.expressionFor(this, substring);
    }

    /**
     * PUBLIC:
     * Function, returns the date rounded to the year, month or day.
     */
    public Expression roundDate(Object yearOrMonthOrDayRoundToken) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.RoundDate);
        return anOperator.expressionFor(this, yearOrMonthOrDayRoundToken);
    }

    /**
     * PUBLIC:
     * Return whether this expression should be included in the SELECT clause if it is used
     * in an ORDER BY clause
     */
    public boolean selectIfOrderedBy() {
        return selectIfOrderedBy;
    }

    /**
     * INTERNAL:
     * Set the local base expression, ie the one on the other side of the operator
     * Most types will ignore this, since they don't need it.
     */
    public void setLocalBase(Expression exp) {
    }

    /**
     * PUBLIC:
     * Set whether this expression should be included in the SELECT clause of a query
     * that uses it in the ORDER BY clause.
     *
     * @param selectIfOrderedBy
     */
    public void setSelectIfOrderedBy(boolean selectIfOrderedBy) {
        this.selectIfOrderedBy = selectIfOrderedBy;
    }

    /**
     * INTERNAL:
     */
    public Expression shallowClone() {
        Expression result = null;
        try {
            result = (Expression)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        return result;
    }

    /**
     * PUBLIC: A logical expression for the size of collection <code>attributeName</code>.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.size("phoneNumbers")
     *     Java: employee.getPhoneNumbers().size()
     *     SQL: SELECT ... FROM EMP t0 WHERE  ...
     *      (SELECT COUNT(*) FROM PHONE t1 WHERE (t0.EMP_ID = t1.EMP_ID))
     * </blockquote></pre>
     * This is a case where a fast operation in java does not translate to an
     * equally fast operation in SQL, requiring a correlated subselect.
     */
    public Expression size(String attributeName) {
        return SubSelectExpression.createSubSelectExpressionForCount(this, this, attributeName, null);
    }

    /**
     * PUBLIC: A logical expression for the size of collection expression.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.size(Class returnType)
     *     Java: employee.getPhoneNumbers().size()
     *     SQL: SELECT ... FROM EMP t0 WHERE  ...
     *      (SELECT COUNT(*) FROM PHONE t1 WHERE (t0.EMP_ID = t1.EMP_ID))
     * </blockquote></pre>
     * This is a case where a fast operation in java does not translate to an
     * equally fast operation in SQL, requiring a correlated subselect.
     */
    public Expression size(Class returnType) {
        if (((BaseExpression)this).getBaseExpression() == null){
            return SubSelectExpression.createSubSelectExpressionForCount(this, this, null, returnType);
        }
        return SubSelectExpression.createSubSelectExpressionForCount(((BaseExpression)this).getBaseExpression(), this, null, returnType);
    }

    /**
     * PUBLIC:
     * This represents the aggregate function StandardDeviation. Can be used only within Report Queries.
     */
    public Expression standardDeviation() {
        return getFunction(ExpressionOperator.StandardDeviation);
    }

    /**
     * PUBLIC:
     * Return a sub query expression.
     * A sub query using a report query to define a subselect within another queries expression or select's where clause.
     * The sub query (the report query) will use its own expression builder be can reference expressions from the base expression builder.
     * <p>Example:
     * <pre><blockquote>
     * ExpressionBuilder builder = new ExpressionBuilder();
     * ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
     * subQuery.addMaximum("salary");
     * builder.get("salary").equal(builder.subQuery(subQuery));
     * </blockquote></pre>
     */
    public Expression subQuery(ReportQuery subQuery) {
        return new SubSelectExpression(subQuery, this);
    }

    /**
     * PUBLIC:
     * Function, returns the substring from the source string.
     * EclipseLink: employee.get("firstName").substring(1, 2)
     * Java: NA
     * SQL: SUBSTR(FIRST_NAME, 1, 2)
     */
    public Expression substring(int startPosition, int size) {
        return substring(Integer.valueOf(startPosition), Integer.valueOf(size));
    }

    /**
     * PUBLIC:
     * Function, returns the substring from the source string.
     * EclipseLink: employee.get("firstName").substring(1, 2)
     * Java: NA
     * SQL: SUBSTR(FIRST_NAME, 1, 2)
     */
    public Expression substring(Object startPosition, Object size) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Substring);
        List args = new ArrayList(2);
        args.add(startPosition);
        args.add(size);
        return anOperator.expressionForArguments(this, args);
    }
    
    /**
     * PUBLIC:
     * Function, returns the substring from the source string.
     * EclipseLink: employee.get("firstName").substring(1)
     * Java: NA
     * SQL: SUBSTR(FIRST_NAME, 1)
     */
    public Expression substring(int startPosition) {
        return substring(Integer.valueOf(startPosition));
    }

    /**
     * PUBLIC:
     * Function, returns the substring from the source string.
     * EclipseLink: employee.get("firstName").substring(1)
     * Java: NA
     * SQL: SUBSTR(FIRST_NAME, 1)
     */
    public Expression substring(Object startPosition) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.SubstringSingleArg);
        List args = new ArrayList(1);
        args.add(startPosition);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * This represents the aggregate function Sum. Can be used only within Report Queries.
     */
    public Expression sum() {
        return getFunction(ExpressionOperator.Sum);
    }

    /**
     * PUBLIC:
     * Function, returns the single character string with the ascii or character set value.
     */
    public Expression toCharacter() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Chr);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns date from the string using the default format.
     */
    public Expression toDate() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToDate);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression that represents the receiver value converted to a character string.
     * This is equivalent to the SQL "TO_CHAR" operator and Java "toString" method.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("salary").toChar().equal("100000")
     *     Java: employee.getSalary().toString().equals("100000")
     *     SQL: TO_CHAR(SALARY) = '100000'
     * </blockquote></pre>
     */
    public Expression toChar() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToChar);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression that represents the receiver value converted to a character string,
     * with the database formating options (i.e. 'year', 'yyyy', 'day', etc.).
     * This is equivalent to the SQL "TO_CHAR" operator and Java Date API.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("startDate").toChar("day").equal("monday")
     *     Java: employee.getStartDate().getDay().equals("monday")
     *     SQL: TO_CHAR(START_DATE, 'day') = 'monday'
     * </blockquote></pre>
     */
    public Expression toChar(String format) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToCharWithFormat);
        return anOperator.expressionFor(this, format);
    }

    /**
     * PUBLIC:
     * Return an expression that represents the receiver value converted to lower case.
     * This is equivalent to the SQL "LOWER" operator and Java "toLowerCase" method.
     * This is only allowed for String attribute values.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").toLowerCase().equal("bob")
     *     Java: employee.getFirstName().toLowerCase().equals("bob")
     *     SQL: LOWER(F_NAME) = 'bob'
     * </blockquote></pre>
     */
    public Expression toLowerCase() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToLowerCase);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns the number converted from the string.
     */
    public Expression toNumber() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToNumber);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Print a debug form of the expression tree.
     */
    public String toString() {
        try {
            StringWriter innerWriter = new StringWriter();
            BufferedWriter outerWriter = new BufferedWriter(innerWriter);
            toString(outerWriter, 0);
            outerWriter.flush();
            return innerWriter.toString();
        } catch (IOException e) {
            return ToStringLocalization.buildMessage("error_printing_expression", (Object[])null);
        }
    }

    /**
     * INTERNAL:
     * Print a debug form of the expression tree.
     */
    public void toString(BufferedWriter writer, int indent) throws IOException {
        writer.newLine();
        for (int i = 0; i < indent; i++) {
            writer.write("   ");
        }
        writer.write(descriptionOfNodeType());
        writer.write(" ");
        writeDescriptionOn(writer);
        writeSubexpressionsTo(writer, indent + 1);
    }

    /**
     * PUBLIC:
     * Return an expression that represents the receiver value converted to upper case.
     * This is equivalent to the SQL "UPPER" operator and Java "toUpperCase" method.
     * This is only allowed for String attribute values.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("firstName").toUpperCase().equal("BOB")
     *     Java: employee.getFirstName().toUpperCase().equals("BOB")
     *     SQL: UPPER(F_NAME) = 'BOB'
     * </blockquote></pre>
     */
    public Expression toUpperCase() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ToUpperCase);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns the string with the first letter of each word capitalized.
     */
    public Expression toUppercaseCasedWords() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Initcap);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Function, returns the string with each char from the from string converted to the char in the to string.
     */
    public Expression translate(Object fromString, Object toString) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Translate);
        List args = new ArrayList(2);
        args.add(fromString);
        args.add(toString);
        return anOperator.expressionForArguments(this, args);
    }

    /**
     * PUBLIC:
     * Function, returns the string trimmed for white space.
     */
    public Expression trim() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Trim);
        return anOperator.expressionFor(this);
    }
    
    /**
     * PUBLIC:
     * Function, returns the string right and left trimmed for the substring.
     */
    public Expression trim(Object substring) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Trim2);
        return anOperator.expressionForWithBaseLast(this, substring);
    }

    /**
     * PUBLIC:
     * XMLType Function, extracts a secton of XML from a larget XML document
     * @param xpath XPath expression representing the node to be returned
     */
    public Expression extractXml(String xpath) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ExtractXml);
        return anOperator.expressionFor(this, xpath);
    }

    /**
     * PUBLIC:
     * Extract the date part from the date/time value.
     * EXTRACT is part of the SQL standard, so should be supported by most databases.
     * @param part is the date part to extract, "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE_HOUR", "TIMEZONE_MINUTE".
     */
    public Expression extract(String part) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Extract);
        return anOperator.expressionFor(this, literal(part));
    }

    /**
     * PUBLIC:
     * Cast the value to the database type.
     * CAST is part of the SQL standard, so should be supported by most databases.
     * @param type is the database type name, this is database specific but should include, "CHAR", "VARCHAR", "NUMERIC", "INTEGER", "DATE", "TIME", "TIMESTAMP",
     * the type may include a size and scale.
     */
    public Expression cast(String type) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Cast);
        return anOperator.expressionFor(this, literal(type));
    }

    /**
     * PUBLIC:
     * XMLType Function, extracts a value from an XMLType field
     * @param xpath XPath expression
     */
    public Expression extractValue(String xpath) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ExtractValue);
        return anOperator.expressionFor(this, xpath);
    }

    /**
     * PUBLIC:
     * XMLType Function, gets the number of nodes returned by the given xpath expression
     * returns 0 if there are none
     * @param xpath Xpath expression
     */
    public Expression existsNode(String xpath) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ExistsNode);
        return anOperator.expressionFor(this, xpath);
    }

    /**
     * PUBLIC:
     * XMLType Function - evaluates to 0 if the xml is a well formed document and 1 if the document
     * is a fragment
     */
    public Expression isFragment() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.IsFragment);
        return anOperator.expressionFor(this);
    }
    
    /**
     * PUBLIC:
     * XMLType Function - gets a string value from an XMLType
     */
    public Expression getStringVal() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GetStringVal);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * XMLType Function - gets a number value from an XMLType
     */
    public Expression getNumberVal() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.GetNumberVal);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * return the date truncated to the indicated datePart. Equivalent
     * to the Sybase TRUNC function for dates
     * <p>Example:
     * <pre><blockquote>
     * EclipseLink: employee.get("date").truncDate(year)
     * Java: NA
     * SQL: TRUNC(date, year)
     * </blockquote></pre>  */
    public Expression truncateDate(String datePart) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.TruncateDate);
        return anOperator.expressionFor(this, datePart);

    }

    /**
     * INTERNAL:
     * We are given an expression that comes from a different context than the one in which this was built,
     * e.g. it is the selection criteria of a mapping, or the criteria on which multiple tables are joined in a descriptor.
     * We need to transform it so it refers to the objects we are dealing with, and AND it into the rest of our expression.
     *
     * We want to replace the original base expression with (newBase), and any parameters will be given values based
     * on the context which (this) provides.
     *
     * For example, suppose that the main expression is
     *      emp.address.streetName = 'something'
     * and we are trying to twist the selection criteria for the mapping 'address' in Employee. Because that mapping
     * selects addresses, we will use the 'address' node as the base. Values for any parameters will come from the 'emp' node,
     * which was the base of the original expression. Note that the values need not be constants, they can be fields.
     *
     * We do this by taking the tree we're trying to merge and traverse it more or less re-executing it
     * it with the appropriate initial receiver and context.
     * Return the root of the new expression tree. This will probably need to be AND'ed with the root of the old tree.
     */
    public Expression twist(Expression expression, Expression newBase) {
        if (expression == null) {
            return null;
        }
        return expression.twistedForBaseAndContext(newBase, this, null);

    }

    /**
     * INTERNAL:
     * Rebuild myself against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist
     * See the comment there for more details"
     */
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        // Will be overridden by subclasses
        return this;
    }

    /**
     * INTERNAL:
     * Do any required validation for this node. Throw an exception for any incorrect constructs.
     */
    public void validateNode() {
    }

    /**
     * PUBLIC:
     * Function, this represents the value function, used in nestedtable
     */
    public Expression value() {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Value);
        return anOperator.expressionFor(this);
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(byte constant) {
        return value(Byte.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(char constant) {
        return value(Character.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(double constant) {
        return value(Double.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(float constant) {
        return value(Float.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(int constant) {
        return value(Integer.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(long constant) {
        return value(Long.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(Object constant) {
        return new ConstantExpression(constant, this);
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(short constant) {
        return value(Short.valueOf(constant));
    }

    /**
     * PUBLIC:
     * Return an expression on the constant.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("a constant", builder.value("a constant"));
     * </blockquote></pre>
     */
    public Expression value(boolean constant) {
        return value(Boolean.valueOf(constant));
    }

    /**
     * ADVANCED:
     * Return an expression on the literal.
     * A literal is a specific SQL syntax string that will be printed as is without quotes in the SQL.
     * It can be useful for printing database key words or global variables.
     * <p>Example:
     * <pre><blockquote>
     * reportQuery.addItem("currentTime", builder.literal("SYSDATE"));
     * </blockquote></pre>
     */
    public Expression literal(String literal) {
        return new LiteralExpression(literal, this);
    }

    /**
     * ADVANCED:
     * Return an expression for the alias.
     * This allows an alias used in the select clause to be used in other clauses.
     */
    public Expression alias(String alias) {
        return literal(alias);
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     * New parameter added for feature 2612601
     * @param isObjectUnregistered true if object possibly not a clone, but is being
     * conformed against the unit of work cache.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        throw QueryException.cannotConformExpression();
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy) {
        return valueFromObject(object, session, translationRow, valueHolderPolicy, false);
    }

    /**
     * PUBLIC:
     * Function, this represents the aggregate function Variance. Can be used only within Report Queries.
     */
    public Expression variance() {
        return getFunction(ExpressionOperator.Variance);
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write("some expression");
    }

    /**
     * INTERNAL:
     * Append the field name to the writer. Should be overridden for special operators such as functions.
     */
    protected void writeField(ExpressionSQLPrinter printer, DatabaseField field, SQLSelectStatement statement) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        if (statement.requiresAliases()) {
            if (field.getTable() != lastTable) {
                lastTable = field.getTable();
                currentAlias = aliasForTable(lastTable);
            }
            printer.printString(currentAlias.getQualifiedNameDelimited(printer.getPlatform()));
            printer.printString(".");
        }
        printer.printString(field.getNameDelimited(printer.getPlatform()));

        //bug6070214: unique field aliases need to be generated when required.
        if (statement.getUseUniqueFieldAliases()){
            printer.printString(" AS " + statement.generatedAlias(field.getNameDelimited(printer.getPlatform())));
        }
    }

    /**
     * INTERNAL:
     * Append the field's alias to the writer.
     * This is used for pessimistic locking.
     */
    protected void writeAlias(ExpressionSQLPrinter printer, DatabaseField field, SQLSelectStatement statement) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }
        
        if (statement.requiresAliases()) {
            if (field.getTable() != this.lastTable) {
                this.lastTable = field.getTable();
                this.currentAlias = aliasForTable(this.lastTable);
            }
            printer.printString(this.currentAlias.getQualifiedNameDelimited(printer.getPlatform()));
        } else {
            printer.printString(field.getTable().getQualifiedNameDelimited(printer.getPlatform()));
        }
    }

    /**
     * INTERNAL:
     * called from SQLSelectStatement.writeFieldsFromExpression(...)
     */
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        for (Enumeration fieldsEnum = getFields().elements(); fieldsEnum.hasMoreElements();) {
            DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
            newFields.add(field);
            writeField(printer, field, statement);
        }
    }

    /**
     * INTERNAL:
     * Used in SQL printing.
     */
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        // In general, there are no sub-expressions
    }

    /**
     *
     * PUBLIC:
     * Return an expression that is used with a comparison expression.
     * The ANY keyword denotes that the search condition is TRUE if the comparison is TRUE
     * for at least one of the values that is returned. If the subquery returns no value,
     * the search condition is FALSE
     */
    public Expression any(byte[] theBytes) {
        List values = new ArrayList(theBytes.length);

        for (int index = 0; index < theBytes.length; index++) {
            values.add(Byte.valueOf(theBytes[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(char[] theChars) {
        List values = new ArrayList(theChars.length);

        for (int index = 0; index < theChars.length; index++) {
            values.add(Character.valueOf(theChars[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(double[] theDoubles) {
        List values = new ArrayList(theDoubles.length);

        for (int index = 0; index < theDoubles.length; index++) {
            values.add(Double.valueOf(theDoubles[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(float[] theFloats) {
        List values = new ArrayList(theFloats.length);

        for (int index = 0; index < theFloats.length; index++) {
            values.add(Float.valueOf(theFloats[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(int[] theInts) {
        List values = new ArrayList(theInts.length);

        for (int index = 0; index < theInts.length; index++) {
            values.add(Integer.valueOf(theInts[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(long[] theLongs) {
        List values = new ArrayList(theLongs.length);

        for (int index = 0; index < theLongs.length; index++) {
            values.add(Long.valueOf(theLongs[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(Object[] theObjects) {
        List values = new ArrayList(theObjects.length);

        for (int index = 0; index < theObjects.length; index++) {
            values.add(theObjects[index]);
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(short[] theShorts) {
        List values = new ArrayList(theShorts.length);

        for (int index = 0; index < theShorts.length; index++) {
            values.add(Short.valueOf(theShorts[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression any(boolean[] theBooleans) {
        List values = new ArrayList(theBooleans.length);

        for (int index = 0; index < theBooleans.length; index++) {
            values.add(Boolean.valueOf(theBooleans[index]));
        }

        return any(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     * @deprecated since 2.4 replaced by any(List)
     * @see #any(List)
     */
    @Deprecated
    public Expression any(Vector theObjects) {
        return any((List)theObjects);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     */
    public Expression any(List theObjects) {
        return any(new ConstantExpression(theObjects, this));
    }

    public Expression any(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Any);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a union expression with the subquery.
     */
    public Expression union(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Union);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a intersect expression with the subquery.
     */
    public Expression intersect(ReportQuery query) {
        return intersect(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a intersect all expression with the subquery.
     */
    public Expression intersectAll(ReportQuery query) {
        return intersectAll(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a intersect expression with the subquery.
     */
    public Expression intersect(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Intersect);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a except expression with the subquery.
     */
    public Expression except(ReportQuery query) {
        return except(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a except all expression with the subquery.
     */
    public Expression exceptAll(ReportQuery query) {
        return exceptAll(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a except expression with the subquery.
     */
    public Expression except(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Except);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a union expression with the subquery.
     */
    public Expression union(ReportQuery query) {
        return union(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a union all expression with the subquery.
     */
    public Expression unionAll(ReportQuery query) {
        return unionAll(subQuery(query));
    }

    /**
     * PUBLIC:
     * Return a union all expression with the subquery.
     */
    public Expression unionAll(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.UnionAll);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a intersect all expression with the subquery.
     */
    public Expression intersectAll(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.IntersectAll);
        return anOperator.expressionFor(this, arguments);
    }

    /**
     * PUBLIC:
     * Return a except all expression with the subquery.
     */
    public Expression exceptAll(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.ExceptAll);
        return anOperator.expressionFor(this, arguments);
    }

    public Expression any(ReportQuery subQuery) {
        return any(subQuery(subQuery));
    }

    /**
     * PUBLIC:
     * Return an expression that is used with a comparison expression.
     * The SOME keyword denotes that the search condition is TRUE if the comparison is TRUE
     * for at least one of the values that is returned. If the subquery returns no value,
     * the search condition is FALSE
     */
    public Expression some(byte[] theBytes) {
        List values = new ArrayList(theBytes.length);

        for (int index = 0; index < theBytes.length; index++) {
            values.add(Byte.valueOf(theBytes[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(char[] theChars) {
        List values = new ArrayList(theChars.length);

        for (int index = 0; index < theChars.length; index++) {
            values.add(Character.valueOf(theChars[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(double[] theDoubles) {
        List values = new ArrayList(theDoubles.length);

        for (int index = 0; index < theDoubles.length; index++) {
            values.add(Double.valueOf(theDoubles[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(float[] theFloats) {
        List values = new ArrayList(theFloats.length);

        for (int index = 0; index < theFloats.length; index++) {
            values.add(Float.valueOf(theFloats[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(int[] theInts) {
        List values = new ArrayList(theInts.length);

        for (int index = 0; index < theInts.length; index++) {
            values.add(Integer.valueOf(theInts[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(long[] theLongs) {
        List values = new ArrayList(theLongs.length);

        for (int index = 0; index < theLongs.length; index++) {
            values.add(Long.valueOf(theLongs[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(Object[] theObjects) {
        List values = new ArrayList(theObjects.length);

        for (int index = 0; index < theObjects.length; index++) {
            values.add(theObjects[index]);
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(short[] theShorts) {
        List values = new ArrayList(theShorts.length);

        for (int index = 0; index < theShorts.length; index++) {
            values.add(Short.valueOf(theShorts[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression some(boolean[] theBooleans) {
        List values = new ArrayList(theBooleans.length);

        for (int index = 0; index < theBooleans.length; index++) {
            values.add(Boolean.valueOf(theBooleans[index]));
        }

        return some(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     * @deprecated since 2.4 replaced by some(List)
     * @see #some(List)
     */
    @Deprecated    
    public Expression some(Vector theObjects) {
        return some(new ConstantExpression(theObjects, this));
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     */    
    public Expression some(List theObjects) {
        return some(new ConstantExpression(theObjects, this));
    }

    public Expression some(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.Some);
        return anOperator.expressionFor(this, arguments);
    }

    public Expression some(ReportQuery subQuery) {
        return some(subQuery(subQuery));
    }

    /**
     *
     * PUBLIC:
     * Return an expression that is used with a comparison expression.
     * The SOME keyword denotes that the search condition is TRUE if the comparison is TRUE
     * for at least one of the values that is returned. If the subquery returns no value,
     * the search condition is FALSE
     */
    public Expression all(byte[] theBytes) {
        List values = new ArrayList(theBytes.length);

        for (int index = 0; index < theBytes.length; index++) {
            values.add(Byte.valueOf(theBytes[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(char[] theChars) {
        List values = new ArrayList(theChars.length);

        for (int index = 0; index < theChars.length; index++) {
            values.add(Character.valueOf(theChars[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(double[] theDoubles) {
        List values = new ArrayList(theDoubles.length);

        for (int index = 0; index < theDoubles.length; index++) {
            values.add(Double.valueOf(theDoubles[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(float[] theFloats) {
        List values = new ArrayList(theFloats.length);

        for (int index = 0; index < theFloats.length; index++) {
            values.add(Float.valueOf(theFloats[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(int[] theInts) {
        List values = new ArrayList(theInts.length);

        for (int index = 0; index < theInts.length; index++) {
            values.add(Integer.valueOf(theInts[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(long[] theLongs) {
        List values = new ArrayList(theLongs.length);

        for (int index = 0; index < theLongs.length; index++) {
            values.add(Long.valueOf(theLongs[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(Object[] theObjects) {
        List values = new ArrayList(theObjects.length);

        for (int index = 0; index < theObjects.length; index++) {
            values.add(theObjects[index]);
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(short[] theShorts) {
        List values = new ArrayList(theShorts.length);

        for (int index = 0; index < theShorts.length; index++) {
            values.add(Short.valueOf(theShorts[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     */
    public Expression all(boolean[] theBooleans) {
        List values = new ArrayList(theBooleans.length);

        for (int index = 0; index < theBooleans.length; index++) {
            values.add(Boolean.valueOf(theBooleans[index]));
        }

        return all(values);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     * @deprecated since 2.4 replaced by all(List)
     * @see #all(List)
     */
    @Deprecated
    public Expression all(Vector theObjects) {
        return all((List)theObjects);
    }

    /**
     * PUBLIC:
     * Return an expression that checks if the receivers value is contained in the collection.
     * This is equivalent to the SQL "IN" operator and Java "contains" operator.
     * <p>Example:
     * <pre><blockquote>
     *     EclipseLink: employee.get("age").in(ages)
     *     Java: ages.contains(employee.getAge())
     *     SQL: AGE IN (55, 18, 30)
     * </blockquote></pre>
     */
    public Expression all(List theObjects) {
        return all(new ConstantExpression(theObjects, this));
    }

    public Expression all(Expression arguments) {
        ExpressionOperator anOperator = getOperator(ExpressionOperator.All);
        return anOperator.expressionFor(this, arguments);
    }

    public Expression all(ReportQuery subQuery) {
        return all(subQuery(subQuery));
    }
    
    /**
     * INTERNAL:
     * Lookup the descriptor for this item by traversing its expression recursively.
     */
    public ClassDescriptor getLeafDescriptor(DatabaseQuery query, ClassDescriptor rootDescriptor, AbstractSession session) {        
        return null;
    }
    
    /**
     * INTERNAL:
     * Lookup the mapping for this item by traversing its expression recursively.
     * If an aggregate of foreign mapping is found it is traversed.
     */
    public DatabaseMapping getLeafMapping(DatabaseQuery query, ClassDescriptor rootDescriptor, AbstractSession session) {
        return null;
    }
    
}
