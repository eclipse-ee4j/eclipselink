/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 * Copyright (c) 2011, 2022 Jenzabar, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Jenzabar - Initial implementation

package org.eclipse.persistence.platform.database;

import java.io.IOException;
import java.io.Serializable; // for javadoc only
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform; // for javadoc only
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.UpdateAllQuery; // for javadoc only
// for javadoc only

/**
 * An {@link InformixPlatform} that fixes many EclipseLink bugs
 * related to Informix support.
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401393">EclipseLink
 * bug 401393</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401718">EclipseLink
 * bug 401718</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401746">EclipseLink
 * bug 401746</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
 * bug 402037</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402180">EclipseLink
 * bug 402180</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402600">EclipseLink
 * bug 402600</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
 * bug 402953</a>
 *
 * @see <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=405645">EclipseLink
 * bug 405645</a>
 */
public class Informix11Platform extends InformixPlatform {

  /**
   * The version of this class for serialization purposes.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link Informix11Platform}.  Calls {@link
   * #setShouldBindLiterals(boolean)} with {@code false} as a
   * parameter value.
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401718">EclipseLink
   * bug 401718</a>
   */
  public Informix11Platform() {
    super();
    this.setShouldBindLiterals(false);
  }

  /**
   * Fixes <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402600">EclipseLink
   * bug 402600</a> by making sure that the {@link
   * ExpressionOperator#distinct() Distinct} {@link
   * ExpressionOperator} is set to {@linkplain
   * ExpressionOperator#printsAs(String) print as} {@code
   * DISTINCT&nbsp;} (no parentheses, one trailing space), and fixes
   * <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a> by {@linkplain
   * DatasourcePlatform#addOperator(ExpressionOperator) adding
   * platform operators} for {@linkplain
   * ExpressionOperator#CurrentDate current date} and {@linkplain
   * ExpressionOperator#CurrentTime current time}.
   *
   * @see #currentDateOperator()
   *
   * @see #currentTimeOperator()
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402600">EclipseLink
   * bug 402600</a>
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a>
   */
  @Override
  protected void initializePlatformOperators() {
    super.initializePlatformOperators();
    this.addOperator(this.currentDateOperator());
    this.addOperator(this.currentTimeOperator());
    this.addOperator(this.distinctOperator());
  }

  /**
   * Fixes <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a> by returning the result of running the following
   * code: {@link ExpressionOperator#simpleFunctionNoParentheses(int,
   * String)
   * ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate,
   * "CURRENT YEAR TO DAY");}
   *
   * @return a non-{@code null} {@link ExpressionOperator}
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a>
   */
  protected ExpressionOperator currentDateOperator() {
    return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate, "CURRENT YEAR TO DAY");
  }

  /**
   * Fixes <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a> by returning the result of running the following
   * code: {@link ExpressionOperator#simpleFunctionNoParentheses(int,
   * String)
   * ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate,
   * "CURRENT YEAR TO FRACTION(3)");}
   *
   * @return a non-{@code null} {@link ExpressionOperator}
   *
   * <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402953">EclipseLink
   * bug 402953</a>
   */
  protected ExpressionOperator currentTimeOperator() {
    return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate, "CURRENT YEAR TO FRACTION(3)");
  }

  /**
   * Fixes <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402600">EclipseLink
   * bug 402600</a> by making sure that the {@link
   * ExpressionOperator#distinct() Distinct} {@link
   * ExpressionOperator} is set to {@linkplain
   * ExpressionOperator#printsAs(String) print as} {@code
   * DISTINCT&nbsp;} (no parentheses, one trailing space), and fixes
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402600">EclipseLink
   * bug 402600</a>
   */
  protected ExpressionOperator distinctOperator() {
    ExpressionOperator operator = ExpressionOperator.distinct();
    operator.printsAs("DISTINCT "); // no parens, one space
    return operator;
  }

  /**
   * Overrides the default behavior to return {@code false},
   * indicating that ANSI {@code OUTER JOIN} syntax should be used,
   * since all modern versions of Informix support it.
   *
   * @return {@code false} when invoked
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401393">EclipseLink
   * bug 401393</a>
   */
  @Override
  public boolean isInformixOuterJoin() {
    return false;
  }

  /**
   * Fixes <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401746">EclipseLink
   * bug 401746</a> by writing out either {@code 't'} or {@code
   * 'f'}&mdash;with single quotes&mdash; instead of {@code 1} or
   * {@code 0}.
   *
   * @param booleanValue a non-{@code null} {@link Boolean} to append
   *
   * @param writer a non-{@code null} {@link Writer} to append the
   * literal value to
   *
   * @exception IllegalArgumentException if either {@code
   * booleanValue} or {@code writer} is {@code null}
   *
   * @exception IOException if the supplied {@link Writer} throws an
   * {@link IOException}
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=401393">EclipseLink
   * bug 401393</a>
   */
  @Override
  protected void appendBoolean(final Boolean booleanValue, final Writer writer) throws IOException {
    if (booleanValue == null) {
      throw new IllegalArgumentException("booleanValue", new NullPointerException("booleanValue"));
    }
    if (writer == null) {
      throw new IllegalArgumentException("writer", new NullPointerException("writer"));
    }
    if (Boolean.TRUE.equals(booleanValue)) {
      writer.write("'t'");
    } else {
      writer.write("'f'");
    }
  }

  /**
   * Returns {@code true} when invoked to indicate that parameter
   * binding <em>must not</em> be used when working with temporary
   * tables as part of an {@code UPDATE} query.
   *
   * <p>Parsing the English is a little difficult in this method name.
   * It means: "Do not bind parameters in queries that are part of an
   * overall 'update all' operation where {@linkplain
   * #shouldAlwaysUseTempStorageForModifyAll() temporary tables are
   * being used}."</p>
   *
   * @return {@code true} when invoked
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  public boolean dontBindUpdateAllQueryUsingTempTables() {
    return true;
  }

  /**
   * Returns {@code true} when invoked to force Informix to use
   * temporary storage when constructing bulk {@code UPDATE}
   * statements.
   *
   * @return {@code true} in all cases
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  public boolean shouldAlwaysUseTempStorageForModifyAll() {
    return true;
  }

  /**
   * Returns {@code true} when invoked to indicate that Informix does
   * indeed support <em>local temporary tables</em>.
   *
   * <p><em>local</em> is defined by EclipseLink in {@link
   * org.eclipse.persistence.internal.databaseaccess.DatabasePlatform}
   * to mean:</p>
   *
   * <blockquote>"Local" means that several threads may create
   * temporary tables with the same name.
   * Local temporary table is created in the beginning of {@link UpdateAllQuery}
   * execution and dropped in the end of it.</blockquote>
   *
   * @return {@code true} when invoked
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  public boolean supportsLocalTempTables() {
    return true;
  }

  /**
   * Returns {@code CREATE TEMP TABLE} when invoked, per
   * the <a
   * href="http://publib.boulder.ibm.com/infocenter/idshelp/v117/topic/com.ibm.sqls.doc/ids_sqs_0571.htm">Informix
   * 11.70 Information Center documentation</a>.
   *
   * <p>While Informix 11.70 supports an additional {@code IF NOT
   * EXISTS} clause, Informix 11.50 does not, so this method does not
   * add the {@code IF NOT EXISTS} clause.  In practice this should be
   * fine as temporary tables are scoped to the Informix session, and
   * EclipseLink takes care of cleaning up any temporary tables that
   * it creates.</p>
   *
   * @return {@code CREATE TEMP TABLE} when invoked
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  protected String getCreateTempTableSqlPrefix() {
    return "CREATE TEMP TABLE ";
  }

  /**
   * Overrides the superclass implementation to return a new {@link
   * DatabaseTable} with no {@linkplain
   * DatabaseTable#getTableQualifier()}.  Informix 11.50 and above do
   * not support table qualifiers of any kind on temporary tables.
   *
   * @param table the {@link DatabaseTable} for which a temporary
   * {@link DatabaseTable} should be returned; must not be {@code
   * null}
   *
   * @return a new {@link DatabaseTable} with no {@linkplain
   * DatabaseTable#getTableQualifier() table qualifier}
   *
   * @exception IllegalArgumentException if {@code table} is {@code
   * null}
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402180">EclipseLink
   * bug 402180</a>
   */
  @Override
  public DatabaseTable getTempTableForTable(final DatabaseTable table) {
    if (table == null) {
      throw new IllegalArgumentException("table", new NullPointerException("table"));
    }
    return new DatabaseTable("TL_" + table.getName(), "" /* no table qualifier */, table.shouldUseDelimiters(), this.getStartDelimiter(), this.getEndDelimiter());
  }

  /**
   * Returns {@code WITH NO LOG} when invoked, per the <a
   * href="http://publib.boulder.ibm.com/infocenter/idshelp/v117/topic/com.ibm.sqls.doc/ids_sqs_0571.htm">Informix
   * 11.70 Information Center documentation</a>, since transactions
   * are not needed on temp tables for the purposes for which
   * EclipseLink uses them.
   *
   * @return {@code WITH NO LOG} when invoked
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  protected String getCreateTempTableSqlSuffix() {
    return " WITH NO LOG";
  }

  /**
   * Overrides {@link
   * org.eclipse.persistence.internal.databaseaccess.DatabasePlatform#writeUpdateOriginalFromTempTableSql(Writer,
   * DatabaseTable, Collection, Collection)} to ensure that a {@code
   * SET} clause that is supposed to set a value for only one column
   * does not wrap that column name in parentheses.
   *
   * <p>The code for this method is identical to that of the stock
   * {@link H2Platform}, since the H2 database has the same
   * requirement.</p>
   *
   * @param writer the {@link Writer} that will be writing SQL; must
   * not be {@code null}
   *
   * @param table the {@link DatabaseTable} to be updated; must not be
   * {@code null}
   *
   * @param pkFields a {@link Collection} of {@link DatabaseField}s
   * each of which is a primary key of the table to be updated; must
   * not be {@code null}
   *
   * @param assignedFields a {@link Collection} of {@link
   * DatabaseField}s that are receiving updated values; must not be
   * {@code null}
   *
   * @exception IOException if an input/output error is thrown by the
   * supplied {@link Writer}
   *
   * @exception NullPointerException if any of the supplied parameters
   * is {@code null}
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=402037">EclipseLink
   * bug 402037</a>
   */
  @Override
  public void writeUpdateOriginalFromTempTableSql(final Writer writer, final DatabaseTable table, final Collection pkFields, final Collection assignedFields) throws IOException {
    writer.write("UPDATE ");
    final String tableName = table.getQualifiedNameDelimited(this);
    writer.write(tableName);
    writer.write(" SET ");
    final int size = assignedFields.size();
    if (size > 1) {
      writer.write("(");
    }
    writeFieldsList(writer, assignedFields, this);
    if (size > 1) {
      writer.write(")");
    }
    writer.write(" = (SELECT ");
    writeFieldsList(writer, assignedFields, this);
    writer.write(" FROM ");
    final String tempTableName = this.getTempTableForTable(table).getQualifiedNameDelimited(this);
    writer.write(tempTableName);
    writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
    writer.write(") WHERE EXISTS(SELECT ");
    writer.write(((DatabaseField)pkFields.iterator().next()).getNameDelimited(this));
    writer.write(" FROM ");
    writer.write(tempTableName);
    writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
    writer.write(")");
  }

  /**
   * Overrides {@link
   * org.eclipse.persistence.internal.databaseaccess.DatabasePlatform#getObjectFromResultSet(ResultSet,
   * int, int, AbstractSession)} so that if the supplied {@code
   * jdbcType} is equal to {@link Types#LONGVARCHAR}, then the {@link
   * ResultSet#getString(int)} method (instead of the {@link
   * ResultSet#getObject(int)} method) is used to fetch the "raw"
   * value of the column indicated by the supplied {@code
   * columnNumber} from the supplied {@link ResultSet}.
   *
   * <p>This works around an issue with the Informix JDBC driver,
   * version 3.70.JC7 and earlier, where a {@code TEXT} column is
   * reported to be mappable to the JDBC type of {@link
   * Types#LONGVARCHAR LONGVARCHAR}, but the actual object returned by
   * {@link ResultSet#getObject(int)} is a {@code byte[]} (instead of
   * either a {@code char[]} or a {@link String}).  Invoking {@link
   * ResultSet#getString(int)} instead returns the desired result.</p>
   *
   * @param resultSet the {@link ResultSet} to {@linkplain
   * ResultSet#getObject(int) get an <code>Object</code>} from; may be
   * {@code null}
   *
   * @param columnNumber the {@code 1}-based column number indicating
   * the column to use
   *
   * @param jdbcType an {@code int} indicating the {@linkplain Types
   * JDBC type} of which the column in the {@link ResultSet} indicated
   * by the supplied {@code columnNumber} is expected to be
   *
   * @param session an {@link AbstractSession} that, when used at all,
   * is passed to the superclass implementation of this method; may be
   * {@code null}
   *
   * @return the {@link Object} resulting from a call to {@link
   * ResultSet#getObject(int)}; may be {@code null}
   *
   * @exception SQLException if any error occurs
   *
   * @see <a
   * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=405645">EclipseLink
   * bug 405645</a>
   */
  @Override
  public Object getObjectFromResultSet(final ResultSet resultSet, final int columnNumber, final int jdbcType, final AbstractSession session) throws SQLException {
    Object returnValue = null;
    switch (jdbcType) {
    case Types.LONGVARCHAR:
      if (resultSet != null) {
        returnValue = resultSet.getString(columnNumber);
      }
      break;
    default:
      returnValue = super.getObjectFromResultSet(resultSet, columnNumber, jdbcType, session);
    }
    return returnValue;
  }

}

