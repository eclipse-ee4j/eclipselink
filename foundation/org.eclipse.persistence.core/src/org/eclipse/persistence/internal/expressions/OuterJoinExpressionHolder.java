/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/06/2013-2.5.1 Chris Delahunt
//       - 374771 : TREAT support
package org.eclipse.persistence.internal.expressions;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.history.DecoratedDatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.DB2MainframePlatform;

/**
 * Holder class storing a QueryKeyExpression representing an outer join
 * plus some data calculated by method appendFromClauseForOuterJoin.
 */
public class OuterJoinExpressionHolder implements Comparable, Serializable
{
    final ObjectExpression joinExpression;
    DatabaseTable targetTable;
    DatabaseTable sourceTable;
    DatabaseTable targetAlias;
    DatabaseTable sourceAlias;
    List<DatabaseTable> additionalTargetTables;
    List<DatabaseTable> additionalTargetAliases;
    List<Expression> additionalJoinOnExpression;
    List<Boolean> additionalTargetIsDescriptorTable;
    Boolean hasInheritance;
    List<Integer> indexList;
    // if it's a map then an additional holder created for the key.
    // mapKeyHolder is not used in sorting because there can be no outer joins out of it,
    // the main reason for it to exist is its printAdditionalJoins method.
    OuterJoinExpressionHolder mapKeyHolder;
    // indicates whether it's a mapKeyHolder
    boolean isMapKeyHolder;

    Expression outerJoinedMappingCriteria;

    // table join expressions keyed by the tables
    Map<DatabaseTable, Expression> outerJoinedAdditionalJoinCriteria;
    // used in case no corresponding outerJoinExpression is provided -
    // only multi-table inheritance should be outer joined
    ClassDescriptor descriptor;

    SQLSelectStatement statement;

    public OuterJoinExpressionHolder(SQLSelectStatement statement, ObjectExpression joinExpression, Expression outerJoinedMappingCriteria,
            Map<DatabaseTable, Expression> outerJoinedAdditionalJoinCriteria, ClassDescriptor descriptor) {
        this.statement = statement;
        this.joinExpression = joinExpression;

        this.outerJoinedMappingCriteria = outerJoinedMappingCriteria;
        this.outerJoinedAdditionalJoinCriteria = outerJoinedAdditionalJoinCriteria;
        this.descriptor = descriptor;
    }

    /*
     * Used for MapKeys
     */
    public OuterJoinExpressionHolder(OuterJoinExpressionHolder holder) {
        this.joinExpression = holder.joinExpression;

        this.outerJoinedMappingCriteria = holder.outerJoinedMappingCriteria;
        this.outerJoinedAdditionalJoinCriteria = holder.outerJoinedAdditionalJoinCriteria;
        this.descriptor = holder.descriptor;
    }

    protected void process(boolean usesHistory) {
        process(usesHistory, false);
    }

    protected void process(boolean usesHistory, boolean isMapKeyHolder) {
        this.isMapKeyHolder = isMapKeyHolder;
        if (this.joinExpression instanceof QueryKeyExpression) {
            QueryKeyExpression expression = (QueryKeyExpression)this.joinExpression;
            if (isMapKeyHolder) {
                descriptor = expression.getMapKeyDescriptor();
                this.targetTable = descriptor.getTables().get(0);
                this.targetAlias = outerJoinedMappingCriteria.aliasForTable(this.targetTable);
            } else {
                // this is a map - create a holder for the key
                if(expression.isMapKeyObjectRelationship()) {
                    this.mapKeyHolder = new OuterJoinExpressionHolder(this);
                    this.mapKeyHolder.process(usesHistory, true);
                }
                // in DirectCollection case descriptor is null
                descriptor = expression.getDescriptor();
                this.targetTable = expression.getReferenceTable();
                this.targetAlias = expression.aliasForTable(this.targetTable);
            }
            this.sourceTable = expression.getSourceTable();
            this.sourceAlias = expression.getBaseExpression().aliasForTable(this.sourceTable);
        } else if (this.joinExpression != null) {
            this.sourceTable = ((ObjectExpression)this.joinExpression.getJoinSource()).getDescriptor().getTables().get(0);
            this.sourceAlias = this.joinExpression.getJoinSource().aliasForTable(this.sourceTable);
            this.targetTable = this.joinExpression.getDescriptor().getTables().get(0);
            this.targetAlias = this.joinExpression.aliasForTable(this.targetTable);
        } else {
            // absence of join expression means that this holder used for multitable inheritance:
            //   ReadAllQuery query = new ReadAllQuery(Project.class);
            //   query.setShouldOuterJoinSubclasses(true);
            // will produce:
            //   SELECT ... FROM PROJECT t0 LEFT OUTER JOIN LPROJECT t1 ON (t1.PROJ_ID = t0.PROJ_ID)
            sourceTable = descriptor.getTables().get(0);
            targetTable = descriptor.getInheritancePolicy().getChildrenTables().get(0);
            Expression exp = outerJoinedAdditionalJoinCriteria.get(targetTable);
            sourceAlias = exp.aliasForTable(sourceTable);
            targetAlias = exp.aliasForTable(targetTable);
        }
        if(usesHistory) {
            sourceTable = getTableAliases().get(sourceAlias);
            targetTable = getTableAliases().get(targetAlias);
        }

        if(outerJoinedAdditionalJoinCriteria != null && !outerJoinedAdditionalJoinCriteria.isEmpty()) {
            if(descriptor == null) {
                descriptor = joinExpression.getDescriptor();
            }
            List targetTables = descriptor.getTables();
            int nDescriptorTables = targetTables.size();
            hasInheritance = descriptor.hasInheritance();
            if(hasInheritance) {
                targetTables = descriptor.getInheritancePolicy().getAllTables();
            }
            int tablesSize = targetTables.size();
            // skip main table - start with i=1
            for(int i=1; i < tablesSize; i++) {
                DatabaseTable table = (DatabaseTable)targetTables.get(i);
                Expression onExpression = outerJoinedAdditionalJoinCriteria.get(table);
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
                    // if it's the descriptor's own table - true; otherwise (it's the child's table) - false.
                    this.additionalTargetIsDescriptorTable.add(i < nDescriptorTables);
                }
            }
        }
    }

    public boolean hasAdditionalJoinExpressions() {
        return this.additionalTargetTables != null;
    }

    public boolean hasMapKeyHolder() {
        return this.mapKeyHolder != null;
    }

    public void createIndexList(Map<DatabaseTable, OuterJoinExpressionHolder> targetAliasToHolders, Map<DatabaseTable, Integer> aliasToIndexes) {
        if(this.indexList != null) {
            // indexList has been already created
            return;
        }

        this.indexList = new ArrayList();
        OuterJoinExpressionHolder baseHolder = targetAliasToHolders.get(this.sourceAlias);
        if(baseHolder != null) {
            baseHolder.createIndexList(targetAliasToHolders, aliasToIndexes);
            this.indexList.addAll(baseHolder.indexList);
        } else {
            this.indexList.add(aliasToIndexes.get(this.sourceAlias));
        }
        this.indexList.add(aliasToIndexes.get(this.targetAlias));
    }

    /*
     * The method should be called only on instances of OuterJoinExpressionHolder
     * and only after the indexList has been created.
     * Loop through both lists comparing the members corresponding to the same index
     * until not equal members are found.
     * If all the members are the same, but one of the lists is shorter then it's less.
     * Examples:
     * {2, 1} < {2, 2}; {2, 1} < {3}; {2, 1} > {2}
     */
    public int compareTo(Object other) {
        if(other == this) {
            return 0;
        }
        List<Integer> otherIndexList = ((OuterJoinExpressionHolder)other).indexList;
        int nMinSize = this.indexList.size();
        int nCompare = -1;
        int nOtherSize = otherIndexList.size();
        if(nMinSize > nOtherSize) {
            nMinSize = nOtherSize;
            nCompare = 1;
        } else if(nMinSize == nOtherSize) {
            nCompare = 0;
        }
        for(int i=0; i < nMinSize; i++) {
            int index = indexList.get(i);
            int otherIndex = otherIndexList.get(i);
            if(index < otherIndex) {
                return -1;
            } else if(index > otherIndex) {
                return 1;
            }
        }
        return nCompare;
    }

    void printAdditionalJoins(ExpressionSQLPrinter printer, List<DatabaseTable> outerJoinedAliases, Collection aliasesOfTablesToBeLocked, boolean shouldPrintUpdateClauseForAllTables)  throws IOException {
        Writer writer = printer.getWriter();
        AbstractSession session = printer.getSession();

        int size = this.additionalTargetAliases.size();
        for(int i=0; i < size; i++) {
            DatabaseTable table = this.additionalTargetTables.get(i);
            if(this.additionalTargetIsDescriptorTable.get(i)) {
                // it's descriptor's own table
                if (!session.getPlatform().supportsANSIInnerJoinSyntax()) {
                    // if the DB does not support 'JOIN', do a:
                    if (this.hasInheritance) {
                        // right outer join instead. This will give the same
                        // result because the right table has no rows that
                        // are not in the left table (left table maps to the
                        // main class, right table to a subclass in an
                        // inheritance mapping with a joined subclass
                        // strategy).
                        writer.write(" RIGHT OUTER");
                    } else {
                        // left outer join instead. This will give the same
                        // result because the left table has no rows that
                        // are not in the right table (left table is either
                        // a join table or it is joining secondary tables to
                        // a primary table).
                        writer.write(" LEFT OUTER");
                    }
                }
                writer.write(" JOIN ");
            } else {
                // it's child's table
                writer.write(" LEFT OUTER JOIN ");
            }
            DatabaseTable alias = this.additionalTargetAliases.get(i);
            table.printSQL(printer);
            writer.write(" ");
            if (alias.isDecorated()) {
                ((DecoratedDatabaseTable)alias).getAsOfClause().printSQL(printer);
                writer.write(" ");
            }
            outerJoinedAliases.add(alias);
            alias.printSQL(printer);
            if (shouldPrintUpdateClauseForAllTables || (aliasesOfTablesToBeLocked != null && aliasesOfTablesToBeLocked.remove(alias))) {
                getForUpdateClause().printSQL(printer, statement/*SQLSelectStatement.this*/);
            }
            writer.write(" ON ");
            if (session.getPlatform() instanceof DB2MainframePlatform) {
                ((RelationExpression)this.additionalJoinOnExpression.get(i)).printSQLNoParens(printer);
            } else {
                this.additionalJoinOnExpression.get(i).printSQL(printer);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the aliases used.
     */
    public Map<DatabaseTable, DatabaseTable> getTableAliases() {
        return statement.getTableAliases();
    }

    protected ForUpdateClause getForUpdateClause() {
        return statement.getForUpdateClause();
    }
}
