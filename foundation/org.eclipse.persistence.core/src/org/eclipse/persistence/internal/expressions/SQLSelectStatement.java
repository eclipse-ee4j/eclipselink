/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
//     04/30/2014-2.6 Lukas Jungmann
//       - 380101: Invalid MySQL SQL syntax in query with LIMIT and FOR UPDATE
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.internal.expressions;

import static org.eclipse.persistence.queries.ReadAllQuery.Direction.CHILD_TO_PARENT;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.FunctionField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.history.DecoratedDatabaseTable;
import org.eclipse.persistence.internal.history.UniversalAsOfClause;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.SQLCall;

/**
 * <p><b>Purpose</b>: Print SELECT statement.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print SELECT statement.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public class SQLSelectStatement extends SQLStatement {

    /** Query this statement is associated to (used for SQL query options). */
    protected ReadQuery query;
    /** Flag used to indicate field names should use unique aliases */
    protected boolean useUniqueFieldAliases;
    /** Counter to generate unique alias names */
    protected int fieldCounter=0;

    /** Fields being selected (can include expressions). */
    protected Vector fields;

    /** Fields not being selected (can include expressions). */
    protected List<Object> nonSelectFields;

    /** Tables being selected from. */
    protected List<DatabaseTable> tables;

    /** Used for "Select Distinct" option. */
    protected short distinctState;

    /** Order by clause for read all queries. */
    protected List<Expression> orderByExpressions;

    /** Group by clause for report queries. */
    protected List<Expression> groupByExpressions;

    /** Union clause. */
    protected List<Expression> unionExpressions;

    /** Having clause for report queries. */
    protected Expression havingExpression;

    /** Used for pessimistic locking ie. "For Update". */
    protected ForUpdateClause forUpdateClause;

    /** Used for report query or counts so we know how to treat distincts. */
    protected boolean isAggregateSelect;

    /** Used for DB2 style from clause outer joins. */
    protected List<OuterJoinExpressionHolder> outerJoinExpressionHolders;

    /** Used for Oracle Hierarchical Queries */
    protected Expression startWithExpression;
    protected Expression connectByExpression;
    protected List<Expression> orderSiblingsByExpressions;
    protected ReadAllQuery.Direction direction;

    /** Variables used for aliasing and normalizing. */
    protected boolean requiresAliases;
    protected Map<DatabaseTable, DatabaseTable> tableAliases;
    protected DatabaseTable lastTable;
    protected DatabaseTable currentAlias;
    protected int currentAliasNumber;

    /** Used for subselects. */
    protected SQLSelectStatement parentStatement;

    /** It is used by subselect to re-normalize joins */
    protected Map<Expression, Expression> optimizedClonedExpressions;

    /** Used for caching the field alias written to the query */
    protected Map<DatabaseField, String> fieldAliases;
    protected boolean shouldCacheFieldAliases;

    public SQLSelectStatement() {
        this.fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        this.tables = new ArrayList(4);
        this.requiresAliases = false;
        this.useUniqueFieldAliases=false;
        this.isAggregateSelect = false;
        this.distinctState = ObjectLevelReadQuery.UNCOMPUTED_DISTINCT;
        this.currentAliasNumber = 0;
    }

    public void addField(DatabaseField field) {
        getFields().addElement(field);
    }

    /**
     * INTERNAL:  adds an expression to the fields.  set a flag if the expression
     * is for and aggregate function.
     */
    public void addField(Expression expression) {
        if (expression instanceof FunctionExpression) {
            if (((FunctionExpression)expression).getOperator().isAggregateOperator()) {
                setIsAggregateSelect(true);
            }
        }

        getFields().add(expression);
    }

    /**
     * When distinct is used with order by the ordered fields must be in the select clause.
     */
    protected void addOrderByExpressionToSelectForDistinct() {
        for (Expression orderExpression : getOrderByExpressions()) {
            Expression fieldExpression = orderExpression;

            while (fieldExpression.isFunctionExpression() && (fieldExpression.getOperator().isOrderOperator())) {
                fieldExpression = ((FunctionExpression)fieldExpression).getBaseExpression();
            }

            // Changed to call a method to loop through the fields vector and check each element
            // individually. Jon D. May 4, 2000 for pr 7811
            if ((fieldExpression.selectIfOrderedBy()) && !fieldsContainField(getFields(), fieldExpression)) {
                addField(fieldExpression);
            }
        }
    }

    /**
     * Add a table to the statement. The table will
     * be used in the FROM part of the SQL statement.
     */
    public void addTable(DatabaseTable table) {
        if (!getTables().contains(table)) {
            getTables().add(table);
        }
    }

    /**
     * ADVANCED:
     * If a platform is Informix, then the outer join must be in the FROM clause.
     * This is used internally by EclipseLink for building Informix outer join syntax which differs from
     * other platforms (Oracle,Sybase) that print the outer join in the WHERE clause and from DB2 which prints
     * the OuterJoinedAliases passed in to keep track of tables used for outer join so no normal join is given.
     * This syntax is old for Informix, so should probably be removed.
     */
    public void appendFromClauseForInformixOuterJoin(ExpressionSQLPrinter printer, List<DatabaseTable> outerJoinedAliases) throws IOException {
        Writer writer = printer.getWriter();

        // Print outer joins
        boolean firstTable = true;

        for (OuterJoinExpressionHolder holder : getOuterJoinExpressionsHolders()) {
            QueryKeyExpression outerExpression = (QueryKeyExpression)holder.joinExpression;
            CompoundExpression relationExpression = (CompoundExpression)holder.outerJoinedMappingCriteria;// get expression for multiple table case

            // CR#3083929 direct collection/map mappings do not have reference descriptor.
            DatabaseTable targetTable = null;
            if (outerExpression.getMapping().isDirectCollectionMapping()) {
                targetTable = ((DirectCollectionMapping)outerExpression.getMapping()).getReferenceTable();
            } else {
                targetTable = outerExpression.getMapping().getReferenceDescriptor().getTables().get(0);
            }
            // Grab the source table from the mapping not just the first table
            // from the descriptor. In an joined inheritance hierarchy, the
            // fk used in the outer join may be from a subclasses's table .
            DatabaseTable sourceTable;
            if (outerExpression.getMapping().isObjectReferenceMapping() && ((ObjectReferenceMapping) outerExpression.getMapping()).isForeignKeyRelationship()) {
                sourceTable = (outerExpression.getMapping().getFields().get(0)).getTable();
            } else {
                sourceTable = ((ObjectExpression)outerExpression.getBaseExpression()).getDescriptor().getTables().get(0);
            }

            DatabaseTable sourceAlias = outerExpression.getBaseExpression().aliasForTable(sourceTable);
            DatabaseTable targetAlias = outerExpression.aliasForTable(targetTable);

            if (!(outerJoinedAliases.contains(sourceAlias) || outerJoinedAliases.contains(targetAlias))) {
                if (!firstTable) {
                    writer.write(", ");
                }

                firstTable = false;
                writer.write(sourceTable.getQualifiedNameDelimited(printer.getPlatform()));
                outerJoinedAliases.add(sourceAlias);
                writer.write(" ");
                writer.write(sourceAlias.getQualifiedNameDelimited(printer.getPlatform()));

                if (outerExpression.getMapping().isManyToManyMapping()) {// for many to many mappings, you need to do some funky stuff to get the relation table's alias
                    DatabaseTable newTarget = ((ManyToManyMapping)outerExpression.getMapping()).getRelationTable();
                    DatabaseTable newAlias = relationExpression.aliasForTable(newTarget);
                    writer.write(", OUTER ");// need to outer join only to relation table for many-to-many case in Informix
                    writer.write(newTarget.getQualifiedNameDelimited(printer.getPlatform()));
                    writer.write(" ");
                    outerJoinedAliases.add(newAlias);
                    writer.write(newAlias.getQualifiedNameDelimited(printer.getPlatform()));
                } else if (outerExpression.getMapping().isDirectCollectionMapping()) {// for many to many mappings, you need to do some funky stuff to get the relation table's alias
                    DatabaseTable newTarget = ((DirectCollectionMapping)outerExpression.getMapping()).getReferenceTable();
                    DatabaseTable newAlias = relationExpression.aliasForTable(newTarget);
                    writer.write(", OUTER ");
                    writer.write(newTarget.getQualifiedNameDelimited(printer.getPlatform()));
                    writer.write(" ");
                    outerJoinedAliases.add(newAlias);
                    writer.write(newAlias.getQualifiedNameDelimited(printer.getPlatform()));
                } else {// do normal outer stuff for Informix
                    for (Enumeration target = outerExpression.getMapping().getReferenceDescriptor().getTables().elements();
                             target.hasMoreElements();) {
                        DatabaseTable newTarget = (DatabaseTable)target.nextElement();
                        DatabaseTable newAlias = outerExpression.aliasForTable(newTarget);
                        writer.write(", OUTER ");
                        writer.write(newTarget.getQualifiedNameDelimited(printer.getPlatform()));
                        writer.write(" ");
                        outerJoinedAliases.add(newAlias);
                        writer.write(newAlias.getQualifiedNameDelimited(printer.getPlatform()));
                    }
                }
            }
        }
    }

    /**
     * ADVANCED:
     * Appends the SQL standard outer join clause, and some variation per platform.
     * Most platforms use this syntax, support is also offered for Oracle to join in the where clause (although it should use the FROM clause as the WHERE clause is obsolete).
     * This is also used for inner joins when configured in the platform.
     */
    public void appendFromClauseForOuterJoin(ExpressionSQLPrinter printer, List<DatabaseTable> outerJoinedAliases, Collection aliasesOfTablesToBeLocked, boolean shouldPrintUpdateClauseForAllTables) throws IOException {
        Writer writer = printer.getWriter();
        AbstractSession session = printer.getSession();
        DatabasePlatform platform = session.getPlatform();

        // Print outer joins
        boolean firstTable = true;
        boolean requiresEscape = false; // Checks if the JDBC closing escape syntax is needed.

        boolean usesHistory = (getBuilder() != null) && getBuilder().hasAsOfClause();

        int nSize = getOuterJoinExpressionsHolders().size();
        for (OuterJoinExpressionHolder holder : getOuterJoinExpressionsHolders()) {
            holder.process(usesHistory);
        }

        if(nSize > 1) {
            sortOuterJoinExpressionHolders(getOuterJoinExpressionsHolders());
        }

        for (OuterJoinExpressionHolder holder : outerJoinExpressionHolders) {
            ObjectExpression outerExpression = holder.joinExpression;
            boolean isOuterJoin = (outerExpression ==  null) || outerExpression.shouldUseOuterJoin();
            DatabaseTable targetTable = holder.targetTable;
            DatabaseTable sourceTable = holder.sourceTable;
            DatabaseTable sourceAlias = holder.sourceAlias;
            DatabaseTable targetAlias = holder.targetAlias;

            if (!outerJoinedAliases.contains(targetAlias)) {
                if (!outerJoinedAliases.contains(sourceAlias)) {
                    if (requiresEscape && session.getPlatform().shouldUseJDBCOuterJoinSyntax()) {
                        writer.write("}");
                    }
                    if (!firstTable) {
                        writer.write(",");
                    }
                    if (platform.shouldUseJDBCOuterJoinSyntax()) {
                        writer.write(platform.getJDBCOuterJoinString());
                    }
                    requiresEscape = true;
                    firstTable = false;
                    sourceTable.printSQL(printer);
                    outerJoinedAliases.add(sourceAlias);
                    writer.write(" ");
                    if (sourceAlias.isDecorated()) {
                        ((DecoratedDatabaseTable)sourceAlias).getAsOfClause().printSQL(printer);
                        writer.write(" ");
                    }
                    sourceAlias.printSQL(printer);
                    printForUpdateClauseOnJoin(sourceAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                }

                if (outerExpression == null) {
                    holder.printAdditionalJoins(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
                } else {
                    DatabaseTable relationTable = outerExpression.getRelationTable();
                    boolean hasAdditionalJoinExpressions = holder.hasAdditionalJoinExpressions();
                    boolean isMapKeyObject = holder.hasMapKeyHolder();
                    Expression additionalOnExpression = outerExpression.getOnClause();
                    if (relationTable == null) {
                        if (outerExpression.isDirectCollection()) {
                            // Append the join clause,
                            // If this is a direct collection, join to direct table.
                            Expression onExpression = holder.outerJoinedMappingCriteria;

                            DatabaseTable newAlias = onExpression.aliasForTable(targetTable);
                            if (isOuterJoin) {
                                writer.write(" LEFT OUTER JOIN ");
                            } else {
                                writer.write(" JOIN ");
                            }
                            targetTable.printSQL(printer);
                            writer.write(" ");
                            if (newAlias.isDecorated()) {
                                ((DecoratedDatabaseTable)newAlias).getAsOfClause().printSQL(printer);
                                writer.write(" ");
                            }
                            outerJoinedAliases.add(newAlias);
                            newAlias.printSQL(printer);
                            printForUpdateClauseOnJoin(newAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                            printOnClause(onExpression.and(additionalOnExpression), printer, platform);
                        } else {
                            // Must outerjoin each of the targets tables.
                            // The first table is joined with the mapping join criteria,
                            // the rest of the tables are joined with the additional join criteria.
                            if (isOuterJoin) {
                                writer.write(" LEFT OUTER JOIN ");
                            } else {
                                writer.write(" JOIN ");
                            }
                            if (hasAdditionalJoinExpressions && platform.supportsNestingOuterJoins()) {
                                writer.write("(");
                            }
                            targetTable.printSQL(printer);
                            writer.write(" ");
                            if (targetAlias.isDecorated()) {
                                ((DecoratedDatabaseTable)targetAlias).getAsOfClause().printSQL(printer);
                                writer.write(" ");
                            }
                            outerJoinedAliases.add(targetAlias);
                            targetAlias.printSQL(printer);
                            printForUpdateClauseOnJoin(targetAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                            if (hasAdditionalJoinExpressions && platform.supportsNestingOuterJoins()) {
                                holder.printAdditionalJoins(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
                                writer.write(")");
                            }
                            Expression sourceToTargetJoin = holder.outerJoinedMappingCriteria;
                            if (additionalOnExpression != null) {
                                if (sourceToTargetJoin == null) {
                                    sourceToTargetJoin = additionalOnExpression;
                                } else {
                                    sourceToTargetJoin = sourceToTargetJoin.and(additionalOnExpression);
                                }
                            }
                            printOnClause(sourceToTargetJoin, printer, platform);
                            if (hasAdditionalJoinExpressions && !platform.supportsNestingOuterJoins()) {
                                holder.printAdditionalJoins(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
                            }
                        }
                    } else {
                        // Bug#4240751 Treat ManyToManyMapping separately for out join
                        // Must outer join each of the targets tables.
                        // The first table is joined with the mapping join criteria,
                        // the rest of the tables are joined with the additional join criteria.
                        // For example: EMPLOYEE t1 LEFT OUTER JOIN (PROJ_EMP t3 LEFT OUTER JOIN PROJECT t0 ON (t0.PROJ_ID = t3.PROJ_ID)) ON (t3.EMP_ID = t1.EMP_ID)
                        // Now OneToOneMapping also may have relation table.
                        DatabaseTable relationAlias = holder.outerJoinedMappingCriteria.aliasForTable(relationTable);
                        DatabaseTable mapKeyAlias = null;
                        DatabaseTable mapKeyTable = null;
                        List<DatabaseTable> tablesInOrder = new ArrayList();
                        // glassfish issue 2440: store aliases instead of tables
                        // in the tablesInOrder. This allows to distinguish source
                        // and target table in case of an self referencing relationship.
                        tablesInOrder.add(sourceAlias);
                        tablesInOrder.add(relationAlias);
                        tablesInOrder.add(targetAlias);
                        if (isMapKeyObject) {
                            // Need to also join the map key key.
                            mapKeyAlias = holder.mapKeyHolder.targetAlias;
                            mapKeyTable = holder.mapKeyHolder.targetTable;
                            tablesInOrder.add(mapKeyAlias);
                        }
                        TreeMap indexToExpressionMap = new TreeMap();
                        mapTableIndexToExpression(holder.outerJoinedMappingCriteria, indexToExpressionMap, tablesInOrder);
                        Expression sourceToRelationJoin = (Expression)indexToExpressionMap.get(Integer.valueOf(1));
                        Expression relationToTargetJoin = (Expression)indexToExpressionMap.get(Integer.valueOf(2));
                        Expression relationToKeyJoin = null;
                        if (isMapKeyObject) {
                            relationToKeyJoin = (Expression)indexToExpressionMap.get(Integer.valueOf(3));
                        }

                        if (outerExpression.shouldUseOuterJoin()) {
                            writer.write(" LEFT OUTER JOIN ");
                        } else {
                            writer.write(" JOIN ");
                        }
                        if (platform.supportsNestingOuterJoins()) {
                            writer.write("(");
                        }
                        relationTable.printSQL(printer);
                        writer.write(" ");
                        if (relationAlias.isDecorated()) {
                            ((DecoratedDatabaseTable)relationAlias).getAsOfClause().printSQL(printer);
                            writer.write(" ");
                        }
                        outerJoinedAliases.add(relationAlias);
                        relationAlias.printSQL(printer);
                        printForUpdateClauseOnJoin(relationAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                        if (!platform.supportsNestingOuterJoins()) {
                            printOnClause(sourceToRelationJoin.and(additionalOnExpression), printer, platform);
                        }

                        if (isMapKeyObject) {
                            // Append join to map key.
                            if (isOuterJoin && !session.getPlatform().supportsANSIInnerJoinSyntax()) {
                                writer.write(" LEFT OUTER");
                            }
                            writer.write(" JOIN ");
                            mapKeyTable.printSQL(printer);
                            writer.write(" ");
                            if (mapKeyAlias.isDecorated()) {
                                ((DecoratedDatabaseTable)mapKeyAlias).getAsOfClause().printSQL(printer);
                                writer.write(" ");
                            }
                            outerJoinedAliases.add(mapKeyAlias);
                            mapKeyAlias.printSQL(printer);
                            printForUpdateClauseOnJoin(mapKeyAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                            printOnClause(relationToKeyJoin.and(additionalOnExpression), printer, platform);

                            if (holder.mapKeyHolder.hasAdditionalJoinExpressions()) {
                                holder.mapKeyHolder.printAdditionalJoins(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
                            }
                        }

                        if (isOuterJoin && !session.getPlatform().supportsANSIInnerJoinSyntax()) {
                            // if the DB does not support 'JOIN', do a left outer
                            // join instead. This will give the same result because
                            // the left table is a join table and has therefore
                            // no rows that are not in the right table.
                            writer.write(" LEFT OUTER");
                        }
                        writer.write(" JOIN ");
                        targetTable.printSQL(printer);
                        writer.write(" ");
                        if (targetAlias.isDecorated()) {
                            ((DecoratedDatabaseTable)targetAlias).getAsOfClause().printSQL(printer);
                            writer.write(" ");
                        }
                        outerJoinedAliases.add(targetAlias);
                        targetAlias.printSQL(printer);
                        printForUpdateClauseOnJoin(targetAlias, printer, shouldPrintUpdateClauseForAllTables, aliasesOfTablesToBeLocked, platform);
                        printOnClause(relationToTargetJoin, printer, platform);

                        if (hasAdditionalJoinExpressions) {
                            holder.printAdditionalJoins(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
                        }
                        if (platform.supportsNestingOuterJoins()) {
                            writer.write(")");
                            printOnClause(sourceToRelationJoin, printer, platform);
                        }
                    }
                }
            }
        }

        if (requiresEscape && session.getPlatform().shouldUseJDBCOuterJoinSyntax()) {
            writer.write("}");
        }
    }

    /**
     * Print the outer join ON clause.
     * Some databases do not allow brackets.
     */
    protected void printOnClause(Expression onClause, ExpressionSQLPrinter printer, DatabasePlatform platform) throws IOException {
        printer.getWriter().write(" ON ");
        if (!platform.supportsOuterJoinsWithBrackets()) {
            ((RelationExpression)onClause).printSQLNoParens(printer);
        } else {
            onClause.printSQL(printer);
        }
    }

    /**
     * Print the FOR UPDATE clause after each join if required.
     */
    protected void printForUpdateClauseOnJoin(DatabaseTable alias, ExpressionSQLPrinter printer, boolean shouldPrintUpdateClauseForAllTables, Collection aliasesOfTablesToBeLocked, DatabasePlatform platform) {
        if (shouldPrintUpdateClauseForAllTables || (aliasesOfTablesToBeLocked != null && aliasesOfTablesToBeLocked.remove(alias))) {
            getForUpdateClause().printSQL(printer, this);
        }
    }

    /**
     * Print the from clause.
     * This includes outer joins, these must be printed before the normal join to ensure that the source tables are not joined again.
     * Outer joins are not printed in the FROM clause on Oracle or Sybase.
     */
    public void appendFromClauseToWriter(ExpressionSQLPrinter printer) throws IOException {
        Writer writer = printer.getWriter();
        AbstractSession session = printer.getSession();
        writer.write(" FROM ");

        // Print outer joins
        boolean firstTable = true;
        List<DatabaseTable> outerJoinedAliases = new ArrayList(4); // Must keep track of tables used for outer join so no normal join is given

        // prepare to lock tables if required
        boolean shouldPrintUpdateClause = printer.getPlatform().shouldPrintForUpdateClause()
                && !printer.getPlatform().shouldPrintLockingClauseAfterWhereClause()
                && (getForUpdateClause() != null);
        Collection aliasesOfTablesToBeLocked = null;
        boolean shouldPrintUpdateClauseForAllTables = false;
        if (shouldPrintUpdateClause) {
            aliasesOfTablesToBeLocked = getForUpdateClause().getAliasesOfTablesToBeLocked(this);
            shouldPrintUpdateClauseForAllTables = aliasesOfTablesToBeLocked.size() == getTableAliases().size();
        }

        if (hasOuterJoinExpressions()) {
            if (session.getPlatform().isInformixOuterJoin()) {
                appendFromClauseForInformixOuterJoin(printer, outerJoinedAliases);
            } else if (!session.getPlatform().shouldPrintOuterJoinInWhereClause() || !session.getPlatform().shouldPrintInnerJoinInWhereClause(query)) {
                appendFromClauseForOuterJoin(printer, outerJoinedAliases, aliasesOfTablesToBeLocked, shouldPrintUpdateClauseForAllTables);
            }
            firstTable = false;
        }

        // If there are no table aliases it means the query was malformed,
        // most likely the wrong builder was used, or wrong builder on the left in a sub-query.
        if (getTableAliases().isEmpty()) {
            throw QueryException.invalidBuilderInQuery(null);// Query is set in execute.
        }

        // Print tables for normal join
        for (DatabaseTable alias : getTableAliases().keySet()) {
            if (!outerJoinedAliases.contains(alias)) {
                DatabaseTable table = getTableAliases().get(alias);
                if (requiresAliases()) {
                    if (!firstTable) {
                        writer.write(", ");
                    }
                    firstTable = false;
                    table.printSQL(printer);
                    writer.write(" ");
                    if (alias.isDecorated()) {
                        ((DecoratedDatabaseTable)alias).getAsOfClause().printSQL(printer);
                        writer.write(" ");
                    }
                    alias.printSQL(printer);
                } else {
                    table.printSQL(printer);

                    if (alias.isDecorated()) {
                        writer.write(" ");
                        ((DecoratedDatabaseTable)alias).getAsOfClause().printSQL(printer);
                    }
                }
                if (shouldPrintUpdateClause) {
                    if (shouldPrintUpdateClauseForAllTables || aliasesOfTablesToBeLocked.remove(alias)) {
                        getForUpdateClause().printSQL(printer, this);
                    }
                }
            }
        }
    }

    /**
     * This method will append the group by clause to the end of the
     * select statement.
     */
    public void appendGroupByClauseToWriter(ExpressionSQLPrinter printer) throws IOException {
        if (getGroupByExpressions().isEmpty()) {
            return;
        }

        printer.getWriter().write(" GROUP BY ");

        Vector newFields = new Vector();
        // to avoid printing a comma before the first field
        printer.setIsFirstElementPrinted(false);
        for (Expression expression : getGroupByExpressions()) {
      //      if (expression.isObjectExpression() && ((ObjectExpression)expression).getDescriptor() != null){
                //in the case where the user is grouping by an entity we need to change this to the PKs
//               for (String field : ((ObjectExpression)expression).getDescriptor().getPrimaryKeyFieldNames()){
//                   writeFieldsFromExpression(printer, expression.getField(field), newFields);
    //           }
  //          }else{
                writeFieldsFromExpression(printer, expression, newFields);
       //     }
        }
    }

    /**
     * This method will append the Hierarchical Query Clause to the end of the
     * select statement
     */
    public void appendHierarchicalQueryClauseToWriter(ExpressionSQLPrinter printer) throws IOException {
        Expression startWith = getStartWithExpression();
        Expression connectBy = getConnectByExpression();
        List<Expression> orderSiblingsBy = getOrderSiblingsByExpressions();

        //Create the START WITH CLAUSE
        if (startWith != null) {
            printer.getWriter().write(" START WITH ");
            startWith.printSQL(printer);
        }

        if (connectBy != null) {
            if (!connectBy.isQueryKeyExpression()) {
                throw QueryException.illFormedExpression(connectBy);
            }

            printer.getWriter().write(" CONNECT BY ");

            DatabaseMapping mapping = ((QueryKeyExpression)connectBy).getMapping();
            ClassDescriptor descriptor = mapping.getDescriptor();

            //only works for these kinds of mappings. The data isn't hierarchical otherwise
            //Should also check that the source class and target class are the same.
            Map foreignKeys = null;

            if (mapping.isOneToManyMapping()) {
                OneToManyMapping otm = (OneToManyMapping)mapping;
                foreignKeys = otm.getTargetForeignKeyToSourceKeys();
            } else if (mapping.isOneToOneMapping()) {
                OneToOneMapping oto = (OneToOneMapping)mapping;
                foreignKeys = oto.getSourceToTargetKeyFields();
            } else if (mapping.isAggregateCollectionMapping()) {
                AggregateCollectionMapping acm = (AggregateCollectionMapping)mapping;
                foreignKeys = acm.getTargetForeignKeyToSourceKeys();
            } else {
                throw QueryException.invalidQueryKeyInExpression(connectBy);
            }

            DatabaseTable defaultTable = descriptor.getDefaultTable();
            String tableName = "";

            //determine which table name to use
            if (requiresAliases()) {
                tableName = getBuilder().aliasForTable(defaultTable).getName();
            } else {
                tableName = defaultTable.getNameDelimited(printer.getPlatform());
            }

            if ((foreignKeys != null) && !foreignKeys.isEmpty()) {
                //get the source and target fields.
                Iterator sourceKeys = foreignKeys.keySet().iterator();

                //for each source field, get the target field and create the link. If there's
                //only one, use the simplest version without ugly bracets
                if (foreignKeys.size() > 1) {
                    printer.getWriter().write("((");
                }

                DatabaseField source = (DatabaseField)sourceKeys.next();
                DatabaseField target = (DatabaseField)foreignKeys.get(source);

                ReadAllQuery.Direction direction = getDirection() != null ? getDirection() : ReadAllQuery.Direction.getDefault(mapping);
                if (direction == CHILD_TO_PARENT) {
                    printer.getWriter().write("PRIOR " + tableName + "." + source.getNameDelimited(printer.getPlatform()));
                    printer.getWriter().write(" = " + tableName + "." + target.getNameDelimited(printer.getPlatform()));
                } else {
                    printer.getWriter().write(tableName + "." + source.getNameDelimited(printer.getPlatform()));
                    printer.getWriter().write(" = PRIOR " + tableName + "." + target.getNameDelimited(printer.getPlatform()));
                }

                while (sourceKeys.hasNext()) {
                    printer.getWriter().write(") AND (");
                    source = (DatabaseField)sourceKeys.next();
                    target = (DatabaseField)foreignKeys.get(source);

                    if (direction == CHILD_TO_PARENT) {
                        printer.getWriter().write("PRIOR " + tableName + "." + source.getNameDelimited(printer.getPlatform()));
                        printer.getWriter().write(" = " + tableName + "." + target.getNameDelimited(printer.getPlatform()));
                    } else {
                        printer.getWriter().write(tableName + "." + source.getNameDelimited(printer.getPlatform()));
                        printer.getWriter().write(" = PRIOR " + tableName + "." + target.getNameDelimited(printer.getPlatform()));
                    }
                }

                if (foreignKeys.size() > 1) {
                    printer.getWriter().write("))");
                }
            }
        }

        if ((orderSiblingsBy != null) && !orderSiblingsBy.isEmpty()) {
            printer.getWriter().write(" ORDER SIBLINGS BY ");

            for (Iterator<Expression> iterator = orderSiblingsBy.iterator(); iterator.hasNext(); ) {
                Expression expression = iterator.next();
                expression.printSQL(printer);

                if (iterator.hasNext()) {
                    printer.getWriter().write(", ");
                }
            }
        }
    }

    /**
     * This method will append the order clause to the end of the
     * select statement.
     */
    public void appendOrderClauseToWriter(ExpressionSQLPrinter printer) throws IOException {
        if (!hasOrderByExpressions()) {
            return;
        }

        printer.getWriter().write(" ORDER BY ");

        for (Iterator<Expression> expressionsEnum = getOrderByExpressions().iterator(); expressionsEnum.hasNext();) {
            Expression expression = expressionsEnum.next();

            /*
             *  Allow the platform to indicate if they support parameter expressions in the ORDER BY clause 
             *  as a whole, regardless if individual functions allow binding. We make that decision here 
             *  before we continue parsing into generic API calls
             */
            if(!printer.getPlatform().supportsOrderByParameters()) {
                if(printer.getPlatform().shouldBindPartialParameters()) {
                    if(expression.isParameterExpression()) {
                        ((ParameterExpression) expression).setCanBind(false);
                    } else if(expression.isConstantExpression() && printer.getPlatform().shouldBindLiterals()) {
                        ((ConstantExpression) expression).setCanBind(false);
                    }
                } else if (printer.getPlatform().isDynamicSQLRequiredForFunctions()) {
                    if(expression.isParameterExpression() 
                            || (expression.isConstantExpression() && printer.getPlatform().shouldBindLiterals())) {
                        printer.getCall().setUsesBinding(false);
                    }
                }
            }

            expression.printSQL(printer);

            if (expressionsEnum.hasNext()) {
                printer.getWriter().write(", ");
            }
        }
    }

    /**
     * This method will append the union clause to the end of the
     * select statement.
     */
    public void appendUnionClauseToWriter(ExpressionSQLPrinter printer) throws IOException {
        if (!hasUnionExpressions()) {
            return;
        }

        for (Iterator expressionsEnum = getUnionExpressions().iterator(); expressionsEnum.hasNext();) {
            Expression expression = (Expression)expressionsEnum.next();
            printer.getWriter().write(" ");
            expression.printSQL(printer);
            printer.printString(")");
        }
    }

    /**
     * This method will append the for update clause to the end of the
     * select statement.
     */
    public void appendForUpdateClause(ExpressionSQLPrinter printer) {
        if (getForUpdateClause() != null) {
            getForUpdateClause().printSQL(printer, this);
        }
    }

    /**
     * INTERNAL: Alias the tables in all of our nodes.
     */
    public void assignAliases(Vector allExpressions) {
        // For sub-selects all statements must share aliasing information.
        // For  CR#2627019
        currentAliasNumber = getCurrentAliasNumber();

        ExpressionIterator iterator = new ExpressionIterator() {
            @Override
            public void iterate(Expression each) {
                currentAliasNumber = each.assignTableAliasesStartingAt(currentAliasNumber);
            }
        };

        if (allExpressions.isEmpty()) {
            // bug 3878553 - ensure aliases are always assigned for when required .
            if ((getBuilder() != null) && requiresAliases()) {
                getBuilder().assignTableAliasesStartingAt(currentAliasNumber);
            }
        } else {
            for (Enumeration expressionEnum = allExpressions.elements();
                     expressionEnum.hasMoreElements();) {
                Expression expression = (Expression)expressionEnum.nextElement();
                iterator.iterateOn(expression);
            }
        }

        // For sub-selects update aliasing information of all statements.
        // For  CR#2627019
        setCurrentAliasNumber(currentAliasNumber);
    }

    /**
     * Build the call, setting the query first, this is required in some cases when the query info is required to print the SQL.
     */
    public DatabaseCall buildCall(AbstractSession session, DatabaseQuery query) {
        SQLCall call = new SQLCall();
        call.setQuery(query);
        call.returnManyRows();

        Writer writer = new CharArrayWriter(200);

        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, requiresAliases(), getBuilder());
        printer.setWriter(writer);

        session.getPlatform().printSQLSelectStatement(call, printer, this);
        call.setSQLString(writer.toString());

        return call;
    }

    /**
     * Print the SQL representation of the statement on a stream.
     */
    @Override
    public DatabaseCall buildCall(AbstractSession session) {
        return buildCall(session, null);
    }

    /**
     * INTERNAL:
     * This is used by cursored stream to determine if an expression used distinct as the size must account for this.
     */
    public void computeDistinct() {
        ExpressionIterator iterator = new ExpressionIterator() {
            @Override
            public void iterate(Expression expression) {
                if (expression.isQueryKeyExpression() && ((QueryKeyExpression)expression).shouldQueryToManyRelationship()) {
                    // Aggregate should only use distinct as specified by the user.
                    if (!isDistinctComputed()) {
                        useDistinct();
                    }
                }
            }
        };

        if (getWhereClause() != null) {
            iterator.iterateOn(getWhereClause());
        }
    }

    public boolean isSubSelect() {
        return (getParentStatement() != null);
    }

    /**
     * INTERNAL:
     * It is used by subqueries to avoid duplicate joins.
     */
    public Map<Expression, Expression> getOptimizedClonedExpressions() {
        // Lazily Initialized only to be used by subqueries.
        if (optimizedClonedExpressions == null) {
            optimizedClonedExpressions = new IdentityHashMap<Expression, Expression>();
        }

        return optimizedClonedExpressions;
    }

    /**
     * INTERNAL:
     * It is used by subqueries to avoid duplicate joins.
     */
    public void addOptimizedClonedExpressions(Expression originalKey, Expression optimizedValue) {
        // Lazily Initialized only to be used by subqueries.
        if (optimizedClonedExpressions == null) {
            optimizedClonedExpressions = new IdentityHashMap<Expression, Expression>();
        }

        optimizedClonedExpressions.put(originalKey, optimizedValue);
    }

    /**
     * INTERNAL:
     * Computes all aliases which will appear in the FROM clause.
     */
    public void computeTables() {
        // Compute tables should never defer to computeTablesFromTables
        // This iterator will pull all the table aliases out of an expression, and
        // put them in a map.
        ExpressionIterator iterator = new ExpressionIterator() {
            @Override
            public void iterate(Expression each) {
                TableAliasLookup aliases = each.getTableAliases();

                if (aliases != null) {
                    // Insure that an aliased table is only added to a single
                    // FROM clause.
                    if (!aliases.haveBeenAddedToStatement()) {
                        aliases.addToMap((Map<DatabaseTable, DatabaseTable>)getResult());
                        aliases.setHaveBeenAddedToStatement(true);
                    }
                }
            }
        };

        iterator.setResult(new Hashtable(5));

        if (getWhereClause() != null) {
            iterator.iterateOn(getWhereClause());
        } else if (hasOuterJoinExpressions()) {
            Expression outerJoinCriteria = getOuterJoinExpressionsHolders().get(0).joinExpression;
            if (outerJoinCriteria != null){
                iterator.iterateOn(outerJoinCriteria);
            }
        }

        //Iterate on fields as well in that rare case where the select is not in the where clause
        for (Object field : getFields()) {
            if (field instanceof Expression) {
                iterator.iterateOn((Expression)field);
            }
        }

        //Iterate on non-selected fields as well in that rare case where the from is not in the where clause
        if (hasNonSelectFields()) {
            for (Object field : getNonSelectFields()) {
                if (field instanceof Expression) {
                    iterator.iterateOn((Expression)field);
                }
            }
        }

        // Always iterator on the builder, as the where clause may not contain the builder, i.e. value=value.
        iterator.iterateOn(getBuilder());

        Map<DatabaseTable, DatabaseTable> allTables = (Map<DatabaseTable, DatabaseTable>)iterator.getResult();
        setTableAliases(allTables);

        for (DatabaseTable table : allTables.values()) {
            addTable(table);
        }
    }

    /**
     * If there is no where clause, alias the tables from the tables list directly. Assume there's
     * no ambiguity
     */
    public void computeTablesFromTables() {
        Map<DatabaseTable, DatabaseTable> allTables = new Hashtable();
        AsOfClause asOfClause = null;

        if (getBuilder().hasAsOfClause() && !getBuilder().getSession().getProject().hasGenericHistorySupport()) {
            asOfClause = getBuilder().getAsOfClause();
        }

        for (int index = 0; index < getTables().size(); index++) {
            DatabaseTable next = getTables().get(index);

            // Aliases in allTables must now be decorated database tables.
            DatabaseTable alias = new DecoratedDatabaseTable("t" + (index), asOfClause);
            allTables.put(alias, next);
        }

        setTableAliases(allTables);
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void dontUseDistinct() {
        setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
    }

    /**
     * Check if the field from the field expression is already contained in the select clause of the statement.
     * This is used on order by expression when the field being ordered by must be in the select,
     * but cannot be in the select twice.
     */
    protected boolean fieldsContainField(List fields, Expression expression) {
        DatabaseField orderByField;

        if (expression instanceof DataExpression) {
            orderByField = ((DataExpression)expression).getField();
        } else {
            return false;
        }

        //check all fields for a match
        for (Object fieldOrExpression : fields) {
            if (fieldOrExpression instanceof DatabaseField) {
                DatabaseField field = (DatabaseField)fieldOrExpression;
                DataExpression exp = (DataExpression)expression;

                if (field.equals(orderByField)) {
                    // Ignore aggregates
                    while (((DataExpression)exp.getBaseExpression()).getMapping() instanceof AggregateObjectMapping) {
                        exp = (DataExpression)exp.getBaseExpression();
                    }
                    if (exp.getBaseExpression() == getBuilder()) {
                        // found a match
                        return true;
                    }
                }
            }
            // For CR#2589.  This method was not getting the fields in the same way that
            // printSQL does (i.e. using getFields() instead of getField()).
            // The problem was that getField() on an expression builder led to a null pointer
            // exception.
            else if (fieldOrExpression != null){
                Expression exp = (Expression)fieldOrExpression;
                DatabaseTable table = orderByField.getTable();

                if (exp.getFields().contains(orderByField) && (expression.aliasForTable(table).equals(exp.aliasForTable(table)))) {
                    //found a match
                    return true;
                }
            }
        }
        // no matches
        return false;
    }

    /**
     * Gets a unique id that will be used to alias the next table.
     * For sub-selects all must use this same aliasing information, maintained
     * in the root enclosing statement.  For CR#2627019
     */
    public int getCurrentAliasNumber() {
        if (getParentStatement() != null) {
            return getParentStatement().getCurrentAliasNumber();
        } else {
            return currentAliasNumber;
        }
    }

    /**
     * INTERNAL:
     * Return all the fields
     */
    public Vector getFields() {
        return fields;
    }

    protected ForUpdateClause getForUpdateClause() {
        return forUpdateClause;
    }

    /**
     * INTERNAL:
     * Return the group bys.
     */
    public List<Expression> getGroupByExpressions() {
        if (groupByExpressions == null) {
            groupByExpressions = new ArrayList<Expression>();
        }

        return groupByExpressions;
    }

    /**
     * INTERNAL:
     * Return the having expression.
     */
    public Expression getHavingExpression() {
        return havingExpression;
    }

    /**
     * INTERNAL:
     * Query held as it may store properties needed to generate the SQL
     */
    public ReadQuery getQuery() {
        return this.query;
    }

    /**
     * INTERNAL:
     * Return the StartWith expression
     */
    public Expression getStartWithExpression() {
        return startWithExpression;
    }

    /**
     * INTERNAL:
     * Return the CONNECT BY expression
     */
    public Expression getConnectByExpression() {
        return connectByExpression;
    }

    /**
     * INTERNAL:
     * Return the ORDER SIBLINGS BY expression
     */
    public List<Expression> getOrderSiblingsByExpressions() {
        return orderSiblingsByExpressions;
    }

    /**
     * INTERNAL:
     * @return the position of the PRIOR keyword
     */
    public ReadAllQuery.Direction getDirection() {
        return direction;
    }

    /**
     * INTERNAL:
     * Return the next value of fieldCounter
     */
    public int getNextFieldCounterValue(){
        return ++fieldCounter;
    }

    /**
     * Return the fields we don't want to select but want to join on.
     */
    public List<Object> getNonSelectFields() {
        return nonSelectFields;
    }

    /**
     * INTERNAL:
     * Return the order expressions for the query.
     */
    public List<Expression> getOrderByExpressions() {
        if (orderByExpressions == null) {
            orderByExpressions = new ArrayList(4);
        }

        return orderByExpressions;
    }

    public List<Expression> getUnionExpressions() {
        if (unionExpressions == null) {
            unionExpressions = new ArrayList(4);
        }
        return unionExpressions;
    }

    public void setUnionExpressions(List<Expression> unionExpressions) {
        this.unionExpressions = unionExpressions;
    }

    /**
     * INTERNAL:
     * returns outerJoinExpressionHolders representing outerjoin expressions.
     * @return
     */
    public List<OuterJoinExpressionHolder> getOuterJoinExpressionsHolders() {
        if (outerJoinExpressionHolders == null) {
            outerJoinExpressionHolders = new ArrayList(4);
        }

        return outerJoinExpressionHolders;
    }

    /**
     * INTERNAL:
     * Used by ExpressionBuilder and QueryKeyExpression normalization to create a standard outerjoin.
     * @param joinExpression - expression resulting in the outerjoin. Null if it is for inheritance reading of subclasses
     * @param outerJoinedMappingCriteria - used for querykey mapping expressions
     * @param outerJoinedAdditionalJoinCriteria - additional tables/expressions to join.  Usually for multitableInheritance join expressions
     * @param descriptor - descriptor to use if this is for reading in subclasses in one query.
     * @return
     */
    public Integer addOuterJoinExpressionsHolders(ObjectExpression joinExpression, Expression outerJoinedMappingCriteria,
            Map<DatabaseTable, Expression> outerJoinedAdditionalJoinCriteria, ClassDescriptor descriptor) {

        int index = getOuterJoinExpressionsHolders().size();
        OuterJoinExpressionHolder holder = new OuterJoinExpressionHolder(this, joinExpression, outerJoinedMappingCriteria,
                outerJoinedAdditionalJoinCriteria, descriptor);

        getOuterJoinExpressionsHolders().add(holder);
        return index;
    }

    /**
     * INTERNAL:
     * used by TREAT to add in a join from the parent table to the child tables when
     * the parent expression did not add an outer join of its own
     */
    public Integer addOuterJoinExpressionsHolders(Map<DatabaseTable, Expression> outerJoinedAdditionalJoinCriteria, ClassDescriptor descriptor) {

        List<OuterJoinExpressionHolder> outerJoinExpressionHolders = getOuterJoinExpressionsHolders();
        int index = outerJoinExpressionHolders.size();
        OuterJoinExpressionHolder holder = new OuterJoinExpressionHolder(this, null, null,
                outerJoinedAdditionalJoinCriteria, descriptor) {
            @Override
            protected void process(boolean usesHistory, boolean isMapKeyHolder) {
                sourceTable = descriptor.getTables().get(0);
                int count = 0;
                for (Map.Entry<DatabaseTable, Expression> entry: outerJoinedAdditionalJoinCriteria.entrySet()) {
                    DatabaseTable table = entry.getKey();
                    Expression onExpression = entry.getValue();
                    if (count==0) {
                        targetTable = table;
                        sourceAlias = onExpression.aliasForTable(sourceTable);
                        targetAlias = onExpression.aliasForTable(targetTable);
                    }

                    if (onExpression != null) {
                        DatabaseTable alias = onExpression.aliasForTable(table);
                        if (usesHistory) {
                            table = getTableAliases().get(alias);
                        }
                        if (this.additionalTargetAliases == null) {
                            this.additionalTargetAliases = new ArrayList();
                            this.additionalTargetTables = new ArrayList();
                            this.additionalJoinOnExpression = new ArrayList();
                            this.additionalTargetIsDescriptorTable = new ArrayList();
                        }
                        this.additionalTargetAliases.add(alias);
                        this.additionalTargetTables.add(table);
                        this.additionalJoinOnExpression.add(onExpression);
                        // if it's descriptor's own table - true; otherwise (it's child's table) - false.
                        this.additionalTargetIsDescriptorTable.add(false);
                    }
                    count++;
                }

                if(usesHistory) {
                    sourceTable = getTableAliases().get(sourceAlias);
                    targetTable = getTableAliases().get(targetAlias);
                }
            }
        };

        outerJoinExpressionHolders.add(holder);
        return index;
    }

    /**
     * Return the parent statement if using subselects.
     * This is used to normalize correctly with subselects.
     */
    public SQLSelectStatement getParentStatement() {
        return parentStatement;
    }

    /**
     * INTERNAL:
     * Return the aliases used.
     */
    public Map<DatabaseTable, DatabaseTable> getTableAliases() {
        return tableAliases;
    }

    /**
     * INTERNAL:
     * Return all the tables.
     */
    public List<DatabaseTable> getTables() {
        return tables;
    }

    /**
     * INTERNAL:
     *  Return True if unique field aliases will be generated of the form
     *     "fieldname AS fieldnameX", False otherwise.
     */
    public boolean getUseUniqueFieldAliases(){
        return this.useUniqueFieldAliases;
    }

    protected boolean hasAliasForTable(DatabaseTable table) {
        if (tableAliases != null) {
            return getTableAliases().containsKey(table);
        }

        return false;
    }

    public boolean hasGroupByExpressions() {
        return (groupByExpressions != null) && (!groupByExpressions.isEmpty());
    }

    public boolean hasHavingExpression() {
        return (havingExpression != null);
    }

    public boolean hasStartWithExpression() {
        return startWithExpression != null;
    }

    public boolean hasConnectByExpression() {
        return connectByExpression != null;
    }

    public boolean hasOrderSiblingsByExpressions() {
        return (orderSiblingsByExpressions != null) && (!orderSiblingsByExpressions.isEmpty());
    }

    public boolean hasHierarchicalQueryExpressions() {
        return ((startWithExpression != null) || (connectByExpression != null) || ((orderSiblingsByExpressions != null) && (!orderSiblingsByExpressions.isEmpty())));
    }

    public boolean hasOrderByExpressions() {
        return (orderByExpressions != null) && (!orderByExpressions.isEmpty());
    }

    public boolean hasUnionExpressions() {
        return (unionExpressions != null) && (!unionExpressions.isEmpty());
    }

    public boolean hasNonSelectFields() {
        return (nonSelectFields != null) && (!nonSelectFields.isEmpty());
    }

    public boolean hasOuterJoinExpressions() {
        return (outerJoinExpressionHolders != null) && (!outerJoinExpressionHolders.isEmpty());
    }

    /**
     * INTERNAL:
     */
    public boolean isAggregateSelect() {
        return isAggregateSelect;
    }

    /**
     * INTERNAL:
     * return true if this query has computed its distinct value already
     */
    public boolean isDistinctComputed() {
        return distinctState != ObjectLevelReadQuery.UNCOMPUTED_DISTINCT;
    }

    /**
     * INTERNAL:
     * Normalize an expression into a printable structure.
     * i.e. merge the expression with the join expressions.
     * Also replace table names with corresponding aliases.
     */
    public final void normalize(AbstractSession session, ClassDescriptor descriptor) {
        // 2612538 - the default size of Map (32) is appropriate
        normalize(session, descriptor, new IdentityHashMap());
    }

    /**
     * INTERNAL:
     * Normalize an expression into a printable structure.
     * i.e. merge the expression with the join expressions.
     * Also replace table names with corresponding aliases.
     * @param clonedExpressions With 2612185 allows additional expressions
     * from multiple bases to be rebuilt on the correct cloned base.
     */
    public void normalize(AbstractSession session, ClassDescriptor descriptor, Map clonedExpressions) {
        // Initialize the builder.
        if (getBuilder() == null) {
            if (getWhereClause() == null) {
                setBuilder(new ExpressionBuilder());
            } else {
                setBuilder(getWhereClause().getBuilder());
            }
        }

        ExpressionBuilder builder = getBuilder();

        // For flashback at this point make the expression
        // as of the correct time.
        if ((session.getAsOfClause() != null) && !isSubSelect()) {
            getWhereClause().asOf(session.getAsOfClause());
        } else if (builder.hasAsOfClause() && builder.getAsOfClause().isUniversal()) {
            // An as of clause set at the query level.
            getWhereClause().asOf(((UniversalAsOfClause)builder.getAsOfClause()).getAsOfClause());
        }

        // For flashback: The builder is increasingly important.  It can store
        // an AsOfClause, needs to be normalized for history, and aliased for
        // pessimistic locking to work.  Hence everything that would have
        // been applied to the where clause will be applied to the builder if
        // the former is null.  In the past though if there was no where
        // clause just threw away the builder (to get to this point we had to
        // pass it in via the vacated where clause), and neither normalized nor
        // aliased it directly.
        if (getWhereClause() == builder) {
            setWhereClause(null);
        }

        builder.setSession(session.getRootSession(null));

        // Some queries are not on objects but for data, thus no descriptor.
        if (!builder.doesNotRepresentAnObjectInTheQuery()) {
            if (descriptor != null) {
                Class queryClass = builder.getQueryClass();
                // GF 2333 Only change the descriptor class if:
                //  1 - it is not set
                //  2 - if this is an inheritance query
                //  3 - if it is to a table per tenant multitenant descriptor.
                //      When used at the EM level we need to ensure we are
                //      normalizing against the initialized descriptor and not
                //      that of the server session which is uninitialized.
                if ((queryClass == null) || descriptor.isChildDescriptor() || descriptor.hasTablePerMultitenantPolicy()) {
                    builder.setQueryClassAndDescriptor(descriptor.getJavaClass(), descriptor);
                }
            }
        }

        // Compute all other expressions used other than the where clause, i.e. select, order by, group by.
        // Also must ensure that all expression use a unique builder.
        Vector allExpressions = new Vector();

        // Process select expressions.
        rebuildAndAddExpressions(getFields(), allExpressions, builder, clonedExpressions);

        // Process non-select expressions
        if (hasNonSelectFields()) {
            rebuildAndAddExpressions(getNonSelectFields(), allExpressions, builder, clonedExpressions);
        }

        // Process group by expressions.
        if (hasGroupByExpressions()) {
            rebuildAndAddExpressions(getGroupByExpressions(), allExpressions, builder, clonedExpressions);
        }

        // Process union expressions.
        if (hasUnionExpressions()) {
            rebuildAndAddExpressions(getUnionExpressions(), allExpressions, builder, clonedExpressions);
        }

        if (hasHavingExpression()) {
            //rebuildAndAddExpressions(getHavingExpression(), allExpressions, builder, clonedExpressions);
            Expression expression = getHavingExpression();
            ExpressionBuilder originalBuilder = expression.getBuilder();
            if (originalBuilder != builder) {
                // For bug 2612185 avoid rebuildOn if possible as it rebuilds all on a single base.
                // i.e. Report query items could be from parallel expressions.
                if (clonedExpressions.get(originalBuilder) != null) {
                    expression = expression.copiedVersionFrom(clonedExpressions);
                } else {
                    // Possibly the expression was built with the wrong builder.
                    expression = expression.rebuildOn(builder);
                }

                setHavingExpression(expression);
            }

            allExpressions.add(expression);
        }

        // Process order by expressions.
        if (hasOrderByExpressions()) {
            normalizeOrderBy(builder, allExpressions, clonedExpressions, session);
        }

        // Process outer join by expressions.
        if (hasOuterJoinExpressions()) {
            for (OuterJoinExpressionHolder holder : this.getOuterJoinExpressionsHolders()) {
                if (holder.outerJoinedMappingCriteria != null) {
                    Expression expression = rebuildExpression(holder.outerJoinedMappingCriteria, builder, clonedExpressions);
                    if (holder.outerJoinedMappingCriteria != expression) {
                        holder.outerJoinedMappingCriteria = expression;
                    }
                    allExpressions.add(expression);
                }
                if (holder.outerJoinedAdditionalJoinCriteria != null) {
                    rebuildAndAddExpressions(holder.outerJoinedAdditionalJoinCriteria, allExpressions, builder, clonedExpressions);
                }
            }
        }

        //Process hierarchical query expressions.
        if (hasStartWithExpression()) {
            startWithExpression = getStartWithExpression().rebuildOn(builder);
            allExpressions.add(startWithExpression);
        }

        if (hasConnectByExpression()) {
            connectByExpression = getConnectByExpression().rebuildOn(builder);
        }

        if (hasOrderSiblingsByExpressions()) {
            rebuildAndAddExpressions(getOrderSiblingsByExpressions(), allExpressions, builder, clonedExpressions);
        }

        // We have to handle the cases where the where
        // clause is initially empty but might have clauses forced into it because the class
        // has multiple tables, order by forces a join, etc.  So we have to create a builder
        // and add expressions for it and the extras, but throw it away if they didn't force anything
        Expression oldRoot = getWhereClause();
        ExpressionNormalizer normalizer = new ExpressionNormalizer(this);
        normalizer.setSession(session);
        normalizer.setClonedExpressions(clonedExpressions);
        boolean isDistinctComputed = isDistinctComputed();

        Expression newRoot = null;

        if (oldRoot != null) {
            newRoot = oldRoot.normalize(normalizer);
        }

        // CR#3166542 always ensure that the builder has been normalized,
        // there may be an expression that does not refer to the builder, i.e. value=value.
        if (descriptor != null) {
            builder.normalize(normalizer);
        }

        for (int index = 0; index < allExpressions.size(); index++) {
            Expression expression = (Expression)allExpressions.get(index);
            expression.getBuilder().setSession(session);
            expression.normalize(normalizer);
        }
        // distinct state has been set by normalization, see may be that should be reversed
        if (shouldDistinctBeUsed() && !isDistinctComputed && !session.getPlatform().isLobCompatibleWithDistinct()) {
            for (Object field : getFields()) {
                if (field instanceof DatabaseField) {
                    if (Helper.isLob((DatabaseField)field)) {
                        dontUseDistinct();
                        break;
                    }
                } else if (field instanceof Expression) {
                    if (Helper.hasLob(((Expression)field).getSelectionFields(this.query))) {
                        dontUseDistinct();
                        break;
                    }
                }
            }
        }

        // Sets the where clause and AND's it with the additional Expression
        // setNormalizedWhereClause must be called to avoid the builder side-effects
        if (newRoot == null) {
            setNormalizedWhereClause(normalizer.getAdditionalExpression());
        } else {
            setNormalizedWhereClause(newRoot.and(normalizer.getAdditionalExpression()));
        }

        if (getWhereClause() != null) {
            allExpressions.add(getWhereClause());
        }

        // CR#3166542 always ensure that the builder has been normalized,
        // there may be an expression that does not refer to the builder, i.e. value=value.
        if (descriptor != null) {
            allExpressions.add(builder);
        }

        // Must also assign aliases to outer joined mapping criterias.
        if (hasOuterJoinExpressions()) {
            // Check for null on criterias.
            for (OuterJoinExpressionHolder holder : this.outerJoinExpressionHolders) {
                Expression criteria = holder.outerJoinedMappingCriteria;//
                if (criteria != null) {
                    allExpressions.add(criteria);
                }

                Map map = holder.outerJoinedAdditionalJoinCriteria;
                if (map != null) {
                    Iterator it = map.values().iterator();
                    while(it.hasNext()) {
                        criteria = (Expression)it.next();
                        if(criteria != null) {
                            allExpressions.add(criteria);
                        }
                    }
                }
            }
        }

        // Bug 2956674 Remove validate call as validation will be completed as the expression was normalized
        // Assign all table aliases.
        assignAliases(allExpressions);

        // If this is data level then the tables must be set manually.
        if (descriptor == null) {
            computeTablesFromTables();
        } else {
            computeTables();
        }

        // Now that the parent statement has been normalized, aliased, etc.,
        // normalize the subselect expressions.  For CR#4223.
        if (normalizer.encounteredSubSelectExpressions()) {
            normalizer.normalizeSubSelects(clonedExpressions);
        }

        // When a distinct is used the order bys must be in the select clause, so this forces them into the select.
        if (hasOrderByExpressions()) {
            // CR2114; If this is data level then we don't have a descriptor.
            // We don't have a target class so we must use the root platform. PWK
            // We are not fixing the informix.
            Class queryClass = null;
            if (descriptor != null) {
                queryClass = descriptor.getJavaClass();
            }
            DatasourcePlatform platform = (DatasourcePlatform)session.getPlatform(queryClass);
            if (platform.shouldSelectIncludeOrderBy() || (shouldDistinctBeUsed() && platform.shouldSelectDistinctIncludeOrderBy())) {
                addOrderByExpressionToSelectForDistinct();
            }
        }
    }

    /**
     * INTERNAL:
     * Normalize an expression mapping all of the descriptor's tables to the view.
     * This is used to allow a descriptor to read from a view, but write to tables.
     * This is used in the multiple table and subclasses read so all of the descriptor's
     * possible tables must be mapped to the view.
     */
    public void normalizeForView(AbstractSession theSession, ClassDescriptor theDescriptor, Map clonedExpressions) {
        ExpressionBuilder builder;

        // bug 3878553 - alias all view selects.
        setRequiresAliases(true);

        if (getWhereClause() != null) {
            builder = getWhereClause().getBuilder();
        } else {
            builder = new ExpressionBuilder();
            setBuilder(builder);
        }

        builder.setViewTable(getTables().get(0));

        normalize(theSession, theDescriptor, clonedExpressions);
    }


    /**
     * Check the order by for object expressions.
     * Order by the objects primary key or all fields for aggregates.
     */
    protected void normalizeOrderBy(Expression builder, List<Expression> allExpressions, Map<Expression, Expression> clonedExpressions, AbstractSession session) {
        List<Expression> newOrderBys = new ArrayList(this.orderByExpressions.size());
        for (Expression orderBy : this.orderByExpressions) {
            orderBy = rebuildExpression(orderBy, builder, clonedExpressions);
            Expression base = orderBy;
            Boolean asc = null;
            Boolean nullsFirst = null;
            if (orderBy.isFunctionExpression()) {
                if (base.getOperator().getSelector() == ExpressionOperator.NullsFirst) {
                    nullsFirst = true;
                    base = ((FunctionExpression)base).getChildren().get(0);
                } else if (base.getOperator().getSelector() == ExpressionOperator.NullsLast) {
                    nullsFirst = false;
                    base = ((FunctionExpression)base).getChildren().get(0);
                }
                if (base.isFunctionExpression()) {
                    if (base.getOperator().getSelector() == ExpressionOperator.Ascending) {
                        asc = true;
                        base = ((FunctionExpression)base).getChildren().get(0);
                    } else if (base.getOperator().getSelector() == ExpressionOperator.Descending) {
                        asc = false;
                        base = ((FunctionExpression)base).getChildren().get(0);
                    }
                }
            }
            if (base.isObjectExpression()) {
                ObjectExpression expression = (ObjectExpression)base;
                expression.getBuilder().setSession(session);
                List<Expression> orderBys = null;
                if (expression.getMapping() != null) {
                    // Check if a non basic mapping.
                    orderBys = expression.getMapping().getOrderByNormalizedExpressions(expression);
                } else if (base.isExpressionBuilder()) {
                    orderBys = new ArrayList(expression.getDescriptor().getPrimaryKeyFields().size());
                    for (DatabaseField field : expression.getDescriptor().getPrimaryKeyFields()) {
                        orderBys.add(expression.getField(field));
                    }
                }
                if (orderBys != null) {
                    for (Expression mappingOrderBy : orderBys) {
                        if (asc != null) {
                            if (asc) {
                                mappingOrderBy = mappingOrderBy.ascending();
                            } else {
                                mappingOrderBy = mappingOrderBy.descending();
                            }
                        }
                        if (nullsFirst != null) {
                            if (nullsFirst) {
                                mappingOrderBy = mappingOrderBy.nullsFirst();
                            } else {
                                mappingOrderBy = mappingOrderBy.nullsLast();
                            }
                        }
                        newOrderBys.add(mappingOrderBy);
                        allExpressions.add(mappingOrderBy);
                    }
                    continue;
                }
            }
            newOrderBys.add(orderBy);
            allExpressions.add(orderBy);
        }
        this.orderByExpressions = newOrderBys;
    }

    /**
     * Print the SQL representation of the statement on a stream.
     */
    public Vector<DatabaseField> printSQL(ExpressionSQLPrinter printer) {
        try {
            Vector<DatabaseField> selectFields = null;
            selectFields = printSQLSelect(printer);
            printSQLWhereKeyWord(printer);
            printSQLWhereClause(printer);
            printSQLHierarchicalQueryClause(printer);
            printSQLGroupByClause(printer);
            printSQLHavingClause(printer);
            printSQLOrderByClause(printer);
            printSQLForUpdateClause(printer);
            printSQLUnionClause(printer);
            return selectFields;
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    public Vector<DatabaseField> printSQLSelect(ExpressionSQLPrinter printer) throws IOException {
        Vector<DatabaseField> selectFields = null;
        printer.setRequiresDistinct(shouldDistinctBeUsed());

        if (hasUnionExpressions()) {
            // Ensure union order using brackets.
            int size = getUnionExpressions().size();
            for (int index = 0; index < size; index++) {
                printer.printString("(");
            }
        }
        printer.printString("SELECT ");

        if (getHintString() != null) {
            printer.printString(getHintString());
            printer.printString(" ");
        }

        if (shouldDistinctBeUsed()) {
            printer.printString("DISTINCT ");
        }

        selectFields = writeFieldsIn(printer);
        //fix bug:6070214: turn off unique field aliases after fields are written
        setUseUniqueFieldAliases(false);

        appendFromClauseToWriter(printer);
        return selectFields;
    }

    public void printSQLWhereKeyWord(ExpressionSQLPrinter printer) throws IOException {
        if (!(getWhereClause() == null)) {
            printer.printString(" WHERE ");
        }
    }

    public void printSQLWhereClause(ExpressionSQLPrinter printer) throws IOException {
        if (!(getWhereClause() == null)) {
            printer.printExpression(getWhereClause());
        }
    }

    public void printSQLHierarchicalQueryClause(ExpressionSQLPrinter printer) throws IOException {
        if (hasHierarchicalQueryExpressions()) {
            appendHierarchicalQueryClauseToWriter(printer);
        }
    }

    public void printSQLGroupByClause(ExpressionSQLPrinter printer) throws IOException {
        if (hasGroupByExpressions()) {
            appendGroupByClauseToWriter(printer);
        }
    }

    public void printSQLHavingClause(ExpressionSQLPrinter printer) throws IOException {
        if (hasHavingExpression()) {
            //appendHavingClauseToWriter(printer);
            printer.printString(" HAVING ");
            printer.printExpression(getHavingExpression());
        }
    }

    public void printSQLOrderByClause(ExpressionSQLPrinter printer) throws IOException {
        if (hasOrderByExpressions()) {
            appendOrderClauseToWriter(printer);
        }
    }

    public void printSQLForUpdateClause(ExpressionSQLPrinter printer) throws IOException {
        if(printer.getPlatform().shouldPrintLockingClauseAfterWhereClause() && printer.getPlatform().shouldPrintForUpdateClause()) {
            // For pessimistic locking.
            appendForUpdateClause(printer);
        }
    }

    public void printSQLUnionClause(ExpressionSQLPrinter printer) throws IOException {
        if (hasUnionExpressions()) {
            appendUnionClauseToWriter(printer);
        }
    }

    /**
     * Rebuild the expressions with the correct expression builder if using a different one.
     */
    public void rebuildAndAddExpressions(List expressions, List allExpressions, ExpressionBuilder primaryBuilder, Map clonedExpressions) {
        for (int index = 0; index < expressions.size(); index++) {
            Object fieldOrExpression = expressions.get(index);
            // Allow for special fields that contain a functional transformation.
            if (fieldOrExpression instanceof FunctionField) {
                fieldOrExpression = ((FunctionField)fieldOrExpression).getExpression();
            }
            if (fieldOrExpression instanceof Expression) {
                Expression expression = rebuildExpression((Expression)fieldOrExpression, primaryBuilder, clonedExpressions);
                if (fieldOrExpression != expression) {
                    expressions.set(index, expression);
                }
                allExpressions.add(expression);
            }
        }
    }

    /**
     * Rebuild the expression if required.
     */
    public Expression rebuildExpression(Expression expression, Expression primaryBuilder, Map<Expression, Expression> clonedExpressions) {
        ExpressionBuilder originalBuilder = expression.getBuilder();

        if (originalBuilder != primaryBuilder) {
            // For bug 2612185 avoid rebuildOn if possible as it rebuilds all on a single base.
            // i.e. Report query items could be from parallel expressions.
            if (clonedExpressions.get(originalBuilder) != null) {
                expression = expression.copiedVersionFrom(clonedExpressions);
                //if there is no builder or it is a copy of the base builder then rebuild otherwise it is a parallel expression not joined
            }
            if (originalBuilder.wasQueryClassSetInternally()) {
                // Possibly the expression was built with the wrong builder.
                expression = expression.rebuildOn(primaryBuilder);
            }
        }
        return expression;
    }

    /**
     * Rebuild the expressions with the correct expression builder if using a different one.
     * Exact copy of the another rebuildAndAddExpressions adopted to a Map with Expression values
     * as the first parameter (instead of Vector in the original method)
     */
    public void rebuildAndAddExpressions(Map expressions, Vector allExpressions, ExpressionBuilder primaryBuilder, Map clonedExpressions) {
        Iterator it = expressions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object fieldOrExpression = entry.getValue();

            if (fieldOrExpression instanceof Expression) {
                Expression expression = (Expression)fieldOrExpression;
                ExpressionBuilder originalBuilder = expression.getBuilder();

                if (originalBuilder != primaryBuilder) {
                    // For bug 2612185 avoid rebuildOn if possible as it rebuilds all on a single base.
                    // i.e. Report query items could be from parallel expressions.
                    if (clonedExpressions.get(originalBuilder) != null) {
                        expression = expression.copiedVersionFrom(clonedExpressions);
                        //if there is no builder or it is a copy of the base builder then rebuild otherwise it is a parallel expression not joined
                    }
                    if (originalBuilder.wasQueryClassSetInternally()) {
                        // Possibly the expression was built with the wrong builder.
                        expression = expression.rebuildOn(primaryBuilder);
                    }

                    entry.setValue(expression);
                }

                allExpressions.addElement(expression);
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void removeField(DatabaseField field) {
        getFields().remove(field);
    }

    /**
     * Remove a table from the statement. The table will
     * be dropped from the FROM part of the SQL statement.
     */
    public void removeTable(DatabaseTable table) {
        getTables().remove(table);
    }

    /**
     * INTERNAL: Returns true if aliases are required, false otherwise.
     * If requiresAliases is set then force aliasing, this is required for object-rel.
     */
    public boolean requiresAliases() {
        if (requiresAliases || hasOuterJoinExpressions()) {
            return true;
        }

        if (tableAliases != null) {
            return getTableAliases().size() > 1;
        }

        // tableAliases is null
        return false;
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void resetDistinct() {
        setDistinctState(ObjectLevelReadQuery.UNCOMPUTED_DISTINCT);
    }

    @Override
    public void setBuilder(ExpressionBuilder builder){
        this.builder = builder;
    }

    /**
     * Sets a unique id that will be used to alias the next table.
     * For sub-selects all must use this same aliasing information, maintained
     * in the root enclosing statement.  For CR#2627019
     */
    public void setCurrentAliasNumber(int currentAliasNumber) {
        if (getParentStatement() != null) {
            getParentStatement().setCurrentAliasNumber(currentAliasNumber);
        } else {
            this.currentAliasNumber = currentAliasNumber;
        }
    }

    /**
     * Set the non select fields. The fields are used only on joining.
     */
    public void setNonSelectFields(List nonSelectFields) {
        this.nonSelectFields = nonSelectFields;
    }

    /**
     * Set the where clause expression.
     * This must be used during normalization as the normal setWhereClause has the side effect
     * of setting the builder, which must not occur during normalize.
     */
    public void setNormalizedWhereClause(Expression whereClause) {
        this.whereClause = whereClause;
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void setDistinctState(short distinctState) {
        this.distinctState = distinctState;
    }

    /**
     * INTERNAL:
     * Set the fields, if any are aggregate selects then record this so that the distinct is not printed through anyOfs.
     */
    public void setFields(Vector fields) {
        for (Object fieldOrExpression : fields) {
            if (fieldOrExpression instanceof FunctionExpression) {
                if (((FunctionExpression)fieldOrExpression).getOperator().isAggregateOperator()) {
                    setIsAggregateSelect(true);
                    break;
                }
            }
        }
        this.fields = fields;
    }

    public void setGroupByExpressions(List<Expression> expressions) {
        this.groupByExpressions = expressions;
    }

    public void setHavingExpression(Expression expressions) {
        this.havingExpression = expressions;
    }

    /**
     * INTERNAL:
     * takes the hierarchical query expression which have been set on the query and sets them here
     * used to generate the Hierarchical Query Clause in the SQL
     */
    public void setHierarchicalQueryExpressions(Expression startWith, Expression connectBy, List<Expression> orderSiblingsExpressions) {
        setHierarchicalQueryExpressions(startWith, connectBy, orderSiblingsExpressions, null);
    }

    /**
     * INTERNAL:
     * takes the hierarchical query expression which have been set on the query and sets them here
     * used to generate the Hierarchical Query Clause in the SQL
     */
    public void setHierarchicalQueryExpressions(Expression startWith, Expression connectBy, List<Expression> orderSiblingsExpressions, ReadAllQuery.Direction direction) {
        this.startWithExpression = startWith;
        this.connectByExpression = connectBy;
        this.orderSiblingsByExpressions = orderSiblingsExpressions;
        this.direction = direction;
    }

    public void setIsAggregateSelect(boolean isAggregateSelect) {
        this.isAggregateSelect = isAggregateSelect;
    }

    protected void setForUpdateClause(ForUpdateClause clause) {
        this.forUpdateClause = clause;
    }

    public void setLockingClause(ForUpdateClause lockingClause) {
        this.forUpdateClause = lockingClause;
    }

    public void setOrderByExpressions(List<Expression> orderByExpressions) {
        this.orderByExpressions = orderByExpressions;
    }

    /**
     * Set the parent statement if using subselects.
     * This is used to normalize correctly with subselects.
     */
    public void setParentStatement(SQLSelectStatement parentStatement) {
        this.parentStatement = parentStatement;
    }

    /**
     * Query held as it may store properties needed to generate the SQL
     */
    public void setQuery(ReadQuery query) {
        this.query = query;
    }

    public void setRequiresAliases(boolean requiresAliases) {
        this.requiresAliases = requiresAliases;
    }

    protected void setTableAliases(Map<DatabaseTable, DatabaseTable> theTableAliases) {
        tableAliases = theTableAliases;
    }

    public void setTables(List<DatabaseTable> theTables) {
        tables = theTables;
    }

    /**
     * INTERNAL:
     *  If set unique field aliases will be generated of the form
     *     "fieldname AS fieldnameX"
     *  Where fieldname is the column name and X is an incremental value
     *  ensuring uniqueness
     */
    public void setUseUniqueFieldAliases(boolean useUniqueFieldAliases){
        this.useUniqueFieldAliases = useUniqueFieldAliases;
    }

    /**
     * INTERNAL:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is required for batch reading.
     */
    public boolean shouldDistinctBeUsed() {
        return distinctState == ObjectLevelReadQuery.USE_DISTINCT;
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void useDistinct() {
        setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
    }

    /**
     * INTERNAL:
     */
    protected void writeField(ExpressionSQLPrinter printer, DatabaseField field) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        if (printer.shouldPrintQualifiedNames()) {
            if (field.getTable() != lastTable) {
                lastTable = field.getTable();
                currentAlias = getBuilder().aliasForTable(lastTable);

                // This is really for the special case where things were pre-aliased
                if (currentAlias == null) {
                    currentAlias = lastTable;
                }
            }

            printer.printString(currentAlias.getQualifiedNameDelimited(printer.getPlatform()));
            printer.printString(".");
            printer.printString(field.getNameDelimited(printer.getPlatform()));
        } else {
            printer.printString(field.getNameDelimited(printer.getPlatform()));
        }
        if (this.getUseUniqueFieldAliases()){
            String alias = generatedAlias(field.getNameDelimited(printer.getPlatform()));
            if (shouldCacheFieldAliases()) {
                fieldAliases.put(field, alias);
            }
            printer.printString(" AS " + alias);
        }
    }

    private boolean shouldCacheFieldAliases() {
        return shouldCacheFieldAliases;
    }

    public void enableFieldAliasesCaching() {
        fieldAliases = new HashMap<>();
        shouldCacheFieldAliases = true;
    }

    public String getAliasFor(DatabaseField field) {
        if (shouldCacheFieldAliases()) {
            return fieldAliases.get(field);
        } else {
            return "";
        }
    }

    /**
    * Returns a generated alias based on the column name.  If the new alias will be too long
    * The alias is automatically truncated
    */
    public String generatedAlias(String fieldName) {
        return "a" + String.valueOf(getNextFieldCounterValue());
     }

    /**
     * INTERNAL:
     */
    protected void writeFieldsFromExpression(ExpressionSQLPrinter printer, Expression expression, Vector newFields) {
        expression.writeFields(printer, newFields, this);
    }

    /**
     * INTERNAL:
     */
    protected Vector writeFieldsIn(ExpressionSQLPrinter printer) {
        this.lastTable = null;

        Vector newFields = NonSynchronizedVector.newInstance();

        for (Object next : getFields()) {
            // Fields can be null placeholders for fetch groups.
            if (next != null) {
                if (next instanceof Expression) {
                    writeFieldsFromExpression(printer, (Expression)next, newFields);
                } else {
                    writeField(printer, (DatabaseField)next);
                    newFields.add(next);
                }
            }
        }
        return newFields;
    }

    /**
     * INTERNAL:
     * The method searches for expressions that join two tables each in a given expression.
     * Given expression and tablesInOrder and an empty SortedMap (TreeMap with no Comparator), this method
     *   populates the map with expressions corresponding to two tables
     *     keyed by an index (in tablesInOrder) of the table with the highest (of two) index;
     *   returns all the participating in at least one of the expressions.
     * Example:
     *   expression (joining Employee to Project through m-m mapping "projects"):
     *     (employee.emp_id = proj_emp.emp_id) and (proj_emp.proj_id = project.proj_id)
     *   tablesInOrder:
     *     employee, proj_emp, project
     *
     *   results:
     *     map:
     *          1 -> (employee.emp_id = proj_emp.emp_id)
     *          2 -> (proj_emp.proj_id = project.proj_id)
     *     returned SortedSet: {0, 1, 2}.
     *
     *     Note that tablesInOrder must contain all tables used by expression
     */
    public static SortedSet mapTableIndexToExpression(Expression expression, TreeMap map, List<DatabaseTable> tablesInOrder) {
        // glassfish issue 2440:
        // - Use DataExpression.getAliasedField instead of getField. This
        // allows to distinguish source and target tables in case of a self
        // referencing relationship.
        // - Removed the block handling ParameterExpressions, because it is
        // not possible to get into that method with a ParameterExpression.
        TreeSet tables = new TreeSet();
        if(expression instanceof DataExpression) {
            DataExpression de = (DataExpression)expression;
            if(de.getAliasedField() != null) {
                tables.add(Integer.valueOf(tablesInOrder.indexOf(de.getAliasedField().getTable())));
            }
            return tables;
        }

        // Bug 279784 - Incomplete OUTER JOIN based on JoinTable.
        // Save a copy of the original map to accommodate cases with more than one joined field, such as:
        // (employee.emp_id1 = proj_emp.emp_id1).and((employee.emp_id2 = proj_emp.emp_id2).and((proj_emp.proj_id1 = project.proj_id1).and(proj_emp.proj_id2 = project.proj_id2)))
        // Never adding (always overriding) cached expression (the code before the fix) resulted in the first child (employee.emp_id1 = proj_emp.emp_id1) being overridden and lost.
        // Always adding to the cached in the map expression would result in (proj_emp.proj_id1 = project.proj_id1).and(proj_emp.proj_id2 = project.proj_id2)) added twice.
        TreeMap originalMap = (TreeMap)map.clone();
        if(expression instanceof CompoundExpression) {
            CompoundExpression ce = (CompoundExpression)expression;
            tables.addAll(mapTableIndexToExpression(ce.getFirstChild(), map, tablesInOrder));
            tables.addAll(mapTableIndexToExpression(ce.getSecondChild(), map, tablesInOrder));
        } else if(expression instanceof FunctionExpression) {
            FunctionExpression fe = (FunctionExpression)expression;
            Iterator<Expression> it = fe.getChildren().iterator();
            while(it.hasNext()) {
                tables.addAll(mapTableIndexToExpression(it.next(), map, tablesInOrder));
            }
        }

        if(tables.size() == 2) {
            Object last = tables.last();
            Expression cachedExpression = (Expression)originalMap.get(last);
            if(cachedExpression == null) {
                map.put(last, expression);
            } else {
                map.put(last, cachedExpression.and(expression));
            }
        }

        return tables;
    }

    /**
     * INTERNAL:
     * The method searches for expressions that join two tables each in a given expression.
     * Given expression and tablesInOrder, this method
     *   returns the map with expressions corresponding to two tables
     *     keyed by tables (from tablesInOrder) with the highest (of two) index;
     * Example:
     *   expression (joining Employee to Project through m-m mapping "projects"):
     *     (employee.emp_id = proj_emp.emp_id) and (proj_emp.proj_id = project.proj_id)
     *   tablesInOrder:
     *     employee, proj_emp, project
     *
     *   results:
     *     returned map:
     *          proj_emp -> (employee.emp_id = proj_emp.emp_id)
     *          project -> (proj_emp.proj_id = project.proj_id)
     *
     *     Note that tablesInOrder must contain all tables used by expression
     */
    public static Map mapTableToExpression(Expression expression, Vector tablesInOrder) {
        TreeMap indexToExpressionMap = new TreeMap();
        mapTableIndexToExpression(expression, indexToExpressionMap, tablesInOrder);
        HashMap map = new HashMap(indexToExpressionMap.size());
        Iterator it = indexToExpressionMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            int index = ((Integer)entry.getKey()).intValue();
            map.put(tablesInOrder.get(index), entry.getValue());
        }
        return map;
    }

    // Outer join support methods / classes

    /*
     * Sort the holder list.
     * The sorting of holders is done to make sure that
     * for every table alias that is both source of (one or more) holders
     * and target of another holder the "target" holder is listed
     * before the "source" holder(s).
     * Denoting a holder as a pair of source alias and target alias, that means:
     *
     * {t0, t1}, {t1, t2}, {t1, t3} or {t0, t1}, {t1, t3}, {t1, t2} is ok;
     * but
     * {t1, t2}, {t0, t1}, {t1, t3} or {t1, t2}, {t1, t3}, {t0, t1} should be reordered.
     *
     * To achieve this goal the method assigns an integer index to each table alias
     * used by holders (for instance t0 -> 0; t1 -> 1; t2 -> 2; t3 -> 3).
     *
     * Each holder assigned a list of integers corresponding to a sequence of aliases
     * that starts with the one, which no holder uses as its target alias,
     * possibly continues several times from source alias to target alias for some other holder (if exists),
     * and ends with the holder's target alias.
     *
     * {t0, t1} -> {0, 1};
     * {t1, t2} -> {0, 1, 2}
     * {t1, t3} -> {0, 1, 3}
     *
     * Sorting of holders uses comparison of these lists (see OuterJoinExpressionHolder.compareTo):
     *
     * {0, 1} < {0, 1, 2} < {0, 1, 3}
     * Therefore the holders will be ordered:
     * {t0, t1}, {t1, t2}, {t1, t3}
     *
     * More complex example:
     * {t0, t1}, {t1, t2}, {t2, t7}, {t7, t10}, {t1, t3}, {t4, t5}, {t5, t8}, {t8, t9}, {t5, t11}, {t4, t12}
     *
     * A holder may have additional target table(s):
     * Examples:
     * secondary SALARY table in Employee class;
     * LPROJECT table in LargeProject class (primary PROJECT table is inherited from Project class).
     * In that case each additional target alias should have an entry in targetAliasToHolders
     * so that the holder that uses the additional table as a source could be placed in correct order.
     * For instance:
     * holder1 has target alias t1 and additional target alias t2:
     * if the latter is ignored then holder2 = {t2, t3} could be placed ahead of holder1.
     *
     */
    protected void sortOuterJoinExpressionHolders(List<OuterJoinExpressionHolder> holders) {
        Map<DatabaseTable, OuterJoinExpressionHolder> targetAliasToHolders = new HashMap();
        Set<DatabaseTable> aliases = new HashSet();
        Map<DatabaseTable, Integer> aliasToIndexes = new HashMap(aliases.size());
        int i = 0;
        for(OuterJoinExpressionHolder holder : holders) {
            targetAliasToHolders.put(holder.targetAlias, holder);
            if(!aliases.contains(holder.sourceAlias)) {
                aliases.add(holder.sourceAlias);
                aliasToIndexes.put(holder.sourceAlias, i++);
            }
            if(!aliases.contains(holder.targetAlias)) {
                aliases.add(holder.targetAlias);
                aliasToIndexes.put(holder.targetAlias, i++);
            }
            if(holder.additionalTargetAliases != null) {
                // if t1 is target alias and t2 is additional target alias (corresponding either to the secondary or inherited table)
                for(DatabaseTable alias : holder.additionalTargetAliases) {
                    if(!aliases.contains(alias)) {
                        aliases.add(alias);
                        aliasToIndexes.put(alias, i++);
                    }
                    targetAliasToHolders.put(alias, holder);
                }
            }
        }
        for(OuterJoinExpressionHolder holder : holders) {
            holder.createIndexList(targetAliasToHolders, aliasToIndexes);
        }
        Collections.sort(holders);
    }
}
