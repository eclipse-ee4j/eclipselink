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
 *     Thomas Spiegl - fix for bug 324406
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;

/**
 * <p><b>Purpose</b>:
 * Mechanism used for all expression read queries.
 * ExpressionQueryInterface  understands how to deal with expressions.
 * <p>
 * <p><b>Responsibilities</b>:
 * Translates the expression and creates the appropriate SQL  statements.
 * Retrieves the data from the database and return the results to the query.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class ExpressionQueryMechanism extends StatementQueryMechanism {
    protected Expression selectionCriteria;

    public ExpressionQueryMechanism() {
    }
    
    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public ExpressionQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     * @param expression - selection criteria
     */
    public ExpressionQueryMechanism(DatabaseQuery query, Expression expression) {
        super(query);
        this.selectionCriteria = expression;
    }

    /**
     * Alias the supplied fields with respect to the expression node. Return copies of the fields
     */
    protected Vector aliasFields(ObjectExpression node, Vector fields) {
        Vector result = new Vector(fields.size());

        for (Enumeration e = fields.elements(); e.hasMoreElements();) {
            DatabaseField eachField = ((DatabaseField)e.nextElement()).clone();
            eachField.setTable(node.aliasForTable(eachField.getTable()));
            result.addElement(eachField);
        }

        return result;
    }

    /**
     * If the fields in the statement have breen pre-set, e.g. for a subset of the fields
     * in a partial attribute read, report query, or just a query for the class indicator,
     * then try to alias those. Right now this just guesses that they're all from the base.
     */
    public Vector aliasPresetFields(SQLSelectStatement statement) {
        Vector fields = statement.getFields();
        Expression exp = statement.getWhereClause();

        if (exp == null) {
            return fields;
        } else {
            ExpressionBuilder base = exp.getBuilder();
            return aliasFields(base, fields);
        }
    }

    /**
     * Create the appropriate where clause.
     * Since this is where the selection criteria gets cloned for the first time
     * (long after the owning query has been) many interesting things happen here.
     */
    public Expression buildBaseSelectionCriteria(boolean isSubSelect, Map clonedExpressions) {
        return buildBaseSelectionCriteria(isSubSelect, clonedExpressions, true);
    }
    /**
     * Create the appropriate where clause.
     * Since this is where the selection criteria gets cloned for the first time
     * (long after the owning query has been) many interesting things happen here.
     * Ability to switch off AdditionalJoinExpression is required for DeleteAllQuery.
     */
    public Expression buildBaseSelectionCriteria(boolean isSubSelect, Map clonedExpressions, boolean shouldUseAdditionalJoinExpression) {
        Expression expression = getSelectionCriteria();

        // For Flashback: builder.asOf(value) counts as a non-trivial selection criteria.
        // Also for bug 2612185 try to preserve the original builder as far as possible.
        if ((expression == null) && getQuery().isObjectLevelReadQuery()) {
            expression = ((ObjectLevelReadQuery)getQuery()).getExpressionBuilder();
        }

        // Subselects are not cloned, as they are cloned in the context of the parent expression.
        if ((!isSubSelect) && (expression != null)) {
            // For bug 2612185 specify the identity hashtable to be used in cloning so
            // it is not thrown away at the end of cloning.
            expression = expression.copiedVersionFrom(clonedExpressions);
        }
        if (expression != null && getQuery().isObjectLevelReadQuery()){
            //reset any new ExpressionBuilders in the expression that do not belong to the query and are not
            //parallel 
            ExpressionBuilder builder = ((ObjectLevelReadQuery)getQuery()).getExpressionBuilder();
                if ((!isSubSelect) && (builder != null)) {
                    builder = (ExpressionBuilder)builder.copiedVersionFrom(clonedExpressions);
                }
            expression.resetPlaceHolderBuilder(builder);
        }

        // Leaf inheritance and multiple table join.
        if (getDescriptor().shouldUseAdditionalJoinExpression()) {
            DescriptorQueryManager queryManager = getDescriptor().getQueryManager();
            Expression additionalJoin;
            if (shouldUseAdditionalJoinExpression) {
                additionalJoin = queryManager.getAdditionalJoinExpression();
            } else {
                additionalJoin = queryManager.getMultipleTableJoinExpression();
                if (additionalJoin == null) {
                    return expression;
                }
            }
    
            // If there's an expression, then we know we'll have to rebuild anyway, so don't clone.
            if (expression == null) {
                // Should never happen...
                expression = (Expression)additionalJoin.clone();
            } else {
                if (query.isObjectLevelReadQuery()){
                    ExpressionBuilder builder = ((ObjectLevelReadQuery)query).getExpressionBuilder();
                    if ((additionalJoin.getBuilder() != builder) && (additionalJoin.getBuilder().getQueryClass() == null)) {
                        if ((!isSubSelect) && (builder != null)) {
                            builder = (ExpressionBuilder)builder.copiedVersionFrom(clonedExpressions);
                        }
                        additionalJoin = additionalJoin.rebuildOn(builder);
                    }
                }
                expression = expression.and(additionalJoin);
            }
            // set wasAdditionalJoinCriteriaUsed on the addionalJoin because the expression may not have the correct builder as its left most builder
            additionalJoin.getBuilder().setWasAdditionJoinCriteriaUsed(true);
        }
        return expression;
    }

    /**
     * Return the appropriate select statement containing the fields in the table.
     */
    public SQLSelectStatement buildBaseSelectStatement(boolean isSubSelect, Map clonedExpressions) {
        return buildBaseSelectStatement(isSubSelect, clonedExpressions, true);
    }
    /**
     * Return the appropriate select statement containing the fields in the table.
     * Ability to switch off AdditionalJoinExpression is required for DeleteAllQuery.
     */
    public SQLSelectStatement buildBaseSelectStatement(boolean isSubSelect, Map clonedExpressions, boolean shouldUseAdditionalJoinExpression) {
        SQLSelectStatement selectStatement = new SQLSelectStatement();
        ObjectLevelReadQuery query = (ObjectLevelReadQuery)getQuery();
        selectStatement.setQuery(query);
        selectStatement.setLockingClause(query.getLockingClause());
        selectStatement.setDistinctState(query.getDistinctState());
        selectStatement.setTables((Vector)getDescriptor().getTables().clone());
        selectStatement.setWhereClause(buildBaseSelectionCriteria(isSubSelect, clonedExpressions, shouldUseAdditionalJoinExpression));
        //make sure we use the cloned builder and make sure we get the builder from the query if we have set the type.
        // If we use the expression builder and there are parallel builders and the query builder is on the 'right' 
        //instead of the 'left' we will build the SQL using the wrong builder.
        if (query.hasDefaultBuilder() && !query.getExpressionBuilder().wasQueryClassSetInternally()){
            selectStatement.setBuilder((ExpressionBuilder)query.getExpressionBuilder().copiedVersionFrom(clonedExpressions));
        }
        //For bug 5900782, the clone of the OrderBy expressions needs to be used to ensure they are normalized
        //every time when select SQL statement gets re-prepared, which will further guarantee the calculation 
        //of table alias always be correct
        if (query.hasOrderByExpressions()) {
            selectStatement.setOrderByExpressions(cloneExpressions(query.getOrderByExpressions(), clonedExpressions));
        }
        if (query.hasNonFetchJoinedAttributeExpressions()) {
            selectStatement.setNonSelectFields(cloneExpressions(query.getNonFetchJoinAttributeExpressions(), clonedExpressions));
        }
        if (query.hasUnionExpressions()) {
            selectStatement.setUnionExpressions(cloneExpressions(query.getUnionExpressions(), clonedExpressions));
        }
        if (getQuery().isReadAllQuery() && ((ReadAllQuery)getQuery()).hasHierarchicalExpressions()) {
            ReadAllQuery readAllquery = (ReadAllQuery)query;
            Expression startsWith = readAllquery.getStartWithExpression();
            if (startsWith != null) {
                startsWith.copiedVersionFrom(clonedExpressions);
            }
            selectStatement.setHierarchicalQueryExpressions(
                    startsWith,
                    readAllquery.getConnectByExpression().copiedVersionFrom(clonedExpressions),
                    cloneExpressions(readAllquery.getOrderSiblingsByExpressions(), clonedExpressions));
        }
        selectStatement.setHintString(query.getHintString());
        selectStatement.setTranslationRow(getTranslationRow());
        return selectStatement;
    }

    /**
     * Return the appropriate select statement containing the fields in the table.
     * This is used as a second read to a concrete class with subclasses in an abstract-multiple table read.
     */
    protected SQLSelectStatement buildConcreteSelectStatement() {
        // 2612538 - the default size of Map (32) is appropriate
        Map clonedExpressions = new IdentityHashMap();
        SQLSelectStatement selectStatement = buildBaseSelectStatement(false, clonedExpressions);

        ClassDescriptor descriptor = getDescriptor();
        InheritancePolicy policy = descriptor.getInheritancePolicy();
        // The onlyInstances expression is only included on leaf descriptor base select,
        // so if a root or branch (!shouldReadSubclasses means leaf), then it must be appended.
        if (policy.shouldReadSubclasses()) {
            Expression indicatorExpression = null;
            // If the descriptor is a single table branch, then select the whole branch in a single query.
            if (this.query.isReadAllQuery() && policy.hasChildren() && !policy.hasMultipleTableChild()) {
                indicatorExpression = policy.getWithAllSubclassesExpression();
            } else {
                indicatorExpression = policy.getOnlyInstancesExpression();                
            }
            if ((indicatorExpression != null) && (selectStatement.getWhereClause() != null)) {
                selectStatement.setWhereClause(selectStatement.getWhereClause().and(indicatorExpression));
            } else if (indicatorExpression != null) {
                selectStatement.setWhereClause((Expression)indicatorExpression.clone());
            }
        }

        selectStatement.setFields(getSelectionFields(selectStatement, false));
        selectStatement.normalize(getSession(), descriptor, clonedExpressions);
        // Allow for joining indexes to be computed to ensure distinct rows.
        if (((ObjectLevelReadQuery)this.query).hasJoining()) {
            ((ObjectLevelReadQuery)this.query).getJoinedAttributeManager().computeJoiningMappingIndexes(false, getSession(), 0);
        }

        return selectStatement;
    }

    /**
     * Return the appropriate delete statement
     * Passing of a call/ statement pair is used because the same pair
     * may be used several times.
     * More elegant orangement of passing just a statement and creating the call
     * in the method was rejected because the same call would've been potentially
     * re-created several times. 
     * Preconditions:
     *   if selectCallForExist != null then selectStatementForExist != null; 
     *   if selectCallForNotExist != null then selectStatementForNotExist != null. 
     * @return SQLDeleteStatement
     */
    protected SQLDeleteStatement buildDeleteAllStatement(DatabaseTable table, Expression inheritanceExpression,
                SQLCall selectCallForExist, SQLSelectStatement selectStatementForExist,
                SQLCall selectCallForNotExist, SQLSelectStatement selectStatementForNotExist,
                Collection primaryKeyFields) {
        if(selectCallForExist == null && selectCallForNotExist == null) {
            return buildDeleteStatementForDeleteAllQuery(table, inheritanceExpression);
        }
        
        SQLDeleteAllStatement deleteAllStatement = new SQLDeleteAllStatement();
        deleteAllStatement.setTable(table);
        deleteAllStatement.setTranslationRow(getTranslationRow());

        if(selectCallForExist != null) {
            deleteAllStatement.setSelectCallForExist(selectCallForExist);
            // if selectStatementForExist doesn't require aliasing and targets the same
            // table as the statement to be built,
            // then instead of creating sql with "WHERE EXISTS("
            // sql is created by extracting where clause from selectStatementForExist,
            // for instance:
            //   DELETE FROM PROJECT WHERE (PROJ_NAME = ?)
            // instead of the wrong one:
            //   DELETE FROM PROJECT WHERE EXISTS(SELECT PROJ_ID FROM PROJECT WHERE (PROJ_NAME = ?) AND PROJECT.PROJ_ID = PROJECT.PROJ_ID)
            deleteAllStatement.setShouldExtractWhereClauseFromSelectCallForExist(!selectStatementForExist.requiresAliases() && table.equals(selectStatementForExist.getTables().get(0)));
            deleteAllStatement.setTableAliasInSelectCallForExist(getAliasTableName(selectStatementForExist, table, getExecutionSession().getPlatform()));
        } else {
            // inheritanceExpression is irrelevant in case selectCallForExist != null
            if(inheritanceExpression != null) {
                deleteAllStatement.setInheritanceExpression((Expression)inheritanceExpression.clone());
            }
        }

        if(selectCallForNotExist != null) {
            deleteAllStatement.setSelectCallForNotExist(selectCallForNotExist);
            deleteAllStatement.setTableAliasInSelectCallForNotExist(getAliasTableName(selectStatementForNotExist, table, getExecutionSession().getPlatform()));
        }

        deleteAllStatement.setPrimaryKeyFieldsForAutoJoin(primaryKeyFields);

        return deleteAllStatement;
    }

    /**
     * Create SQLDeleteAllStatements for mappings that may be responsible for references
     * to the objects to be deleted
     * in the tables NOT mapped to any class: ManyToManyMapping and DirectCollectionMapping
     * 
     * NOTE: A similar pattern also used in method buildDeleteAllStatementsForMappingsWithTempTable():
     *  if you are updating this method consider applying a similar update to that method as well.
     * 
     * @return Vector<SQLDeleteAllStatement>
     */
    protected SQLDeleteStatement buildDeleteAllStatementForMapping(SQLCall selectCallForExist, SQLSelectStatement selectStatementForExist, Vector sourceFields, Vector targetFields) { 
        DatabaseTable targetTable = ((DatabaseField)targetFields.firstElement()).getTable();
        if(selectCallForExist == null) {
            return buildDeleteStatementForDeleteAllQuery(targetTable);
        }

        SQLDeleteAllStatement deleteAllStatement = new SQLDeleteAllStatement();
        
        deleteAllStatement.setTable(targetTable);
        deleteAllStatement.setTranslationRow(getTranslationRow());

        deleteAllStatement.setSelectCallForExist(selectCallForExist);
        DatabaseTable sourceTable = ((DatabaseField)sourceFields.firstElement()).getTable();
        if(selectStatementForExist != null) {
            deleteAllStatement.setTableAliasInSelectCallForExist(getAliasTableName(selectStatementForExist, sourceTable, getExecutionSession().getPlatform()));
        }

        deleteAllStatement.setAliasedFieldsForJoin(sourceFields);
        deleteAllStatement.setOriginalFieldsForJoin(targetFields);

        return deleteAllStatement;
    }
    
    /**
     * Build delete statements with temporary table for ManyToMany and DirectCollection mappings.
     * 
     * NOTE: A similar pattern also used in method buildDeleteAllStatementsForMappings():
     *  if you are updating this method consider applying a similar update to that method as well.
     *  
     * @return Vector<SQLDeleteAllStatementForTempTable>
     */
    protected Vector buildDeleteAllStatementsForMappingsWithTempTable(ClassDescriptor descriptor, DatabaseTable rootTable, boolean dontCheckDescriptor) {
        Vector deleteStatements = new Vector();
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (mapping.isForeignReferenceMapping()) { 
                List<DatabaseField> sourceFields = null;
                List<DatabaseField> targetFields = null;
                if (mapping.isDirectCollectionMapping()) {
                    if (shouldBuildDeleteStatementForMapping((DirectCollectionMapping)mapping, dontCheckDescriptor, descriptor)) {
                        sourceFields = ((DirectCollectionMapping)mapping).getSourceKeyFields();
                        targetFields = ((DirectCollectionMapping)mapping).getReferenceKeyFields();
                    }
                } else if (mapping.isAggregateCollectionMapping()) {
                    if (shouldBuildDeleteStatementForMapping((AggregateCollectionMapping)mapping, dontCheckDescriptor, descriptor)) {
                        sourceFields = ((AggregateCollectionMapping)mapping).getSourceKeyFields();
                        targetFields = ((AggregateCollectionMapping)mapping).getTargetForeignKeyFields();
                    }
                } else if (mapping.isManyToManyMapping()) {
                    if (shouldBuildDeleteStatementForMapping((ManyToManyMapping)mapping, dontCheckDescriptor, descriptor)) {
                        RelationTableMechanism relationTableMechanism = ((ManyToManyMapping)mapping).getRelationTableMechanism();
                        sourceFields = relationTableMechanism.getSourceKeyFields();
                        targetFields = relationTableMechanism.getSourceRelationKeyFields();
                    }
                } else if (mapping.isOneToOneMapping()) {
                    RelationTableMechanism relationTableMechanism = ((OneToOneMapping)mapping).getRelationTableMechanism();
                    if (relationTableMechanism != null) {
                        if (shouldBuildDeleteStatementForMapping((OneToOneMapping)mapping, dontCheckDescriptor, descriptor)) {
                            sourceFields = relationTableMechanism.getSourceKeyFields();
                            targetFields = relationTableMechanism.getSourceRelationKeyFields();
                        }
                    }
                }
                if (sourceFields != null) {
                    DatabaseTable targetTable = targetFields.get(0).getTable();
                    SQLDeleteAllStatementForTempTable deleteStatement 
                        =  buildDeleteAllStatementForTempTable(rootTable, sourceFields, targetTable, targetFields);
                    deleteStatements.addElement(deleteStatement);
                }
            }
        }
        return deleteStatements;
    }
    
    protected boolean shouldBuildDeleteStatementForMapping(ForeignReferenceMapping frMapping, boolean dontCheckDescriptor, ClassDescriptor descriptor) {
        return (dontCheckDescriptor || frMapping.getDescriptor().equals(descriptor))
            && !(frMapping.isCascadeOnDeleteSetOnDatabase());
    }
    
    protected static String getAliasTableName(SQLSelectStatement selectStatement, DatabaseTable table, DatasourcePlatform platform) {
        if(!selectStatement.requiresAliases()) {
            return null;
        }
        HashSet aliasTables = new HashSet();
        Iterator itEntries = selectStatement.getTableAliases().entrySet().iterator();
        DatabaseTable aliasTable = null;
        while(itEntries.hasNext()) {
            Map.Entry entry = (Map.Entry)itEntries.next();
            if(table.equals(entry.getValue())) {
                aliasTable = (DatabaseTable)entry.getKey();
                aliasTables.add(aliasTable);
            }
        }
        if(aliasTables.isEmpty()) {
            return null;
        } else if(aliasTables.size() == 1) {
            return aliasTable.getQualifiedNameDelimited(platform);
        }
        // The table has several aliases, 
        // remove the aliases that used by DataExpressions 
        // with baseExpression NOT the expressionBuilder used by the statement
        ExpressionIterator expIterator = new ExpressionIterator() {
            public void iterate(Expression each) {
                if(each instanceof DataExpression) {
                    DataExpression dataExpression = (DataExpression)each;
                    DatabaseField field = dataExpression.getField();
                    if(field != null) {
                        if(dataExpression.getBaseExpression() != getStatement().getBuilder()) {
                            ((Collection)getResult()).remove(dataExpression.getAliasedField().getTable());
                        }
                    }
                }
            }
            public boolean shouldIterateOverSubSelects() {
                return true;
            }
        };

        expIterator.setStatement(selectStatement);
        expIterator.setResult(aliasTables);
        expIterator.iterateOn(selectStatement.getWhereClause());
        
        if(aliasTables.size() == 1) {
            aliasTable = (DatabaseTable)aliasTables.iterator().next();
            return aliasTable.getQualifiedName();
        } else if(aliasTables.isEmpty()) {
            // should never happen
            return aliasTable.getQualifiedName();
        } else {
            // should never happen
            aliasTable = (DatabaseTable)aliasTables.iterator().next();
            return aliasTable.getQualifiedName();
        }        
    }
    
    /**
     * Used by DeleteAllQuery to create DeleteStatement in a simple case
     * when selectionCriteria==null.
     */
    protected SQLDeleteStatement buildDeleteStatementForDeleteAllQuery(DatabaseTable table) {
        return buildDeleteStatementForDeleteAllQuery(table, null);
    }

    /**
     * Used by DeleteAllQuery to create DeleteStatement in a simple case
     * when selectionCriteria==null.
     */
    protected SQLDeleteStatement buildDeleteStatementForDeleteAllQuery(DatabaseTable table, Expression inheritanceExpression) {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement();

        if(inheritanceExpression != null) {
            deleteStatement.setWhereClause((Expression)inheritanceExpression.clone());
        }
        deleteStatement.setTable(table);
        deleteStatement.setTranslationRow(getTranslationRow());
        deleteStatement.setHintString(getQuery().getHintString());
        return deleteStatement;
    }

    /**
     * Return the appropriate delete statement
     */
    protected SQLDeleteStatement buildDeleteStatement(DatabaseTable table) {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement();
        Expression whereClause;
        whereClause = getDescriptor().getObjectBuilder().buildDeleteExpression(table, getTranslationRow(), ((DeleteObjectQuery)getQuery()).usesOptimisticLocking());

        deleteStatement.setWhereClause(whereClause);
        deleteStatement.setTable(table);
        deleteStatement.setTranslationRow(getTranslationRow());
        deleteStatement.setHintString(getQuery().getHintString());
        return deleteStatement;
    }

    /**
     * Return the appropriate insert statement
     */
    protected SQLInsertStatement buildInsertStatement(DatabaseTable table) {
        SQLInsertStatement insertStatement = new SQLInsertStatement();
        insertStatement.setTable(table);
        insertStatement.setModifyRow(getModifyRow());
        if (getDescriptor().hasReturningPolicy()) {
            insertStatement.setReturnFields(getDescriptor().getReturningPolicy().getFieldsToGenerateInsert(table));
        }
        insertStatement.setHintString(getQuery().getHintString());
        return insertStatement;
    }

    /**
     * Return the appropriate select statement containing the fields in the table.
     */
    protected SQLSelectStatement buildNormalSelectStatement() {
        // From bug 2612185 Remember the identity hashtable used in cloning the selection criteria even in the normal case
        // for performance, in case subqueries need it, or for order by expressions.
        // 2612538 - the default size of Map (32) is appropriate
        Map clonedExpressions = new IdentityHashMap();
        SQLSelectStatement selectStatement = buildBaseSelectStatement(false, clonedExpressions);

        ObjectLevelReadQuery query = ((ObjectLevelReadQuery)getQuery());
        // Case, normal read for branch inheritance class that reads subclasses all in its own table(s).
        boolean includeAllSubclassesFields = true;
        if (getDescriptor().hasInheritance()) {
            getDescriptor().getInheritancePolicy().appendWithAllSubclassesExpression(selectStatement);
            if ((!query.isReportQuery()) && query.shouldOuterJoinSubclasses()) {
                selectStatement.getExpressionBuilder().setShouldUseOuterJoinForMultitableInheritance(true);
            }
            // Bug 380929 - Find whether to include all subclass fields or not.
            includeAllSubclassesFields = shouldIncludeAllSubclassFields(selectStatement);
        }
        
        selectStatement.setFields(getSelectionFields(selectStatement, includeAllSubclassesFields));
        selectStatement.normalize(getSession(), getDescriptor(), clonedExpressions);
        // Allow for joining indexes to be computed to ensure distinct rows.
        if (((ObjectLevelReadQuery)getQuery()).hasJoining()) {
            ((ObjectLevelReadQuery)getQuery()).getJoinedAttributeManager().computeJoiningMappingIndexes(true, getSession(), 0);
        }

        return selectStatement;
    }
    
    /**
     * Return whether to include all subclass fields in select statement or not.
     */
    protected boolean shouldIncludeAllSubclassFields(SQLSelectStatement selectStatement) {
        ExpressionBuilder builder = selectStatement.getBuilder();
        if (builder == null) {
            if (selectStatement.getWhereClause() == null) {
                return true;
            } else {
                builder = selectStatement.getWhereClause().getBuilder();
            }
        }
        
        if (!builder.doesNotRepresentAnObjectInTheQuery()) {
            if (getDescriptor() != null && getDescriptor().hasInheritance()) {
                return !builder.isDowncast(getDescriptor(), getSession());
            }
        }
        
        return true;
    }

    /**
     * Return the appropriate select statement containing the fields in the table.
     * Similar to super except the buildBaseSelectStatement will look after setting
     * the fields to select.
     */
    protected SQLSelectStatement buildReportQuerySelectStatement(boolean isSubSelect) {
        return buildReportQuerySelectStatement(isSubSelect, false, null, true);
    }
    /**
     * Customary inheritance expression is required for DeleteAllQuery and UpdateAllQuery preparation. 
     * Ability to switch off AdditionalJoinExpression is required for DeleteAllQuery.
     */
    protected SQLSelectStatement buildReportQuerySelectStatement(boolean isSubSelect, boolean useCustomaryInheritanceExpression, Expression inheritanceExpression, boolean shouldUseAdditionalJoinExpression) {
        ReportQuery reportQuery = (ReportQuery)getQuery();
        // For bug 2612185: Need to know which original bases were mapped to which cloned bases.
        // For sub-seclets the expressions have already been clones, and identity must be maintained with the outer expression.
        Map clonedExpressions = isSubSelect ? null : new IdentityHashMap();
        SQLSelectStatement selectStatement = buildBaseSelectStatement(isSubSelect, clonedExpressions, shouldUseAdditionalJoinExpression);
        if (reportQuery.hasGroupByExpressions()) {
            selectStatement.setGroupByExpressions(cloneExpressions(reportQuery.getGroupByExpressions(), clonedExpressions));
        }
        if (reportQuery.getHavingExpression() != null) {
            selectStatement.setHavingExpression(reportQuery.getHavingExpression().copiedVersionFrom(clonedExpressions));
        }
        if (getDescriptor().hasInheritance()) {
            if (useCustomaryInheritanceExpression) {
                if (inheritanceExpression != null) {
                    if (selectStatement.getWhereClause() == null) {
                        selectStatement.setWhereClause((Expression)inheritanceExpression.clone());
                    } else {
                        selectStatement.setWhereClause(selectStatement.getWhereClause().and(inheritanceExpression));
                    }
                }
            } else {
                getDescriptor().getInheritancePolicy().appendWithAllSubclassesExpression(selectStatement);
            }
        }
        Vector fieldExpressions = reportQuery.getQueryExpressions();
        int itemOffset = fieldExpressions.size();
        for (Iterator items = reportQuery.getItems().iterator(); items.hasNext();) {
            ReportItem item = (ReportItem)items.next();
            if (item.isConstructorItem()) {
                ConstructorReportItem citem= (ConstructorReportItem)item;
                List reportItems = citem.getReportItems();
                int size = reportItems.size();
                for (int i=0; i<size; i++) {
                    item = (ReportItem)reportItems.get(i);
                    extractStatementFromItem(item, clonedExpressions, selectStatement, fieldExpressions);
                }
            } else {
                extractStatementFromItem(item, clonedExpressions, selectStatement, fieldExpressions);
            }
        }
            
        selectStatement.setFields(fieldExpressions);
        if (reportQuery.hasNonFetchJoinedAttributeExpressions()) {
            selectStatement.setNonSelectFields(cloneExpressions(reportQuery.getNonFetchJoinAttributeExpressions(), clonedExpressions));
        }
        
        // Subselects must be normalized in the context of the parent statement.
        if (!isSubSelect) {
            selectStatement.normalize(getSession(), getDescriptor(), clonedExpressions);
        }

        //calculate indexes after normalize to insure expressions are set up correctly
        for (Iterator items = reportQuery.getItems().iterator(); items.hasNext();){
            ReportItem item = (ReportItem)items.next();
            
            if (item.isConstructorItem()) {
                ConstructorReportItem citem = (ConstructorReportItem)item;
                List reportItems = citem.getReportItems();
                int size = reportItems.size();
                for (int i=0; i<size; i++) {
                    item = (ReportItem)reportItems.get(i);
                    itemOffset = computeAndSetItemOffset(item, itemOffset);
                }
            } else {
                itemOffset = computeAndSetItemOffset(item, itemOffset);
            }
        }

        return selectStatement;
    }
    
    
    /**
     * calculate indexes for an item, given the current Offset
     */
    protected int computeAndSetItemOffset(ReportItem item, int itemOffset){
        item.setResultIndex(itemOffset);
        if (item.getAttributeExpression() != null) {
            if (item.hasJoining()){
                itemOffset = item.getJoinedAttributeManager().computeJoiningMappingIndexes(true, getSession(), itemOffset);
            } else {
                if (item.getDescriptor() != null) {
                    itemOffset += item.getDescriptor().getAllFields().size();
                } else {
                    if (item.getMapping() != null && item.getMapping().isAggregateObjectMapping()) {
                        itemOffset += item.getMapping().getFields().size(); // Aggregate object may consist out of 1..n fields
                    } else {
                        ++itemOffset; //only a single attribute can be selected
                    }
                }
            }
        }
        return itemOffset;
    }
    
    public void extractStatementFromItem(ReportItem item, Map clonedExpressions, SQLSelectStatement selectStatement, Vector fieldExpressions ){
        if (item.getAttributeExpression() != null) {
                // this allows us to modify the item expression without modifying the original in case of re-prepare
                Expression attributeExpression = item.getAttributeExpression(); 
                ExpressionBuilder clonedBuilder = attributeExpression.getBuilder();
                if (clonedBuilder.wasQueryClassSetInternally() && ((ReportQuery)getQuery()).getExpressionBuilder() != clonedBuilder) {
                    // no class specified so use statement builder as it is non-parallel
                    // must have same builder as it will be initialized
                    clonedBuilder = selectStatement.getBuilder();
                    attributeExpression = attributeExpression.rebuildOn(clonedBuilder);
                } else if (clonedExpressions != null && clonedExpressions.get(clonedBuilder) != null) {
                    Expression cloneExpression = (Expression)clonedExpressions.get(attributeExpression);
                    if ((cloneExpression != null) && !cloneExpression.isExpressionBuilder()) {
                        attributeExpression = cloneExpression;
                    } else {
                        //The builder has been cloned ensure that the cloned builder is used
                        //in the items.
                        clonedBuilder = (ExpressionBuilder)clonedBuilder.copiedVersionFrom(clonedExpressions);
                        attributeExpression = attributeExpression.copiedVersionFrom(clonedExpressions);
                    }
                } 
                if (attributeExpression.isExpressionBuilder()
                        && (item.getDescriptor().getQueryManager().getAdditionalJoinExpression() != null)
                        && !(clonedBuilder.wasAdditionJoinCriteriaUsed())) {
                    if (selectStatement.getWhereClause() == null ) {
                        selectStatement.setWhereClause(item.getDescriptor().getQueryManager().getAdditionalJoinExpression().rebuildOn(clonedBuilder));
                    } else {
                        selectStatement.setWhereClause(selectStatement.getWhereClause().and(item.getDescriptor().getQueryManager().getAdditionalJoinExpression().rebuildOn(clonedBuilder)));
                    }
                }
                fieldExpressions.add(attributeExpression);
                if (item.hasJoining()){
                    fieldExpressions.addAll(item.getJoinedAttributeManager().getJoinedAttributeExpressions());
                    fieldExpressions.addAll(item.getJoinedAttributeManager().getJoinedMappingExpressions());
                }
        }
    }

    /**
     * Return the appropriate select statement to perform a does exist check
     * @param field fields for does exist check.
     */
    protected SQLSelectStatement buildSelectStatementForDoesExist(DatabaseField field) {
        // Build appropriate select statement
        SQLSelectStatement selectStatement;
        selectStatement = new SQLSelectStatement();
        selectStatement.addField(field);
        selectStatement.setWhereClause(((Expression)getDescriptor().getObjectBuilder().getPrimaryKeyExpression().clone()).and(getDescriptor().getQueryManager().getAdditionalJoinExpression()));
        selectStatement.setTranslationRow(getTranslationRow());

        selectStatement.normalize(getSession(), getQuery().getDescriptor());
        selectStatement.setHintString(getQuery().getHintString());
        return selectStatement;
    }

    protected SQLUpdateAllStatement buildUpdateAllStatement(DatabaseTable table,
                HashMap databaseFieldsToValues,
                SQLCall selectCallForExist, SQLSelectStatement selectStatementForExist,
                Collection primaryKeyFields) 
    {    
        SQLUpdateAllStatement updateAllStatement = new SQLUpdateAllStatement();
        updateAllStatement.setTable(table);
        updateAllStatement.setTranslationRow(getTranslationRow());

        HashMap databaseFieldsToValuesCopy = new HashMap(databaseFieldsToValues.size());
        HashMap databaseFieldsToTableAliases = null;
        Iterator it = databaseFieldsToValues.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            // for each table to be updated
            DatabaseField field = (DatabaseField)entry.getKey();
            // here's a Map of left hand fields to right hand expressions
            Object value = entry.getValue();
            if(value instanceof SQLSelectStatement) {
                SQLSelectStatement selStatement = (SQLSelectStatement)value;
                SQLCall selCall = (SQLCall)selStatement.buildCall(getSession());
                databaseFieldsToValuesCopy.put(field, selCall);
                if(databaseFieldsToTableAliases == null) {
                    databaseFieldsToTableAliases = new HashMap();
                    updateAllStatement.setPrimaryKeyFieldsForAutoJoin(primaryKeyFields);
                }
                databaseFieldsToTableAliases.put(field, getAliasTableName(selStatement, table, getExecutionSession().getPlatform()));
            } else {
                // should be Expression
                databaseFieldsToValuesCopy.put(field, value);
            }
        }
        updateAllStatement.setUpdateClauses(databaseFieldsToValuesCopy);
        updateAllStatement.setDatabaseFieldsToTableAliases(databaseFieldsToTableAliases);
        
        updateAllStatement.setSelectCallForExist(selectCallForExist);
        updateAllStatement.setShouldExtractWhereClauseFromSelectCallForExist(!selectStatementForExist.requiresAliases() && table.equals(selectStatementForExist.getTables().get(0)));
        updateAllStatement.setTableAliasInSelectCallForExist(getAliasTableName(selectStatementForExist, table, getExecutionSession().getPlatform()));
        updateAllStatement.setPrimaryKeyFieldsForAutoJoin(primaryKeyFields);

        return updateAllStatement;
    }
    
    /**
     * Return the appropriate update statement
     * @return SQLInsertStatement
     */
    protected SQLUpdateStatement buildUpdateStatement(DatabaseTable table) {
        SQLUpdateStatement updateStatement = new SQLUpdateStatement();

        updateStatement.setModifyRow(getModifyRow());
        updateStatement.setTranslationRow(getTranslationRow());
        if (getDescriptor().hasReturningPolicy()) {
            updateStatement.setReturnFields(getDescriptor().getReturningPolicy().getFieldsToGenerateUpdate(table));
        }
        updateStatement.setTable(table);
        updateStatement.setWhereClause(getDescriptor().getObjectBuilder().buildUpdateExpression(table, getTranslationRow(), getModifyRow()));
        updateStatement.setHintString(getQuery().getHintString());
        return updateStatement;
    }

    /**
     * Perform a cache lookup for the query
     * This is only called from read object query.
     * The query has already checked that the cache should be checked.
     */
    @Override
    public Object checkCacheForObject(AbstractRecord translationRow, AbstractSession session) {
        // For bug 2782991 a list of nearly 20 problems with this method have
        // been fixed.
        ReadObjectQuery query = getReadObjectQuery();
        ClassDescriptor descriptor = getDescriptor();
        boolean conforming = false;
        UnitOfWorkImpl uow = null;
        if (session.isUnitOfWork()) {
            conforming = query.shouldConformResultsInUnitOfWork() || descriptor.shouldAlwaysConformResultsInUnitOfWork();
            uow = (UnitOfWorkImpl)session;
        }

        // Set the in memory query policy automatically for conforming queries, unless the
        // user specifies the most cautious one.
        int policyToUse = query.getInMemoryQueryIndirectionPolicyState();
        if (conforming && (policyToUse != InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION)) {
            policyToUse = InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED;
        }
        Object cachedObject = null;
        Expression selectionCriteria = getSelectionCriteria();

        // Perform a series of cache checks, in the following order...
        // 1: If selection key or selection object, lookup by primary key.
        // 2: If selection criteria null, take the first instance in cache.
        // 3: If exact primary key expression, lookup by primary key.
        // 4: If inexact primary key expression, lookup by primary key and see if it conforms.
        // 5: Perform a linear search on the cache, calling doesConform on each object.
        // 6: (Conforming) Search through new objects.
        // Each check is more optimal than the next.
        // Finally: (Conforming) check that any positive result was not deleted in the UnitOfWork.
        // 1: If selection key or selection object, do lookup by primary key.
        Object selectionKey = query.getSelectionId();
        Object selectionObject = query.getSelectionObject();
        if ((selectionKey != null) || (selectionObject != null)) {
            if (selectionKey == null) {
                selectionKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(selectionObject, session, true);
                if (selectionKey == null) {
                    // Has a null primary key, so must not exist.
                    return InvalidObject.instance;
                }
                // Must be checked separately as the expression and row is not yet set.
                query.setSelectionId(selectionKey);
            }
            if (query.requiresDeferredLocks()) {
                cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMapWithDeferredLock(selectionKey, query.getReferenceClass(), false, descriptor);
            } else {
                cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMap(selectionKey, query.getReferenceClass(), false, descriptor);
            }
        } else {
            // 2: If selection criteria null, take any instance in cache.
            //
            if (selectionCriteria == null) {
                // In future would like to always return something from cache.
                if (query.shouldConformResultsInUnitOfWork() || descriptor.shouldAlwaysConformResultsInUnitOfWork() || query.shouldCheckCacheOnly() || query.shouldCheckCacheThenDatabase()) {
                    cachedObject = session.getIdentityMapAccessorInstance().getFromIdentityMap(null, query.getReferenceClass(), translationRow, policyToUse, conforming, false, descriptor);
                }
            } else {
                // 3: If can extract exact primary key expression, do lookup by primary key.
                //
                selectionKey = descriptor.getObjectBuilder().extractPrimaryKeyFromExpression(true, selectionCriteria, translationRow, session);

                // If an exact primary key was extracted or should check cache by exact
                // primary key only this will become the final check.
                if ((selectionKey != null) || query.shouldCheckCacheByExactPrimaryKey()) {
                    if (selectionKey != null) {
                        // Check if key is invalid (null), cannot exist.
                        if (selectionKey == InvalidObject.instance) {
                            return selectionKey;
                        }
                        if (query.requiresDeferredLocks()) {
                            cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMapWithDeferredLock(selectionKey, query.getReferenceClass(), false, descriptor);
                        } else {
                            cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMap(selectionKey, query.getReferenceClass(), false, descriptor);
                        }
                        // Because it was exact primary key if the lookup failed then it is not there.                        
                    }
                } else {
                    // 4: If can extract inexact primary key, find one object by primary key and
                    // check if it conforms.  Failure of this object to conform however does not
                    // rule out a cache hit.
                    Object inexactSelectionKey = descriptor.getObjectBuilder().extractPrimaryKeyFromExpression(false, selectionCriteria, translationRow, session);// Check for any primary key in expression, may have other stuff.
                    if (inexactSelectionKey != null) {
                        // Check if key is invalid (null), cannot exist.
                        if (selectionKey == InvalidObject.instance) {
                            return selectionKey;
                        }
                        // PERF: Only use deferred lock when required.
                        if (query.requiresDeferredLocks()) {
                            cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMapWithDeferredLock(inexactSelectionKey, query.getReferenceClass(), false, descriptor);
                        } else {
                            cachedObject = session.getIdentityMapAccessorInstance().getFromLocalIdentityMap(inexactSelectionKey, query.getReferenceClass(), false, descriptor);
                        }
                    } else {
                        CacheKey cacheKey = descriptor.getCachePolicy().checkCacheByIndex(selectionCriteria, translationRow, descriptor, session);
                        if (cacheKey != null) {
                            if (query.requiresDeferredLocks()) {
                                cacheKey.checkDeferredLock();
                            } else {
                                cacheKey.checkReadLock();                                
                            }
                            cachedObject = cacheKey.getObject();
                        }                        
                    }
                    if (cachedObject != null) {
                        // Must ensure that it matches the expression.
                        try {
                            // PERF: 3639015 - cloning the expression no longer required
                            // when using the root session.
                            ExpressionBuilder builder = selectionCriteria.getBuilder();
                            builder.setSession(session.getRootSession(null));
                            builder.setQueryClass(descriptor.getJavaClass());
                            if (!selectionCriteria.doesConform(cachedObject, session, translationRow, policyToUse)) {
                                cachedObject = null;
                            }
                        } catch (QueryException exception) {// Ignore if expression too complex.
                            if (query.shouldCheckCacheOnly()) {// Throw on only cache.
                                throw exception;
                            }
                            cachedObject = null;
                        }
                    }

                    // 5: Perform a linear search of the cache, calling expression.doesConform on each element.
                    // This is a last resort linear time search of the identity map.
                    // This can be avoided by setting check cache by (inexact/exact) primary key on the query.
                    // That flag becomes invalid in the conforming case (bug 2609611: SUPPORT CONFORM RESULT IN UOW IN CONJUNCTION WITH OTHER IN-MEMORY FEATURES)
                    // so if conforming must always do this linear search, but at least only on 
                    // objects registered in the UnitOfWork.
                    //
                    boolean conformingButOutsideUnitOfWork = ((query.shouldConformResultsInUnitOfWork() || descriptor.shouldAlwaysConformResultsInUnitOfWork()) && !session.isUnitOfWork());
                    if ((cachedObject == null) && (conforming || (!query.shouldCheckCacheByPrimaryKey() && !conformingButOutsideUnitOfWork))) {
                        // PERF: 3639015 - cloning the expression no longer required
                        // when using the root session
                        if (selectionCriteria != null) {
                            ExpressionBuilder builder = selectionCriteria.getBuilder();
                            builder.setSession(session.getRootSession(null));
                            builder.setQueryClass(descriptor.getJavaClass());
                        }
                        try {
                            cachedObject = session.getIdentityMapAccessorInstance().getFromIdentityMap(selectionCriteria, query.getReferenceClass(), translationRow, policyToUse, conforming, false, descriptor);
                        } catch (QueryException exception) {// Ignore if expression too complex.
                            if (query.shouldCheckCacheOnly()) {// Throw on only cache.
                                throw exception;
                            }
                        }
                    }
                }
            }
        }
        
        // 6: If unit of work search through new objects.
        //
        if (conforming) {
            if (cachedObject == null) {
                if (selectionKey != null) {
                    cachedObject = uow.getObjectFromNewObjects(query.getReferenceClass(), selectionKey);
                } else {
                    // PERF: 3639015 - cloning the expression no longer required
                    // when using the root session
                    if (selectionCriteria != null) {
                        ExpressionBuilder builder = selectionCriteria.getBuilder();
                        builder.setSession(session.getRootSession(null));
                        builder.setQueryClass(descriptor.getJavaClass());
                    }
                    try {
                        cachedObject = uow.getObjectFromNewObjects(selectionCriteria, query.getReferenceClass(), translationRow, policyToUse);
                    } catch (QueryException exception) {
                        // Ignore if expression too complex.
                    }
                }
            }

            // Finally, check that a positive result is not deleted in the Unit Of Work.
            //
            if (cachedObject != null) {
                if (uow.isObjectDeleted(cachedObject)) {
                    if (selectionKey != null) {
                        // In this case return a special value, to notify 
                        // that the object was found but null must be returned.
                        return InvalidObject.instance;
                    } else {
                        cachedObject = null;
                    }
                }
            }
        }

        if (cachedObject != null) {
            // Fetch group check, ensure object is fetched.
            if (descriptor.hasFetchGroupManager()) {
                if (descriptor.getFetchGroupManager().isPartialObject(cachedObject)) {
                    FetchGroup fetchGroup = query.getExecutionFetchGroup(descriptor);
                    EntityFetchGroup entityFetchGroup = null;
                    if (fetchGroup!= null){
                        entityFetchGroup = descriptor.getFetchGroupManager().getEntityFetchGroup(fetchGroup);
                    }
                    if (!descriptor.getFetchGroupManager().isObjectValidForFetchGroup(cachedObject, entityFetchGroup)) {
                        //the cached object is partially fetched, and it's fetch group is not a superset of the one in the query, so the cached object is not valid for the query.
                        cachedObject = null;
                    }
                }
            }
        }
        // If only checking the cache, and empty, return invalid, unless it is a unit of work,
        // in which case the parent cache still needs to be checked.
        if ((cachedObject == null) && query.shouldCheckCacheOnly()
                && ((uow == null) ||  (!uow.isNestedUnitOfWork() && descriptor.getCachePolicy().shouldIsolateObjectsInUnitOfWork()))) {
            return InvalidObject.instance;
        }

        return cachedObject;
    }
    
    /**
     * The statement is no longer require after prepare so can be released.
     */
    public void clearStatement() {
        // Only clear the statement if it is an expression query, otherwise the statement may still be needed.
        setSQLStatement(null);
        setSQLStatements(null);
    }

    /**
     * Clone the mechanism for the specified query clone.
     * Should not try to clone statements.
     */
    public DatabaseQueryMechanism clone(DatabaseQuery queryClone) {
        DatabaseQueryMechanism clone = (DatabaseQueryMechanism)clone();
        clone.setQuery(queryClone);
        return clone;
    }

    /**
     * Return an expression builder which is valid for us
     */
    public ExpressionBuilder getExpressionBuilder() {
        if (getSelectionCriteria() != null) {
            return getSelectionCriteria().getBuilder();
        }
        return null;
    }

    /**
     * Return the selection criteria of the query.
     */
    public Expression getSelectionCriteria() {
        return selectionCriteria;
    }

    /**
     * Return the fields required in the select clause.
     * This must now be called after normalization, so it will get the aliased fields
     */
    public Vector getSelectionFields(SQLSelectStatement statement, boolean includeAllSubclassFields) {
        ObjectLevelReadQuery owner = (ObjectLevelReadQuery)getQuery();
        if (owner.hasPartialAttributeExpressions()) {
            return owner.getPartialAttributeSelectionFields(false);
        }

        Vector fields = NonSynchronizedVector.newInstance();
        if (owner.getExecutionFetchGroup() != null) {
            fields.addAll(owner.getFetchGroupSelectionFields());
        } else {
            if (includeAllSubclassFields) {
                fields.addAll(getDescriptor().getAllFields());
            } else {
                fields.add(statement.getExpressionBuilder());
            }
        }
        // Add joined fields.
        if (owner.hasJoining()) {
            owner.addJoinSelectionFields(fields, false);
        }
        if (owner.hasAdditionalFields()) {
            // Add additional fields, use for batch reading m-m.
            fields.addAll(owner.getAdditionalFields());
        }
        return fields;
    }

    /**
     * Return true if this is an expression query mechanism.
     */
    public boolean isExpressionQueryMechanism() {
        return true;
    }

    /**
     * Return true if this is a statement query mechanism
     */
    public boolean isStatementQueryMechanism() {
        return false;
    }

    /**
     * Override super to do nothing.
     */
    public void prepare() throws QueryException {
        // Do nothing.
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareCursorSelectAllRows() {
        if (getQuery().isReportQuery()) {
            SQLSelectStatement statement = buildReportQuerySelectStatement(false);
            setSQLStatement(statement);
            // For bug 2718118 inheritance with cursors is supported provided there is a read all subclasses view.
        } else if (getDescriptor().hasInheritance() && getDescriptor().getInheritancePolicy().requiresMultipleTableSubclassRead() && getDescriptor().getInheritancePolicy().hasView()) {
            InheritancePolicy inheritancePolicy = getDescriptor().getInheritancePolicy();
            SQLSelectStatement statement = inheritancePolicy.buildViewSelectStatement((ObjectLevelReadQuery)getQuery());
            setSQLStatement(statement);
        } else {
            setSQLStatement(buildNormalSelectStatement());
        }

        super.prepareCursorSelectAllRows();
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareDeleteAll() {
        prepareDeleteAll(null, false);
    }

    /**
     * Pre-build the SQL statement from the expression.
     * 
     * NOTE: A similar pattern also used in method buildDeleteAllStatementsForTempTable():
     *  if you are updating this method consider applying a similar update to that method as well.
     */
    protected void prepareDeleteAll(List<DatabaseTable> tablesToIgnore, boolean isWhereClauseRequired) {
        List<DatabaseTable> tablesInInsertOrder;
        ClassDescriptor descriptor = getDescriptor();
        if (tablesToIgnore == null) {
            // It's original (not a nested) method call.
            tablesInInsertOrder = descriptor.getMultipleTableInsertOrder();
        } else {
            // It's a nested method call: tableInInsertOrder filled with descriptor's tables (in insert order),
            // the tables found in tablesToIgnore are thrown away - 
            // they have already been taken care of by the caller.
            // In Employee example, query with reference class Project gets here 
            // to handle LPROJECT table; tablesToIgnore contains PROJECT table.
            tablesInInsertOrder = new ArrayList(descriptor.getMultipleTableInsertOrder().size());
            for (DatabaseTable table : descriptor.getMultipleTableInsertOrder()) {
                if (!tablesToIgnore.contains(table)) {
                    tablesInInsertOrder.add(table);
                }
            }
        }
        
        // cache the flag - used many times
        boolean hasInheritance = descriptor.hasInheritance();
        
        if (!tablesInInsertOrder.isEmpty()) {
            Expression whereClause = getSelectionCriteria();
            if (tablesToIgnore == null) {
                // It's original (not a nested) method call.
                // Ignore the passed dummy value of isWhereClauseRequired and calculate it here.
                // This value will be passed to all other tables.
                isWhereClauseRequired = whereClause != null;                
                if (!isWhereClauseRequired) {
                    Expression additionalExpression = descriptor.getQueryManager().getAdditionalJoinExpression();
                    if (additionalExpression != null) {
                        if (!additionalExpression.equals(descriptor.getQueryManager().getMultipleTableJoinExpression())) {
                            isWhereClauseRequired = true;
                        }
                    }
                }
            }
            
            SQLCall selectCallForExist = null;

            // Most databases support delete cascade constraints by specifying a ON DELETE CASCADE option when defining foreign key constraints. 
            // However some databases which don't support foreign key constraints cannot use delete cascade constraints.
            // Therefore each delete operation should be executed in such a database platform instead of delegating delete cascade constraints.
            boolean supportForeignKeyConstraints = getSession().getPlatform().supportsForeignKeyConstraints();
            boolean supportCascadeOnDelete = supportForeignKeyConstraints && descriptor.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables();
            boolean isSelectCallForNotExistRequired = (tablesToIgnore == null)
                    && (tablesInInsertOrder.size() > 1) && (!supportCascadeOnDelete);

            SQLSelectStatement selectStatementForNotExist = null;
            SQLCall selectCallForNotExist = null;
            
            // inheritanceExpression is always null in a nested method call.
            Expression inheritanceExpression = null;
            if (tablesToIgnore == null) {
                // It's original (not a nested) method call.
                if (hasInheritance) {
                    if (descriptor.getInheritancePolicy().shouldReadSubclasses()) {
                        inheritanceExpression = descriptor.getInheritancePolicy().getWithAllSubclassesExpression();
                    } else {
                        inheritanceExpression = descriptor.getInheritancePolicy().getOnlyInstancesExpression();
                    }                          
                }
            }
            
            SQLSelectStatement selectStatementForExist = createSQLSelectStatementForModifyAll(whereClause);
            
            // Main Case: Descriptor is mapped to more than one table and/or the query references other tables
            boolean isMainCase = selectStatementForExist.requiresAliases();            
            if (isMainCase) {
                if (isWhereClauseRequired) {
                    if (getExecutionSession().getPlatform().shouldAlwaysUseTempStorageForModifyAll() && tablesToIgnore == null) {
                        // currently DeleteAll using Oracle anonymous block is not implemented
                        if(!getExecutionSession().getPlatform().isOracle()) {
                            prepareDeleteAllUsingTempStorage();
                            return;
                        }
                    }
                
                    if (isSelectCallForNotExistRequired) {
                        selectStatementForNotExist = createSQLSelectStatementForModifyAll(null, null, descriptor, true, false);
                        selectCallForNotExist = (SQLCall)selectStatementForNotExist.buildCall(getSession());
                    }
                } else {
                    //whereClause = null
                    if (getExecutionSession().getPlatform().shouldAlwaysUseTempStorageForModifyAll() && tablesToIgnore == null) {
                        // currently DeleteAll using Oracle anonymous block is not implemented
                        if (!getExecutionSession().getPlatform().isOracle()) {
                            // the only case to handle without temp storage is inheritance root without inheritanceExpression:
                            // in this case all generated delete calls will have no where clauses.
                            if (hasInheritance && !(inheritanceExpression == null && descriptor.getInheritancePolicy().isRootParentDescriptor())) {
                                prepareDeleteAllUsingTempStorage();
                                return;
                            }
                        }
                    }                    
                }
            } else {
                // simple case: Descriptor is mapped to a single table and the query references no other tables.
                if (isWhereClauseRequired) {
                    if (getExecutionSession().getPlatform().shouldAlwaysUseTempStorageForModifyAll() && tablesToIgnore == null) {
                        // currently DeleteAll using Oracle anonymous block is not implemented
                        if (!getExecutionSession().getPlatform().isOracle()) {
                            // if there are derived classes with additional tables - use temporary storage
                            if (hasInheritance && descriptor.getInheritancePolicy().hasMultipleTableChild()) {
                                prepareDeleteAllUsingTempStorage();
                                return;
                            }
                        }
                    }                    
                }
            }

            // Don't use selectCallForExist in case there is no whereClause -
            // a simpler sql will be created if possible.
            if (isWhereClauseRequired) {
                selectCallForExist = (SQLCall)selectStatementForExist.buildCall(getSession());
            }
            
            if (isMainCase) {
                // Main case: Descriptor is mapped to more than one table and/or the query references other tables
                //
                // Add and prepare to a call a delete statement for each table.
                // In the case of multiple tables, build the sql statements list in insert order. When the 
                // actual SQL calls are sent they are sent in the reverse of this order.
                for (DatabaseTable table : tablesInInsertOrder) {
                    Collection primaryKeyFields = getPrimaryKeyFieldsForTable(table);                    
                    SQLDeleteStatement deleteStatement;

                    // In Employee example, query with reference class:
                    //   Employee will build "EXISTS" for SALARY and "NOT EXISTS" for EMPLOYEE;
                    //   LargeProject will build "EXISTS" for LPROJECT and "NOT EXISTS" for Project.
                    // The situation is a bit more complex if more than two levels of inheritance is involved:
                    // both "EXISTS" and "NOT EXISTS" used for the "intermediate" (not first and not last) tables.                    
                    if (!isSelectCallForNotExistRequired) {
                        // isSelectCallForNotExistRequired == false:
                        // either tablesToIgnore != null: it's a nested method call.
                        // Example:
                        // In Employee example, query with reference class
                        //   Project will get here to handle LPROJECT table
                        // or tablesInInsertOrder.size() == 1: there is only one table,
                        // but there is joining to at least one other table (otherwise would've been isMainCase==false).
                        //
                        // Note that buildDeleteAllStatement ignores inheritanceExpression if selectCallForExist!=null.
                        deleteStatement = buildDeleteAllStatement(table, inheritanceExpression, selectCallForExist, selectStatementForExist, null, null, primaryKeyFields);
                    } else {
                        // isSelectCallForNotExistRequired==true: original call, multiple tables.
                        
                        // indicates whether the table is the last in insertion order
                        boolean isLastTable = table.equals(tablesInInsertOrder.get(tablesInInsertOrder.size() - 1));
                        
                        if (inheritanceExpression == null) {
                            if(isLastTable) {
                                // In Employee example, query with reference class Employee calls this for SALARY table;
                                deleteStatement = buildDeleteAllStatement(table, null, selectCallForExist, selectStatementForExist, null, null, primaryKeyFields);
                            } else {
                                // In Employee example, query with reference class Employee calls this for EMPLOYEE table
                                deleteStatement = buildDeleteAllStatement(table, null, null, null, selectCallForNotExist, selectStatementForNotExist, primaryKeyFields);
                            }
                        } else {
                            // there is inheritance
                            if (table.equals(descriptor.getMultipleTableInsertOrder().get(0))) {
                                // This is the highest table in inheritance hierarchy - the one that contains conditions
                                // (usually class indicator fields) that defines the class identity.
                                // inheritanceExpression is for this table (it doesn't reference any other tables).
                                // In Employee example, query with reference class LargeProject calls this for PROJECT table
                                deleteStatement = buildDeleteAllStatement(table, inheritanceExpression, null, null, selectCallForNotExist, selectStatementForNotExist, primaryKeyFields);
                            } else {
                                ClassDescriptor desc = getHighestDescriptorMappingTable(table);
                                if (desc == descriptor) {
                                    if (isLastTable) {
                                        // In Employee example, query with reference class LargeProject calls this for LPROJECT table;
                                        deleteStatement = buildDeleteAllStatement(table, null, selectCallForExist, selectStatementForExist, null, null, primaryKeyFields);
                                    } else {
                                        // Class has multiple tables that are not inherited.
                                        // In extended Employee example: 
                                        //   Employee2 class inherits from Employee and
                                        //     mapped to two additional tables: EMPLOYEE2 and SALARY2.
                                        //   Query with reference class Employee2 calls this for EMPLOYEE2 table.
                                        deleteStatement = buildDeleteAllStatement(table, null, null, null, selectCallForNotExist, selectStatementForNotExist, primaryKeyFields);
                                    }
                                } else {
                                    // This table is mapped through descriptor that stands higher in inheritance hierarchy
                                    // (but not the highest one - this is taken care in another case).
                                    //
                                    // inheritanceSelectStatementForExist is created for the higher descriptor,
                                    // but the inheritance expression from the current descriptor is used.
                                    // Note that this trick doesn't work in case the higher descriptor was defined with
                                    // inheritance policy set not to read subclasses
                                    // (descriptor.getInheritancePolicy().dontReadSubclassesOnQueries()).
                                    // In that case inheritance expression for the higher descriptor can't
                                    // be removed - it still appears in the sql and collides with the inheritance 
                                    // expression from the current descriptor - the selection expression is never true.
                                    //
                                    // In extended Employee example:
                                    //   VeryLargeProject inherits from LargeProject,
                                    //     mapped to an additional table VLPROJECT;
                                    //   VeryVeryLargeProject inherits from VeryLargeProject,
                                    //     mapped to the same tables as it's parent.
                                    // 
                                    // Note that this doesn't work in case LargeProject descriptor was set not to read subclasses:
                                    // in that case the selection expression will have (PROJ_TYPE = 'L') AND (PROJ_TYPE = 'V')
                                    // 
                                    SQLSelectStatement inheritanceSelectStatementForExist = createSQLSelectStatementForModifyAll(null, inheritanceExpression, desc, true, true);
                                    SQLCall inheritanceSelectCallForExist = (SQLCall)inheritanceSelectStatementForExist.buildCall(getSession());

                                    if(isLastTable) {
                                        // In extended Employee example:
                                        //   Query with reference class VeryVeryLargeProject calls this for VLPROJECT table.
                                        deleteStatement = buildDeleteAllStatement(table, null, inheritanceSelectCallForExist, inheritanceSelectStatementForExist, null, null, primaryKeyFields);
                                    } else {
                                        // In extended Employee example:
                                        //   Query with reference class VeryLargeProject calls this for LPROJECT table.
                                        // Note that both EXISTS and NOT EXISTS clauses created.
                                        deleteStatement = buildDeleteAllStatement(table, null, inheritanceSelectCallForExist, inheritanceSelectStatementForExist, selectCallForNotExist, selectStatementForNotExist, primaryKeyFields);
                                    }
                                }
                            }
                        }
                    }
        
                    if (descriptor.getTables().size() > 1) {
                        getSQLStatements().add(deleteStatement);
                    } else {
                        setSQLStatement(deleteStatement);
                    }
                    // Only delete from first table if delete is cascaded on the database.
                    if (supportCascadeOnDelete) {
                        break;
                    }
                }
            } else {
                // A simple case:
                //   there is only one table mapped to the descriptor, and
                //   selection criteria doesn't reference any other tables
                // A simple sql call with no subselect should be built.
                // In Employee example, query with reference class:
                //   Project will build a simple sql call for PROJECT(and will make nested method calls for LargeProject and SmallProject);
                //   SmallProject will build a simple sql call for PROJECT                
                setSQLStatement(buildDeleteAllStatement(descriptor.getDefaultTable(), inheritanceExpression, selectCallForExist, selectStatementForExist, null, null, null));
            }

            if (selectCallForExist == null) {
                // Getting there means there is no whereClause. 
                // To handle the mappings selectCallForExist may be required in this case, too.
                if (hasInheritance && (tablesToIgnore != null || inheritanceExpression != null)) {
                    // The only case NOT to create the call for no whereClause is either no inheritance,
                    // or it's an original (not a nested) method call and there is no inheritance expression.
                    // In Employee example:
                    //   query with reference class Project and no where clause for m-to-m mapping generates:
                    //     DELETE FROM EMP_PROJ;
                    //   as opposed to query with reference class SmallProject:
                    //     DELETE FROM EMP_PROJ WHERE EXISTS(SELECT PROJ_ID FROM PROJECT WHERE (PROJ_TYPE = ?) AND PROJ_ID = EMP_PROJ.PROJ_ID).
                    //
                    selectCallForExist = (SQLCall)selectStatementForExist.buildCall(getSession());
                }
            }

            // Add statements for ManyToMany and DirectCollection mappings
            List<SQLStatement> deleteStatementsForMappings = buildDeleteAllStatementsForMappings(selectCallForExist, selectStatementForExist, tablesToIgnore == null);
            if(!deleteStatementsForMappings.isEmpty()) {
                if(getSQLStatement() != null) {
                    getSQLStatements().add(getSQLStatement());
                    setSQLStatement(null);
                }
                getSQLStatements().addAll(deleteStatementsForMappings);
            }            
        }

        // Indicates whether the descriptor has children using extra tables.
        boolean hasChildrenWithExtraTables = hasInheritance && descriptor.getInheritancePolicy().hasChildren() && descriptor.getInheritancePolicy().hasMultipleTableChild();

        // TBD: should we ignore subclasses in case descriptor doesn't want us to read them in?
        //** Currently in this code we do ignore.
        //** If it will be decided that we need to handle children in all cases
        //** the following statement should be changed to: boolean shouldHandleChildren = hasChildrenWithExtraTables;
        boolean shouldHandleChildren = hasChildrenWithExtraTables && descriptor.getInheritancePolicy().shouldReadSubclasses();

        // Perform a nested method call for each child
        if (shouldHandleChildren) {
            // In Employee example: query for Project will make nested calls to
            // LargeProject and SmallProject and ask them to ignore PROJECT table
            List<DatabaseTable> tablesToIgnoreForChildren = new ArrayList();
            // The tables this descriptor has ignored, its children also should ignore.
            if (tablesToIgnore != null) {
                tablesToIgnoreForChildren.addAll(tablesToIgnore);
            }

            // If the descriptor reads subclasses there is no need for
            // subclasses to process its tables for the second time.
            if (descriptor.getInheritancePolicy().shouldReadSubclasses()) {
                tablesToIgnoreForChildren.addAll(tablesInInsertOrder);
            }
            
            Iterator it = descriptor.getInheritancePolicy().getChildDescriptors().iterator();
            while (it.hasNext()) {
                // Define the same query for the child
                ClassDescriptor childDescriptor = (ClassDescriptor)it.next();
                
                // Most databases support delete cascade constraints by specifying a ON DELETE CASCADE option when defining foreign key constraints. 
                // However some databases which don't support foreign key constraints cannot use delete cascade constraints.
                // Therefore each delete operation should be executed in such a database platform instead of delegating delete cascade constraints.
                boolean supportForeignKeyConstraints = getSession().getPlatform().supportsForeignKeyConstraints();
                boolean supportCascadeOnDelete = supportForeignKeyConstraints && childDescriptor.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables();
                // Need to process only "multiple tables" child descriptors
                if (((!supportCascadeOnDelete) && childDescriptor.getTables().size() > descriptor.getTables().size()) || 
                    (childDescriptor.getInheritancePolicy().hasMultipleTableChild())) 
                {
                    DeleteAllQuery childQuery = new DeleteAllQuery();
                    childQuery.setReferenceClass(childDescriptor.getJavaClass());
                    childQuery.setSelectionCriteria(getSelectionCriteria());
                    childQuery.setDescriptor(childDescriptor);
                    childQuery.setSession(getSession());
                    
                    ExpressionQueryMechanism childMechanism = (ExpressionQueryMechanism)childQuery.getQueryMechanism();
                    // nested call
                    childMechanism.prepareDeleteAll(tablesToIgnoreForChildren, isWhereClauseRequired);
                    
                    // Copy the statements from child query mechanism.
                    // In Employee example query for Project will pick up a statement for 
                    // LPROJECT table from LargeProject and nothing from SmallProject.
                    List<SQLStatement> childStatements = new ArrayList();
                    if (childMechanism.getCall() != null) {
                        childStatements.add(childMechanism.getSQLStatement());
                    } else if(childMechanism.getSQLStatements() != null) {
                        childStatements.addAll(childMechanism.getSQLStatements());
                    }
                    if (!childStatements.isEmpty()) {
                        if (getSQLStatement() != null) {
                            getSQLStatements().add(getSQLStatement());
                            setSQLStatement(null);
                        }
                        getSQLStatements().addAll(childStatements);
                    }
                }
            }
        }
        
        // Nested method call doesn't need to call this.
        if (tablesToIgnore == null) {
            ((DeleteAllQuery)getQuery()).setIsPreparedUsingTempStorage(false);
            super.prepareDeleteAll();
        }
    }

    protected void prepareDeleteAllUsingTempStorage() {
        if(getExecutionSession().getPlatform().supportsTempTables()) {
            prepareDeleteAllUsingTempTables();
        } else {
            throw QueryException.tempTablesNotSupported(getQuery(), Helper.getShortClassName(getExecutionSession().getPlatform()));
        }
    }
    
    protected void prepareDeleteAllUsingTempTables() {
        getSQLStatements().addAll(buildStatementsForDeleteAllForTempTables());
        ((DeleteAllQuery)getQuery()).setIsPreparedUsingTempStorage(true);
        super.prepareDeleteAll();
    }
    
    // Create SQLDeleteAllStatements for mappings that may be responsible for references
    // to the objects to be deleted
    // in the tables NOT mapped to any class: ManyToManyMapping and DirectCollectionMapping
    /**
     * 
     * NOTE: A similar pattern also used in method buildDeleteAllStatementsForMappingsWithTempTable:
     *  if you are updating this method consider applying a similar update to that method as well.
     *  
     * @return Vector<SQLDeleteAllStatement>
     */
    protected Vector buildDeleteAllStatementsForMappings(SQLCall selectCallForExist, SQLSelectStatement selectStatementForExist, boolean dontCheckDescriptor) {
        Vector deleteStatements = new Vector();
        ClassDescriptor descriptor = getDescriptor();
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (mapping.isForeignReferenceMapping()) { 
                Vector sourceFields = null;
                Vector targetFields = null;
                if (mapping.isDirectCollectionMapping()) {
                    if (shouldBuildDeleteStatementForMapping((DirectCollectionMapping)mapping, dontCheckDescriptor, descriptor)) {
                        sourceFields = ((DirectCollectionMapping)mapping).getSourceKeyFields();
                        targetFields = ((DirectCollectionMapping)mapping).getReferenceKeyFields();
                    }
                } else if (mapping.isAggregateCollectionMapping()) {
                    if (shouldBuildDeleteStatementForMapping((AggregateCollectionMapping)mapping, dontCheckDescriptor, descriptor)) {
                        sourceFields = ((AggregateCollectionMapping)mapping).getSourceKeyFields();
                        targetFields = ((AggregateCollectionMapping)mapping).getTargetForeignKeyFields();
                    }
                } else if (mapping.isManyToManyMapping()) {
                    if (shouldBuildDeleteStatementForMapping((ManyToManyMapping)mapping, dontCheckDescriptor, descriptor)) {
                        RelationTableMechanism relationTableMechanism = ((ManyToManyMapping)mapping).getRelationTableMechanism();
                        sourceFields = relationTableMechanism.getSourceKeyFields();
                        targetFields = relationTableMechanism.getSourceRelationKeyFields();
                    }
                } else if (mapping.isOneToOneMapping()) {
                    RelationTableMechanism relationTableMechanism = ((OneToOneMapping)mapping).getRelationTableMechanism();
                    if (relationTableMechanism != null) {
                        if (shouldBuildDeleteStatementForMapping((OneToOneMapping)mapping, dontCheckDescriptor, descriptor)) {
                            sourceFields = relationTableMechanism.getSourceKeyFields();
                            targetFields = relationTableMechanism.getSourceRelationKeyFields();
                        }
                    }
                }
                if (sourceFields != null) {
                    deleteStatements.add(buildDeleteAllStatementForMapping(selectCallForExist, selectStatementForExist, sourceFields, targetFields));
                }
            }
        }
        return deleteStatements;
    }
    
    protected SQLSelectStatement createSQLSelectStatementForModifyAll(Expression whereClause) {
        return createSQLSelectStatementForModifyAll(whereClause, null, getDescriptor(), false, true); 
    }
    
    /**
     * Customary inheritance expression is required for DeleteAllQuery and UpdateAllQuery preparation. 
     * Ability to switch off AdditionalJoinExpression is required for DeleteAllQuery.
     */
    protected SQLSelectStatement createSQLSelectStatementForModifyAll(Expression whereClause, Expression inheritanceExpression,
                                 ClassDescriptor desc, boolean useCustomaryInheritanceExpression, boolean shouldUseAdditionalJoinExpression) 
    {
        ExpressionBuilder builder;
        if(whereClause != null) {
            whereClause = (Expression)whereClause.clone();
            builder = whereClause.getBuilder();
        } else {
            builder = new ExpressionBuilder();
        }
        
        ReportQuery reportQuery = new ReportQuery(desc.getJavaClass(), builder);
        reportQuery.setDescriptor(desc);
        reportQuery.setShouldRetrieveFirstPrimaryKey(true);
        reportQuery.setSelectionCriteria(whereClause);
        reportQuery.setSession(getSession());
        
        SQLSelectStatement selectStatement = ((ExpressionQueryMechanism)reportQuery.getQueryMechanism()).buildReportQuerySelectStatement(false, useCustomaryInheritanceExpression, inheritanceExpression, shouldUseAdditionalJoinExpression);
        reportQuery.setSession(null);
        return selectStatement;
    }
        
    
    
    
    protected SQLSelectStatement createSQLSelectStatementForAssignedExpressionForUpdateAll(Expression value)
    {
        ReportQuery reportQuery = new ReportQuery(getQuery().getReferenceClass(), value.getBuilder());
        reportQuery.setDescriptor(getQuery().getDescriptor());
        reportQuery.setSession(getSession());
        reportQuery.addAttribute("", value);
        
        SQLSelectStatement selectStatement = ((ExpressionQueryMechanism)reportQuery.getQueryMechanism()).buildReportQuerySelectStatement(false);
        reportQuery.setSession(null);
        return selectStatement;
    }
    
    
    /**
     * This method return the clones of the list of expressions.
     */
    private List<Expression> cloneExpressions(List<Expression> originalExpressions, Map<Expression, Expression> clonedExpressions){
        if ((originalExpressions == null) || (originalExpressions.size() == 0) || (clonedExpressions == null)) {
            return originalExpressions;
        }
        List<Expression> newExpressions = new ArrayList<Expression>(originalExpressions.size());
        for (Expression expression : originalExpressions) {
            newExpressions.add(expression.copiedVersionFrom(clonedExpressions));
        }
        return newExpressions;
    }

        
    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareDeleteObject() {
        ClassDescriptor descriptor = getDescriptor();
        if (descriptor.usesFieldLocking() && (getTranslationRow() == null)) {
            return;
        }
        // Add and prepare to a call a delete statement for each table.
        // In the case of multiple tables, build the sql statements Vector in insert order. When the 
        // actual SQL calls are sent they are sent in the reverse of this order.
        for (DatabaseTable table : descriptor.getMultipleTableInsertOrder()) {
            SQLDeleteStatement deleteStatement = buildDeleteStatement(table);
            if (descriptor.getTables().size() > 1) {
                getSQLStatements().add(deleteStatement);
            } else {
                setSQLStatement(deleteStatement);
            }
            // Most databases support delete cascade constraints by specifying a ON DELETE CASCADE option when defining foreign key constraints. 
            // However some databases which don't support foreign key constraints cannot use delete cascade constraints.
            // Therefore each delete operation should be executed in such a database platform instead of delegating delete cascade constraints.
            boolean supportForeignKeyConstraints = getSession().getPlatform().supportsForeignKeyConstraints();
            boolean supportCascadeOnDelete = supportForeignKeyConstraints && descriptor.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables();
            if (supportCascadeOnDelete) {
                break;
            }
        }

        super.prepareDeleteObject();
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareDoesExist(DatabaseField field) {
        setSQLStatement(buildSelectStatementForDoesExist(field));

        super.prepareDoesExist(field);
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareInsertObject() {
        // Require modify row to prepare.
        if (getModifyRow() == null) {
            return;
        }

        // Add and prepare to a call a update statement for each table.
        // In the case of multiple tables, build the sql statements in insert order.
        ClassDescriptor descriptor = getDescriptor();
        if (descriptor.getTables().size() == 1) {
            setSQLStatement(buildInsertStatement(descriptor.getTables().get(0)));
        } else {
            for (DatabaseTable table : descriptor.getMultipleTableInsertOrder()) {
                SQLInsertStatement insertStatement = buildInsertStatement(table);
                getSQLStatements().addElement(insertStatement);
            }
        }

        super.prepareInsertObject();
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareReportQuerySelectAllRows() {
        SQLSelectStatement statement = buildReportQuerySelectStatement(false);
        setSQLStatement(statement);
        setCallFromStatement();
        // The statement is no longer require so can be released.
        setSQLStatement(null);

        getCall().returnManyRows();
        prepareCall();
    }

    /**
     * Pre-build the SQL statement from the expression.
     * This is used for subselects, so does not normalize or generate the SQL as it needs the outer expression for this.
     */
    public void prepareReportQuerySubSelect() {
        setSQLStatement(buildReportQuerySelectStatement(true));
        // The expression is no longer require so can be released.
        setSelectionCriteria(null);
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareSelectAllRows() {
        // Check for multiple table inheritance which may require multiple queries.
        if (!getDescriptor().hasInheritance() || !getDescriptor().getInheritancePolicy().requiresMultipleTableSubclassRead()){
            setSQLStatement(buildNormalSelectStatement());
            super.prepareSelectAllRows();
        } else {
            InheritancePolicy policy = getDescriptor().getInheritancePolicy();
            if (policy.hasView()){
                // CR#3158703 if the descriptor has a view, then it requires a single select,
                // so can be prepared.
                setSQLStatement(getDescriptor().getInheritancePolicy().buildViewSelectStatement((ObjectLevelReadQuery)getQuery()));
                super.prepareSelectAllRows();
            } else if ( ((ObjectLevelReadQuery)getQuery()).shouldOuterJoinSubclasses() ){
                //outer join into a single select that can be built normally
                setSQLStatement(buildNormalSelectStatement());
                super.prepareSelectAllRows();
            } else if (!getDescriptor().getInheritancePolicy().hasClassExtractor()) {
                // CR#3158703 otherwise if using a type indicator at least the type select can be prepared.
                setSQLStatement(getDescriptor().getInheritancePolicy().buildClassIndicatorSelectStatement((ObjectLevelReadQuery)getQuery()));
                super.prepareSelectAllRows();
            } 
        }
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareSelectOneRow() {
        // Check for multiple table inheritance which may require multiple queries.
        if (!getDescriptor().hasInheritance() || !getDescriptor().getInheritancePolicy().requiresMultipleTableSubclassRead()){
            setSQLStatement(buildNormalSelectStatement());
            super.prepareSelectOneRow();
        } else {
            InheritancePolicy policy = getDescriptor().getInheritancePolicy();
            if (policy.hasView()){
                // CR#3158703 if the descriptor has a view, then it requires a single select,
                // so can be prepared.
                setSQLStatement(getDescriptor().getInheritancePolicy().buildViewSelectStatement((ObjectLevelReadQuery)getQuery()));
                super.prepareSelectOneRow();
            } else if ( ((ObjectLevelReadQuery)getQuery()).shouldOuterJoinSubclasses() ){
                //outer join into a single select that can be built normally
                setSQLStatement(buildNormalSelectStatement());
                super.prepareSelectOneRow();
            } else if (!getDescriptor().getInheritancePolicy().hasClassExtractor()) {
                // CR#3158703 otherwise if using a type indicator at least the type select can be prepared.
                setSQLStatement(getDescriptor().getInheritancePolicy().buildClassIndicatorSelectStatement((ObjectLevelReadQuery)getQuery()));
                super.prepareSelectOneRow();
            } 
        }
    }

    /**
     * Pre-build the SQL statement from the expression.
     */
    public void prepareUpdateObject() {
        // Require modify row to prepare.
        if (getModifyRow() == null) {
            return;
        }
        
        // EL Bug 319759
        AbstractRecord row = getQuery().getTranslationRow();
        boolean useCache = (row == null || !(getQuery().shouldValidateUpdateCallCacheUse() && row.hasNullValueInFields()));
        
        // PERF: Check the descriptor update SQL call cache for a matching update with the same fields.
        Vector updateCalls = getDescriptor().getQueryManager().getCachedUpdateCalls(getModifyRow().getFields());
        // If the calls were cached then don't need to prepare.
        if (updateCalls != null && useCache == true) {
            int updateCallsSize = updateCalls.size();
            if (updateCallsSize == 1) {
                // clone call, to be able to set query on clone
                DatasourceCall existingCall = (DatasourceCall)updateCalls.get(0);
                DatasourceCall clonedCall = (DatasourceCall)existingCall.clone();
                setCall(clonedCall);
            } else {
                // clone calls
                Vector clonedCalls = new Vector(updateCallsSize);
                for (int i = 0; i < updateCallsSize; i++) {
                    DatasourceCall existingCall = (DatasourceCall)updateCalls.get(i);
                    clonedCalls.add(existingCall.clone());
                }
                setCalls(clonedCalls);                
            }
            return;
        }
        
        // Add and prepare to a call a update statement for each table.
        int tablesSize = getDescriptor().getTables().size();
        for (int index = 0; index < tablesSize; index++) {
            DatabaseTable table = getDescriptor().getTables().get(index);
            SQLUpdateStatement updateStatement = buildUpdateStatement(table);
            if (tablesSize > 1) {
                getSQLStatements().addElement(updateStatement);
            } else {
                setSQLStatement(updateStatement);
            }
        }

        super.prepareUpdateObject();
        
        // PERF: Cache the update SQL call to avoid regeneration.
        if (useCache == true) { // EL Bug 319759
            if (hasMultipleCalls()) {
                updateCalls = getCalls();
            } else {
                updateCalls = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
                if (getCall() != null) {
                    updateCalls.add(getCall());
                }
            }
            getDescriptor().getQueryManager().putCachedUpdateCalls(getModifyRow().getFields(), updateCalls);
        }
    }

    /**
     * Pre-build the SQL statement from the expressions.
     */
    public void prepareUpdateAll() {
        ExpressionBuilder builder = ((UpdateAllQuery)getQuery()).getExpressionBuilder();        
        HashMap updateClauses = ((UpdateAllQuery)getQuery()).getUpdateClauses();
        
        // Add a statement to update the optimistic locking field if their is one.
        OptimisticLockingPolicy policy = getDescriptor().getOptimisticLockingPolicy();
        if (policy != null) {
            if(policy.getWriteLockField() != null) {
                Expression writeLock = builder.getField(policy.getWriteLockField());
                Expression writeLockUpdateExpression = policy.getWriteLockUpdateExpression(builder, getQuery().getSession());
                if (writeLockUpdateExpression != null) {
                    // clone it to keep user's original data intact
                    updateClauses = (HashMap)updateClauses.clone();
                    updateClauses.put(writeLock, writeLockUpdateExpression);
                }
            }
        }
        
        HashMap tables_databaseFieldsToValues =  new HashMap();
        HashMap<DatabaseTable, List<DatabaseField>> tablesToPrimaryKeyFields = new HashMap();
        Iterator it = updateClauses.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            
            Object fieldObject = entry.getKey();
            DataExpression fieldExpression = null;
            Expression baseExpression = null; // QueryKeyExpression or FieldExpression of the field 
            String attributeName = null;
            if(fieldObject instanceof String) {
                attributeName = (String)fieldObject;
            } else {
                // it should be either QueryKeyExpression or FieldExpression
                fieldExpression = (DataExpression)fieldObject;
            }

            DatabaseField field = null;
            DatabaseMapping mapping = null;
            if(attributeName != null) {
                mapping = getDescriptor().getObjectBuilder().getMappingForAttributeName(attributeName);
                if (mapping != null && !mapping.getFields().isEmpty()) {
                    field = mapping.getFields().get(0);
                }
                if(field == null) {
                    throw QueryException.updateAllQueryAddUpdateDoesNotDefineField(getDescriptor(), getQuery(), attributeName);
                }
                baseExpression = ((UpdateAllQuery)getQuery()).getExpressionBuilder().get(attributeName);
            } else if (fieldExpression != null) {
                // it should be either QueryKeyExpression or ExpressionBuilder
                if (fieldExpression.getBaseExpression() instanceof ExpressionBuilder) {
                    field = getDescriptor().getObjectBuilder().getFieldForQueryKeyName(fieldExpression.getName());
                }
                if(field == null) {
                    DataExpression fieldExpressionClone = (DataExpression)fieldExpression.clone();
                    fieldExpressionClone.getBuilder().setQueryClass(getQuery().getReferenceClass());
                    fieldExpressionClone.getBuilder().setSession(getSession().getRootSession(null));
                    field = fieldExpressionClone.getField();
                    if(field == null) {
                        throw QueryException.updateAllQueryAddUpdateDoesNotDefineField(getDescriptor(), getQuery(), fieldExpression.toString());
                    }
                }
                mapping = getDescriptor().getObjectBuilder().getMappingForField(field);
                baseExpression = fieldExpression;
            }

            Object valueObject = entry.getValue();
            Vector fields;
            Vector values;
            Vector baseExpressions;
            if(mapping != null && mapping.isOneToOneMapping()) {
                fields = mapping.getFields();
                int fieldsSize = fields.size();
                values = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldsSize);
                baseExpressions = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldsSize);
                for(int i=0; i<fieldsSize; i++) {
                    if(valueObject instanceof ConstantExpression) {
                        valueObject = ((ConstantExpression)valueObject).getValue();
                    }
                    if(valueObject == null) {
                        values.add(null);
                    } else {
                        DatabaseField targetField = ((OneToOneMapping)mapping).getSourceToTargetKeyFields().get(fields.get(i));
                        if(valueObject instanceof Expression) {
                            Expression exp = ((Expression)((Expression)valueObject).clone()).getField(targetField);
                            if(exp.isParameterExpression()) {
                                ((ParameterExpression)exp).setType(targetField.getType());
                            }
                            values.add(exp);
                        } else {
                            values.add(((OneToOneMapping)mapping).getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(valueObject, targetField, getSession()));
                        }
                    }
                    baseExpressions.add(new FieldExpression((DatabaseField)fields.elementAt(i), ((QueryKeyExpression)baseExpression).getBaseExpression()));
                }
            } else {
                fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
                fields.add(field);
                values = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
                values.add(valueObject);
                baseExpressions = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
                baseExpressions.add(baseExpression);
            }
            int fieldsSize = fields.size();            
            for(int i=0; i<fieldsSize; i++) {
                field = (DatabaseField)fields.elementAt(i);
                DatabaseTable table = field.getTable();
                if(!getDescriptor().getTables().contains(table)) {
                    if(attributeName != null) {
                        throw QueryException.updateAllQueryAddUpdateDefinesWrongField(getDescriptor(), getQuery(), attributeName, field.getQualifiedName());
                    } else {
                        throw QueryException.updateAllQueryAddUpdateDefinesWrongField(getDescriptor(), getQuery(), fieldExpression.toString(), field.getQualifiedName());
                    }
                }
                
                HashMap databaseFieldsToValues = (HashMap)tables_databaseFieldsToValues.get(table);
                if(databaseFieldsToValues == null) {
                    databaseFieldsToValues = new HashMap();
                    tables_databaseFieldsToValues.put(table, databaseFieldsToValues);
    
                    tablesToPrimaryKeyFields.put(table, getPrimaryKeyFieldsForTable(table));
                }
    
                Object value = values.elementAt(i);
                Expression valueExpression;            
                if(valueObject instanceof Expression) {
                    valueExpression = (Expression)value;
                } else {
                    valueExpression = builder.value(value);
                }
                // GF#1123 - UPDATE with JPQL does not handle enums correctly 
                // Set localBase so that the value can be converted properly later.
                // NOTE: If baseExpression is FieldExpression, conversion is not required.
                if(valueExpression.isValueExpression()) {
                    valueExpression.setLocalBase((Expression)baseExpressions.elementAt(i));
                }
                
                databaseFieldsToValues.put(field, valueExpression);
            }
        }
        
        SQLCall selectCallForExist = null;
        SQLSelectStatement selectStatementForExist = createSQLSelectStatementForModifyAll(getSelectionCriteria());
        
        // Main Case: Descriptor is mapped to more than one table and/or the query references other tables
        boolean isMainCase = selectStatementForExist.requiresAliases();            
        if(isMainCase) {
            if(getExecutionSession().getPlatform().shouldAlwaysUseTempStorageForModifyAll()) {
                prepareUpdateAllUsingTempStorage(tables_databaseFieldsToValues, tablesToPrimaryKeyFields);
                return;
            }
        }
        selectCallForExist = (SQLCall)selectStatementForExist.buildCall(getSession());
        
        // ExpressionIterator to search for valueExpressions that require select statements.
        // Those are expressions that
        //   either reference other tables:
        //     Employee-based example: valueExp = builder.get("address").get("city");
        //   or use DataExpressions with base not ExpressionBuilder:
        //     Employee-base example: valueExp = builder.get("manager").get("firstName");
        // Before iterating the table is set into result,
        // if expression requiring select is found, then resul set to null.
        ExpressionIterator expRequiresSelectIterator = new ExpressionIterator() {
            public void iterate(Expression each) {
                if(getResult() == null) {
                    return;
                }
                if(each instanceof DataExpression) {
                    DataExpression dataExpression = (DataExpression)each;
                    Expression baseExpression = dataExpression.getBaseExpression();
                    if(baseExpression != null && !(baseExpression instanceof ExpressionBuilder)) {
                        boolean stop = true;
                        if(baseExpression instanceof DataExpression) {
                            DataExpression baseDataExpression = (DataExpression)baseExpression;
                            if(baseDataExpression.getMapping() != null && baseDataExpression.getMapping().isAggregateObjectMapping()) {
                                stop = false;
                            }
                        }
                        if(stop) {
                            setResult(null);
                            return;
                        }
                    }
                    DatabaseField field = dataExpression.getField();
                    if(field != null) {
                        if(!field.getTable().equals((DatabaseTable)getResult())) {
                            setResult(null);
                            return;
                        }
                    }
                }
            }
            public boolean shouldIterateOverSubSelects() {
                return true;
            }
        };

        HashMap tables_databaseFieldsToValuesCopy = new HashMap();
        it = tables_databaseFieldsToValues.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            DatabaseTable table = (DatabaseTable)entry.getKey();
            HashMap databaseFieldsToValues = (HashMap)entry.getValue();
            HashMap databaseFieldsToValuesCopy = new HashMap();
            tables_databaseFieldsToValuesCopy.put(table, databaseFieldsToValuesCopy);
            Iterator itFieldsToValues = databaseFieldsToValues.entrySet().iterator();
            while(itFieldsToValues.hasNext()) {
                Map.Entry entry2 = (Map.Entry)itFieldsToValues.next();
                DatabaseField field = (DatabaseField)entry2.getKey();
                Expression value = (Expression)entry2.getValue();

                // initialize result with the table
                expRequiresSelectIterator.setResult(table);                
                // To find fields have to have session and ref class
                Expression valueClone = (Expression)value.clone();
                valueClone.getBuilder().setSession(getSession());
                valueClone.getBuilder().setQueryClass(getQuery().getReferenceClass());
                expRequiresSelectIterator.iterateOn(valueClone);
                if(expRequiresSelectIterator.getResult() == null) {
                    // this one should use SELECT as an assigned expression.
                    // The corresponding SelectionStatement should be assigned to value
                    if(getExecutionSession().getPlatform().shouldAlwaysUseTempStorageForModifyAll()) {
                        prepareUpdateAllUsingTempStorage(tables_databaseFieldsToValues, tablesToPrimaryKeyFields);
                        return;
                    }
                    
                    SQLSelectStatement selStatement = createSQLSelectStatementForAssignedExpressionForUpdateAll(value);
                    databaseFieldsToValuesCopy.put(field, selStatement);
                } else {
                    databaseFieldsToValuesCopy.put(field, valueClone);
                }
            }
        }
        HashMap tables_databaseFieldsToValuesOriginal = tables_databaseFieldsToValues;
        tables_databaseFieldsToValues = tables_databaseFieldsToValuesCopy;
        
        if (tables_databaseFieldsToValues.size() == 1) {
            Map.Entry entry = (Map.Entry)tables_databaseFieldsToValues.entrySet().iterator().next();
            DatabaseTable table = (DatabaseTable)entry.getKey();
            HashMap databaseFieldsToValues = (HashMap)entry.getValue();
            Collection primaryKeyFields = (Collection)tablesToPrimaryKeyFields.values().iterator().next();
            setSQLStatement(buildUpdateAllStatement(table, databaseFieldsToValues, selectCallForExist, selectStatementForExist, primaryKeyFields));
        } else {
            // To figure out the order of statements we need to find dependencies
            // between updating of tables.
            // Here's an example:
            // All objects with nameA = "Clob" should be changed so that nameA = "Alex" and nameB = "Bob";
            // nameA is mapped to A.name and nameB mapped to B.name:
            // UPDATE B SET B.name = "Bob" WHERE A.name = "Clob" and A.id = B.id;
            // UPDATE A SET A.name = "Alex" WHERE A.name = "Clob" and A.id = B.id;
            // The order can't be altered - or there will be no updating of B.
            // To formalize that: for each table we'll gather two Collections:
            // leftFields - all the table's fields to receive a new value;
            // rightFields - all the fields either in assigned or selecton expression.
            // A_leftFields = {A.name}; A_rightFields = {A.name}.
            // B_leftFields = {B.name}; B_rightFields = {A.name}.
            // There are several comparison outcomes:
            // 1. A_leftFields doesn't intersect B_rightFields  and
            //    B_leftFields doesn't intersect A_rightFields
            //      There is no dependency - doesn't matter which update goes first;
            // 2. A_leftFields intersects B_rightFields  and
            //    B_leftFields doesn't intersect A_rightFields
            //      B should be updated before A (the case in the example).
            // 3. A_leftFields intersects B_rightFields  and
            //    B_leftFields intersects A_rightFields
            //      Ordering conflict that can't be resolved without using transitionary storage.
            //
            // This ExpressionIterator will be used for collecting fields from
            // selection criteria and assigned expressions.
            ExpressionIterator expIterator = new ExpressionIterator() {
                public void iterate(Expression each) {
                    if(each instanceof DataExpression) {
                        DataExpression dataExpression = (DataExpression)each;
                        DatabaseField field = dataExpression.getField();
                        if(field != null) {
                            ((Collection)getResult()).add(field);
                        }
                    }
                }
                public boolean shouldIterateOverSubSelects() {
                    return true;
                }
            };
            
            // This will hold collection of fields from selection criteria expression.
            HashSet selectCallForExistFields = new HashSet();
            if(selectCallForExist != null) {
                expIterator.setResult(selectCallForExistFields);
                expIterator.iterateOn(selectStatementForExist.getWhereClause());
            }
            
            // Left of the assignment operator that is - the fields acquiring new values
            HashMap tablesToLeftFields = new HashMap();
            // The fields right of the assignment operator AND the fields from whereClause
            HashMap tablesToRightFields = new HashMap();
            
            // before and after vectors work together: n-th member of beforeTable should
            // be updated before than n-th member of afterTable
            Vector beforeTables = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            Vector afterTables = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            
            // Both keys and values are tables.
            // An entry indicates a timing conflict between the key and the value:
            // both key should be updated before value and value before key.
            HashMap simpleConflicts = new HashMap();
            
            it = tables_databaseFieldsToValues.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                // for each table to be updated
                DatabaseTable table = (DatabaseTable)entry.getKey();
                // here's a Map of left hand fields to right hand expressions
                HashMap databaseFieldsToValues = (HashMap)entry.getValue();
                
                // This will contain all the left hand fields
                HashSet leftFields = new HashSet(databaseFieldsToValues.size());
                // This will contain all the left hand fields plus fields form selection criteria
                HashSet rightFields = (HashSet)selectCallForExistFields.clone();
                expIterator.setResult(rightFields);
                Iterator itDatabaseFieldsToValues = databaseFieldsToValues.entrySet().iterator();
                while(itDatabaseFieldsToValues.hasNext()) {
                    // for each left hand - right hand expression pair
                    Map.Entry databaseFieldValueEntry = (Map.Entry)itDatabaseFieldsToValues.next();
                    // here's the left hand database field
                    DatabaseField field = (DatabaseField)databaseFieldValueEntry.getKey();
                    leftFields.add(field);
                    // here's the right hand expression 
                    Object value = databaseFieldValueEntry.getValue();
                    if(value instanceof Expression) {
                        Expression valueExpression = (Expression)value;
                        // use iterator to extract all the fields
                        expIterator.iterateOn(valueExpression);
                    } else {
                        // It should be SQLSelectStatement with a single field
                        SQLSelectStatement selStatement = (SQLSelectStatement)value;
                        // first one is the normalized value to be assigned
                        expIterator.iterateOn((Expression)selStatement.getFields().get(0));
                        // whereClause - generated during normalization
                        expIterator.iterateOn(selStatement.getWhereClause());
                    }
                }
                
                // now let's compare the table with the already processed tables
                Iterator itProcessedTables = tablesToLeftFields.keySet().iterator();
                while(itProcessedTables.hasNext()) {
                    DatabaseTable processedTable = (DatabaseTable)itProcessedTables.next();
                    HashSet processedTableLeftFields = (HashSet)tablesToLeftFields.get(processedTable);
                    HashSet processedTableRightFields = (HashSet)tablesToRightFields.get(processedTable);
                    boolean tableBeforeProcessedTable = false;
                    Iterator itProcessedTableLeftField = processedTableLeftFields.iterator();
                    while(itProcessedTableLeftField.hasNext()) {
                        if(rightFields.contains(itProcessedTableLeftField.next())) {
                            tableBeforeProcessedTable = true;
                            break;
                        }
                    }
                    boolean processedTableBeforeTable = false;
                    Iterator itLeftField = leftFields.iterator();
                    while(itLeftField.hasNext()) {
                        if(processedTableRightFields.contains(itLeftField.next())) {
                            processedTableBeforeTable = true;
                            break;
                        }
                    }
                    if(tableBeforeProcessedTable && !processedTableBeforeTable) {
                        // table should be updated before processedTable
                        beforeTables.add(table);
                        afterTables.add(processedTable);
                    } else if (!tableBeforeProcessedTable && processedTableBeforeTable) {
                        // processedTable should be updated before table
                        beforeTables.add(processedTable);
                        afterTables.add(table);
                    } else if (tableBeforeProcessedTable && processedTableBeforeTable) {
                        // there is an order conflict between table and processTable 
                        simpleConflicts.put(processedTable, table);
                    }
                }
                
                tablesToLeftFields.put(table, leftFields);
                tablesToRightFields.put(table, rightFields);
            }
            
            if(!simpleConflicts.isEmpty()) {
                prepareUpdateAllUsingTempStorage(tables_databaseFieldsToValuesOriginal, tablesToPrimaryKeyFields);
                return;
            }
            
            // This will contain tables in update order
            Vector orderedTables = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(tables_databaseFieldsToValues.size());
            // first process the tables found in beforeTables / afterTables
            while(!beforeTables.isEmpty()) {
                // Find firstTable - the one that appears in beforeTables, but not afterTables.
                // That means there is no requirement to update it after any other table and we
                // can put it first in update order. There could be several such tables - 
                // it doesn't matter which one will be picked.
                DatabaseTable firstTable = null;
                for(int i=0; i < beforeTables.size(); i++) {
                    DatabaseTable beforeTable = (DatabaseTable)beforeTables.elementAt(i);
                    if(!afterTables.contains(beforeTable)) {
                        firstTable = beforeTable;
                        break;
                    }      
                }
                if(firstTable == null) {
                    // There is no firstTable - it's an order conflict between three or more tables
                    prepareUpdateAllUsingTempStorage(tables_databaseFieldsToValuesOriginal, tablesToPrimaryKeyFields);
                    return;
                } else {
                    // Remove first table from beforeTables - there could be several entries.
                    // Also remove the corresponding entries from afterTable.
                    for(int i=beforeTables.size()-1; i>=0; i--) {
                        if(beforeTables.elementAt(i).equals(firstTable)) {
                            beforeTables.remove(i);
                            afterTables.remove(i);
                        }
                    }
                    // Add firstTable to orderedTables
                    orderedTables.addElement(firstTable);
                }
            }

            // now all the remaining ones - there are no dependencies between them
            // so the order is arbitrary.
            Iterator itTables = tables_databaseFieldsToValues.keySet().iterator();
            while(itTables.hasNext()) {
                DatabaseTable table = (DatabaseTable)itTables.next();
                if(!orderedTables.contains(table)) {
                    orderedTables.add(table);
                }
            }
            
            // finally create statements
            for(int i=0; i < orderedTables.size(); i++) {
                DatabaseTable table = (DatabaseTable)orderedTables.elementAt(i);
                HashMap databaseFieldsToValues = (HashMap)tables_databaseFieldsToValues.get(table);
                Collection primaryKeyFields = (Collection)tablesToPrimaryKeyFields.get(table);
                getSQLStatements().addElement(buildUpdateAllStatement(table, databaseFieldsToValues, selectCallForExist, selectStatementForExist, primaryKeyFields));
            }
        }

        ((UpdateAllQuery)getQuery()).setIsPreparedUsingTempStorage(false);        
        super.prepareUpdateAll();
    }

    protected SQLSelectStatement createSQLSelectStatementForUpdateAllForOracleAnonymousBlock(HashMap tables_databaseFieldsToValues) 
    {
        ExpressionBuilder builder = ((UpdateAllQuery)getQuery()).getExpressionBuilder();
        Expression whereClause = getSelectionCriteria();
        
        ReportQuery reportQuery = new ReportQuery(getDescriptor().getJavaClass(), builder);
        reportQuery.setDescriptor(getDescriptor());
        reportQuery.setSelectionCriteria(whereClause);
        reportQuery.setSession(getSession());
        
        reportQuery.setShouldRetrievePrimaryKeys(true);
        Iterator itDatabaseFieldsToValues = tables_databaseFieldsToValues.values().iterator();
        while(itDatabaseFieldsToValues.hasNext()) {
            HashMap databaseFieldsToValues = (HashMap)itDatabaseFieldsToValues.next();
            Iterator itValues = databaseFieldsToValues.values().iterator();
            while(itValues.hasNext()) {
                reportQuery.addAttribute("", (Expression)itValues.next());
            }
        }

        SQLSelectStatement selectStatement = ((ExpressionQueryMechanism)reportQuery.getQueryMechanism()).buildReportQuerySelectStatement(false);
        reportQuery.setSession(null);
        return selectStatement;
    }
        
    protected SQLSelectStatement createSQLSelectStatementForModifyAllForTempTable(HashMap databaseFieldsToValues)
    {
        ExpressionBuilder builder = ((ModifyAllQuery)getQuery()).getExpressionBuilder();
        Expression whereClause = getSelectionCriteria();
                
        ReportQuery reportQuery = new ReportQuery(getDescriptor().getJavaClass(), builder);
        reportQuery.setDescriptor(getDescriptor());
        reportQuery.setSelectionCriteria(whereClause);
        reportQuery.setSession(getSession());
        
        reportQuery.setShouldRetrievePrimaryKeys(true);
        if(databaseFieldsToValues != null) {
            Iterator itValues = databaseFieldsToValues.values().iterator();
            while(itValues.hasNext()) {
                reportQuery.addAttribute("", (Expression)itValues.next());
            }
        }

        SQLSelectStatement selectStatement = ((ExpressionQueryMechanism)reportQuery.getQueryMechanism()).buildReportQuerySelectStatement(false);
        reportQuery.setSession(null);
        return selectStatement;
    }
        
    protected SQLModifyStatement buildUpdateAllStatementForOracleAnonymousBlock(HashMap tables_databaseFieldsToValues, HashMap tablesToPrimaryKeyFields) {
        SQLSelectStatement selectStatement = createSQLSelectStatementForUpdateAllForOracleAnonymousBlock(tables_databaseFieldsToValues);
        SQLCall selectCall = (SQLCall)selectStatement.buildCall(getSession());
        
        SQLUpdateAllStatementForOracleAnonymousBlock updateAllStatement = new SQLUpdateAllStatementForOracleAnonymousBlock();
        updateAllStatement.setTranslationRow(getTranslationRow());

        updateAllStatement.setSelectCall(selectCall);
        updateAllStatement.setTables_databaseFieldsToValues(tables_databaseFieldsToValues);
        updateAllStatement.setTablesToPrimaryKeyFields(tablesToPrimaryKeyFields);
        
        updateAllStatement.setTable(getDescriptor().getTables().firstElement());

        return updateAllStatement;
    }
    
    protected void prepareUpdateAllUsingTempStorage(HashMap tables_databaseFieldsToValues, HashMap<DatabaseTable, List<DatabaseField>> tablesToPrimaryKeyFields) {
        if(getExecutionSession().getPlatform().supportsTempTables()) {
            prepareUpdateAllUsingTempTables(tables_databaseFieldsToValues, tablesToPrimaryKeyFields);
        } else if(getExecutionSession().getPlatform().isOracle()) {
            prepareUpdateAllUsingOracleAnonymousBlock(tables_databaseFieldsToValues, tablesToPrimaryKeyFields);
        } else {
            throw QueryException.tempTablesNotSupported(getQuery(), Helper.getShortClassName(getExecutionSession().getPlatform()));
        }
    }
    
    /**
     * Pre-build the SQL statement from the expressions.
     */
    protected void prepareUpdateAllUsingOracleAnonymousBlock(HashMap tables_databaseFieldsToValues, HashMap tablesToPrimaryKeyFields) {
        
        setSQLStatement(buildUpdateAllStatementForOracleAnonymousBlock(tables_databaseFieldsToValues, tablesToPrimaryKeyFields));
        ((UpdateAllQuery)getQuery()).setIsPreparedUsingTempStorage(true);
        super.prepareUpdateAll();
    }

    /**
     * Pre-build the SQL statement from the expressions.
     */
    protected void prepareUpdateAllUsingTempTables(HashMap tables_databaseFieldsToValues, HashMap<DatabaseTable, List<DatabaseField>> tablesToPrimaryKeyFields) {
        int nTables = tables_databaseFieldsToValues.size();
        Vector createTableStatements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(nTables);
        Vector selectStatements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(nTables);
        Vector updateStatements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(nTables);
        Vector cleanupStatements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(nTables);
        
        Iterator itEntrySets = tables_databaseFieldsToValues.entrySet().iterator();
        while(itEntrySets.hasNext()) {
            Map.Entry entry = (Map.Entry)itEntrySets.next();
            DatabaseTable table = (DatabaseTable)entry.getKey();
            HashMap databaseFieldsToValues = (HashMap)entry.getValue();            
            List<DatabaseField> primaryKeyFields = tablesToPrimaryKeyFields.get(table);

            Vector statementsForTable = buildStatementsForUpdateAllForTempTables(table, databaseFieldsToValues, primaryKeyFields);
            
            createTableStatements.add(statementsForTable.elementAt(0));
            selectStatements.add(statementsForTable.elementAt(1));
            updateStatements.add(statementsForTable.elementAt(2));
            cleanupStatements.add(statementsForTable.elementAt(3));
        }
        
        getSQLStatements().addAll(createTableStatements);
        getSQLStatements().addAll(selectStatements);
        getSQLStatements().addAll(updateStatements);
        getSQLStatements().addAll(cleanupStatements);

        if (getExecutionSession().getPlatform().dontBindUpdateAllQueryUsingTempTables()) {
            if(getQuery().shouldBindAllParameters() || (getQuery().shouldIgnoreBindAllParameters() && getExecutionSession().getPlatform().shouldBindAllParameters())) {
                getQuery().setShouldBindAllParameters(false);
                getSession().warning("update_all_query_cannot_use_binding_on_this_platform", SessionLog.QUERY);
            }
        }
        ((UpdateAllQuery)getQuery()).setIsPreparedUsingTempStorage(true);
        super.prepareUpdateAll();
    }

    /**
     * Build SQLStatements for delete all using temporary table. 
     * @return Vector<SQLStatement>
     */
    protected Vector buildStatementsForDeleteAllForTempTables() {
        Vector statements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        
        // retrieve rootTable and its primary key fields for composing temporary table
        DatabaseTable rootTable = getDescriptor().getMultipleTableInsertOrder().get(0);
        List<DatabaseField> rootTablePrimaryKeyFields = getPrimaryKeyFieldsForTable(rootTable);
        ClassDescriptor rootDescriptor = getDescriptor();
        if(getDescriptor().hasInheritance()) {
            rootDescriptor = rootDescriptor.getInheritancePolicy().getRootParentDescriptor();
        }
        Vector allFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        Iterator it = rootDescriptor.getFields().iterator();
        while(it.hasNext()) {
            DatabaseField field = (DatabaseField)it.next();
            if(rootTable.equals(field.getTable())) {
                allFields.add(field);
            }
        }        
        
        // statements will be executed in reverse order
        
        // statement for temporary table cleanup (Drop table or Delete from temp_table)
        SQLDeleteAllStatementForTempTable cleanupStatement = new SQLDeleteAllStatementForTempTable();
        cleanupStatement.setMode(SQLModifyAllStatementForTempTable.CLEANUP_TEMP_TABLE);
        cleanupStatement.setTable(rootTable);
        statements.addElement(cleanupStatement);
                        
        // delete statements using temporary table
        Vector deleteStatements = buildDeleteAllStatementsForTempTable(getDescriptor(), rootTable, rootTablePrimaryKeyFields, null);
        statements.addAll(deleteStatements);

        // Insert statement populating temporary table with criteria
        SQLSelectStatement selectStatement = createSQLSelectStatementForModifyAllForTempTable(null);
        SQLCall selectCall = (SQLCall)selectStatement.buildCall(getSession());
        SQLDeleteAllStatementForTempTable insertStatement = new SQLDeleteAllStatementForTempTable();
        insertStatement.setMode(SQLModifyAllStatementForTempTable.INSERT_INTO_TEMP_TABLE);
        insertStatement.setTable(rootTable);
        insertStatement.setTranslationRow(getTranslationRow());
        insertStatement.setSelectCall(selectCall);
        insertStatement.setPrimaryKeyFields(rootTablePrimaryKeyFields);
        statements.addElement(insertStatement);
        
        // Create temporary table statement
        SQLDeleteAllStatementForTempTable createTempTableStatement = new SQLDeleteAllStatementForTempTable();
        createTempTableStatement.setMode(SQLModifyAllStatementForTempTable.CREATE_TEMP_TABLE);
        createTempTableStatement.setTable(rootTable);
        createTempTableStatement.setAllFields(allFields);
        createTempTableStatement.setPrimaryKeyFields(rootTablePrimaryKeyFields);
        statements.addElement(createTempTableStatement);
                
        return statements;
    }

    /**
     * Build delete all SQLStatements using temporary table. 
     * This is recursively called for multiple table child descriptors.
     * 
     * NOTE: A similar pattern also used in method prepareDeleteAll():
     *  if you are updating this method consider applying a similar update to that method as well.
     *  
     * @return Vector<SQLDeleteAllStatementForTempTable>
     */
    private Vector buildDeleteAllStatementsForTempTable(ClassDescriptor descriptor, DatabaseTable rootTable, List<DatabaseField> rootTablePrimaryKeyFields, Vector tablesToIgnore) {
        Vector statements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        
        List<DatabaseTable> tablesInInsertOrder;
        if (tablesToIgnore == null) {
            // It's original (not a nested) method call.
            tablesInInsertOrder = descriptor.getMultipleTableInsertOrder();
        } else {
            // It's a nested method call: tableInInsertOrder filled with descriptor's tables (in insert order),
            // the tables found in tablesToIgnore are thrown away - 
            // they have already been taken care of by the caller.
            // In Employee example, query with reference class Project gets here 
            // to handle LPROJECT table; tablesToIgnore contains PROJECT table.
            tablesInInsertOrder = new ArrayList(descriptor.getMultipleTableInsertOrder().size());
            for (DatabaseTable table : descriptor.getMultipleTableInsertOrder()) {
                if (!tablesToIgnore.contains(table)) {
                    tablesInInsertOrder.add(table);
                }
            }
        }

        if (!tablesInInsertOrder.isEmpty()) {
            for (DatabaseTable table : tablesInInsertOrder) {
                SQLDeleteAllStatementForTempTable deleteStatement 
                    = buildDeleteAllStatementForTempTable(rootTable, rootTablePrimaryKeyFields, table, getPrimaryKeyFieldsForTable(descriptor, table));
                statements.add(deleteStatement);
                // Most databases support delete cascade constraints by specifying a ON DELETE CASCADE option when defining foreign key constraints. 
                // However some databases which don't support foreign key constraints cannot use delete cascade constraints.
                // Therefore each delete operation should be executed in such a database platform instead of delegating delete cascade constraints.
                boolean supportForeignKeyConstraints = getSession().getPlatform().supportsForeignKeyConstraints();
                boolean supportCascadeOnDelete = supportForeignKeyConstraints && descriptor.isCascadeOnDeleteSetOnDatabaseOnSecondaryTables();
                // Only delete from first table if delete is cascaded on the database.
                if (supportCascadeOnDelete) {
                    break;
                }
            }
    
            // Add statements for ManyToMany and DirectCollection mappings
            Vector deleteStatementsForMappings 
                = buildDeleteAllStatementsForMappingsWithTempTable(descriptor, rootTable, tablesToIgnore == null);
            statements.addAll(deleteStatementsForMappings);
        }
        
        // Indicates whether the descriptor has children using extra tables.
        boolean hasChildrenWithExtraTables = descriptor.hasInheritance() && descriptor.getInheritancePolicy().hasChildren() && descriptor.getInheritancePolicy().hasMultipleTableChild();

        // TBD: should we ignore subclasses in case descriptor doesn't want us to read them in?
        //** Currently in this code we do ignore.
        //** If it will be decided that we need to handle children in all cases
        //** the following statement should be changed to: boolean shouldHandleChildren = hasChildrenWithExtraTables;
        boolean shouldHandleChildren = hasChildrenWithExtraTables && descriptor.getInheritancePolicy().shouldReadSubclasses();

        // Perform a nested method call for each child
        if (shouldHandleChildren) {
            // In Employee example: query for Project will make nested calls to
            // LargeProject and SmallProject and ask them to ignore PROJECT table
            Vector tablesToIgnoreForChildren = new Vector();
            // The tables this descriptor has ignored, its children also should ignore.
            if (tablesToIgnore != null) {
                tablesToIgnoreForChildren.addAll(tablesToIgnore);
            }

            // If the descriptor reads subclasses there is no need for
            // subclasses to process its tables for the second time.
            if (descriptor.getInheritancePolicy().shouldReadSubclasses()) {
                tablesToIgnoreForChildren.addAll(tablesInInsertOrder);
            }
            
            Iterator it = descriptor.getInheritancePolicy().getChildDescriptors().iterator();
            while (it.hasNext()) {
                ClassDescriptor childDescriptor = (ClassDescriptor)it.next();
                
                // Need to process only "multiple tables" child descriptors
                if ((childDescriptor.getTables().size() > descriptor.getTables().size()) || 
                    (childDescriptor.getInheritancePolicy().hasMultipleTableChild())) 
                {
                    //recursively build for child desciptors
                    Vector childStatements = buildDeleteAllStatementsForTempTable(childDescriptor, rootTable, rootTablePrimaryKeyFields, tablesToIgnoreForChildren);
                    statements.addAll(childStatements);
                }
            }
        }
        
        return statements;
    }

    /**
     * Build SQL delete statement which delete from target table using temporary table.
     * @return SQLDeleteAllStatementForTempTable
     */
    private SQLDeleteAllStatementForTempTable buildDeleteAllStatementForTempTable(DatabaseTable rootTable, List<DatabaseField> rootTablePrimaryKeyFields, DatabaseTable targetTable, List<DatabaseField> targetTablePrimaryKeyFields) {
        SQLDeleteAllStatementForTempTable deleteStatement = new SQLDeleteAllStatementForTempTable();
        deleteStatement.setMode(SQLModifyAllStatementForTempTable.UPDATE_ORIGINAL_TABLE);
        deleteStatement.setTable(rootTable);
        deleteStatement.setPrimaryKeyFields(rootTablePrimaryKeyFields);
        deleteStatement.setTargetTable(targetTable);
        deleteStatement.setTargetPrimaryKeyFields(targetTablePrimaryKeyFields);
        return deleteStatement;
    }
    
    protected Vector buildStatementsForUpdateAllForTempTables(DatabaseTable table, HashMap databaseFieldsToValues, List<DatabaseField> primaryKeyFields) {
        Vector statements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(4);
        
        Vector allFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        Iterator it = getDescriptor().getFields().iterator();
        while(it.hasNext()) {
            DatabaseField field = (DatabaseField)it.next();
            if(table.equals(field.getTable())) {
                allFields.add(field);
            }
        }        
        
        Collection assignedFields = databaseFieldsToValues.keySet();
        HashMap databaseFieldsToValuesForInsert = databaseFieldsToValues;
        Collection assignedFieldsForInsert = assignedFields;

        // The platform doesn't allow nulls in select clause.
        // Remove all the constant expressions with value null:
        // can do that because all fields initialized to null when temp. table created.
        if(!getExecutionSession().getPlatform().isNullAllowedInSelectClause()) {
            databaseFieldsToValuesForInsert = new HashMap(databaseFieldsToValues.size());
            Iterator itEntries = databaseFieldsToValues.entrySet().iterator();
            while(itEntries.hasNext()) {
                Map.Entry entry = (Map.Entry)itEntries.next();
                if(entry.getValue() instanceof ConstantExpression) {
                    ConstantExpression constExp = (ConstantExpression)entry.getValue();
                    if(constExp.getValue() == null) {
                        continue;
                    }
                }
                databaseFieldsToValuesForInsert.put(entry.getKey(), entry.getValue());
            }
            assignedFieldsForInsert = databaseFieldsToValuesForInsert.keySet();
        }

        SQLUpdateAllStatementForTempTable createTempTableStatement = new SQLUpdateAllStatementForTempTable();
        createTempTableStatement.setMode(SQLModifyAllStatementForTempTable.CREATE_TEMP_TABLE);
        createTempTableStatement.setTable(table);
        createTempTableStatement.setAllFields(allFields);
        createTempTableStatement.setAssignedFields(assignedFields);
        createTempTableStatement.setPrimaryKeyFields(primaryKeyFields);
        statements.addElement(createTempTableStatement);
                
        SQLSelectStatement selectStatement = createSQLSelectStatementForModifyAllForTempTable(databaseFieldsToValuesForInsert);
        SQLCall selectCall = (SQLCall)selectStatement.buildCall(getSession(), getQuery());
        SQLUpdateAllStatementForTempTable insertStatement = new SQLUpdateAllStatementForTempTable();
        insertStatement.setMode(SQLModifyAllStatementForTempTable.INSERT_INTO_TEMP_TABLE);
        insertStatement.setTable(table);
        insertStatement.setTranslationRow(getTranslationRow());
        insertStatement.setSelectCall(selectCall);
        insertStatement.setAssignedFields(assignedFieldsForInsert);
        insertStatement.setPrimaryKeyFields(primaryKeyFields);
        statements.addElement(insertStatement);
        
        SQLUpdateAllStatementForTempTable updateStatement = new SQLUpdateAllStatementForTempTable();
        updateStatement.setMode(SQLModifyAllStatementForTempTable.UPDATE_ORIGINAL_TABLE);
        updateStatement.setTable(table);
        updateStatement.setTranslationRow(getTranslationRow());
        updateStatement.setAssignedFields(assignedFields);
        updateStatement.setPrimaryKeyFields(primaryKeyFields);
        statements.addElement(updateStatement);
        
        SQLUpdateAllStatementForTempTable cleanupStatement = new SQLUpdateAllStatementForTempTable();
        cleanupStatement.setMode(SQLModifyAllStatementForTempTable.CLEANUP_TEMP_TABLE);
        cleanupStatement.setTable(table);
        statements.addElement(cleanupStatement);
                
        return statements;
    }

    protected List<DatabaseField> getPrimaryKeyFieldsForTable(DatabaseTable table) {
        return getPrimaryKeyFieldsForTable(getDescriptor(), table);
    }

    protected List<DatabaseField> getPrimaryKeyFieldsForTable(ClassDescriptor descriptor, DatabaseTable table) {
        List<DatabaseField> mainTablePrimaryKeyFields = descriptor.getPrimaryKeyFields();
        if(table.equals(descriptor.getTables().firstElement())) {
            return mainTablePrimaryKeyFields;
        } else {
            List<DatabaseField> primaryKeyFields;
            Map<DatabaseField, DatabaseField> additionalPksMap = descriptor.getAdditionalTablePrimaryKeyFields().get(table);
            primaryKeyFields = new ArrayList(additionalPksMap.size());
            for (DatabaseField field : mainTablePrimaryKeyFields) {
                primaryKeyFields.add(additionalPksMap.get(field));
            }
            return primaryKeyFields;
        }
    }
   
    /**
     * INTERNAL
     * Read all rows from the database. The code to retrieve the full inheritance hierarchy was removed.
     *
     * @return Vector containing the database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public Vector selectAllReportQueryRows() throws DatabaseException {
        return selectAllRowsFromTable();
    }

    /**
     * Read all rows from the database.
     * @return Vector containing the database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public Vector selectAllRows() throws DatabaseException {
        // Check for multiple table inheritance which may require multiple queries.
        if (!((ObjectLevelReadQuery)this.query).shouldOuterJoinSubclasses()) {
            ClassDescriptor descriptor = getDescriptor();
            if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().requiresMultipleTableSubclassRead() && (!descriptor.getInheritancePolicy().hasView())) {
                return descriptor.getInheritancePolicy().selectAllRowUsingMultipleTableSubclassRead((ObjectLevelReadQuery)this.query);
            }
        }
        return selectAllRowsFromTable();
    }

    /**
     * Read all rows from the database.
     * This is used only from query mechanism on a abstract-multiple table read.
     */
    public Vector selectAllRowsFromConcreteTable() throws DatabaseException {
        ObjectLevelReadQuery query = (ObjectLevelReadQuery)this.query;
        // PERF: First check the subclass calls cache for the prepared call.
        // Must clear the translation row to avoid in-lining parameters unless not a prepared query.
        boolean shouldPrepare = query.shouldPrepare();
        DatabaseCall call = null;
        if (shouldPrepare) {
            call = query.getConcreteSubclassCalls().get(query.getReferenceClass());
        }
        if (call == null) {
            AbstractRecord translationRow = query.getTranslationRow();
            if (shouldPrepare) {
                query.setTranslationRow(null);
            }
            setSQLStatement(buildConcreteSelectStatement());
            // Must also build the call.
            super.prepareSelectAllRows();
            if (shouldPrepare) {
                if (query.hasJoining()) {
                    query.getConcreteSubclassJoinedMappingIndexes().put(query.getReferenceClass(), query.getJoinedAttributeManager().getJoinedMappingIndexes_());
                }
                query.getConcreteSubclassCalls().put(query.getReferenceClass(), (DatabaseCall)this.call);
                query.setTranslationRow(translationRow);                
            }
        } else {
            setCall(call);
            if (shouldPrepare && query.hasJoining()) {
                query.getJoinedAttributeManager().setJoinedMappingIndexes_(query.getConcreteSubclassJoinedMappingIndexes().get(query.getReferenceClass()));
            }
        }

        return super.selectAllRows();
    }

    /**
     * Read all rows from the database.
     * @return Vector containing the database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public Vector selectAllRowsFromTable() throws DatabaseException {
        return super.selectAllRows();
    }

    /**
     * Read a single row from the database. Create an SQL statement object,
     * use it to create an SQL command string, and delegate row building
     * responsibility to the accessor.
     */
    public AbstractRecord selectOneRow() throws DatabaseException {
        // Check for multiple table inheritance which may require multiple queries.
        if (!getReadObjectQuery().shouldOuterJoinSubclasses()) {
            ClassDescriptor descriptor = getDescriptor();
            if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().requiresMultipleTableSubclassRead() && (!descriptor.getInheritancePolicy().hasView())) {
                return descriptor.getInheritancePolicy().selectOneRowUsingMultipleTableSubclassRead((ReadObjectQuery)this.query);
            }
        }
        return selectOneRowFromTable();
    }

    /**
     * Read a single row from the database.
     * This is used from query  mechanism during an abstract-multiple table read.
     */
    public AbstractRecord selectOneRowFromConcreteTable() throws DatabaseException {
        ObjectLevelReadQuery query = (ObjectLevelReadQuery)this.query;
        // PERF: First check the subclass calls cache for the prepared call.
        // Must clear the translation row to avoid in-lining parameters unless not a prepared query.
        boolean shouldPrepare = query.shouldPrepare();
        DatabaseCall call = null;
        if (shouldPrepare) {
            call = query.getConcreteSubclassCalls().get(query.getReferenceClass());
        }
        if (call == null) {
            AbstractRecord translationRow = query.getTranslationRow();
            if (shouldPrepare) {
                query.setTranslationRow(null);
            }
            setSQLStatement(buildConcreteSelectStatement());
            // Must also build the call.
            super.prepareSelectOneRow();
            if (shouldPrepare) {
                if (query.hasJoining()) {
                    query.getConcreteSubclassJoinedMappingIndexes().put(query.getReferenceClass(), query.getJoinedAttributeManager().getJoinedMappingIndexes_());
                }
                query.getConcreteSubclassCalls().put(query.getReferenceClass(), (DatabaseCall)this.call);
                query.setTranslationRow(translationRow);                
            }
        } else {
            setCall(call);
            if (shouldPrepare && query.hasJoining()) {
                query.getJoinedAttributeManager().setJoinedMappingIndexes_(query.getConcreteSubclassJoinedMappingIndexes().get(query.getReferenceClass()));
            }
        }

        return super.selectOneRow();
    }

    /**
     * Read a single row from the database. Create an SQL statement object,
     * use it to create an SQL command string, and delegate row building
     * responsibility to the accessor.
     */
    public AbstractRecord selectOneRowFromTable() throws DatabaseException {
        return super.selectOneRow();
    }

    /**
     * Set the selection criteria of the query.
     */
    public void setSelectionCriteria(Expression expression) {
        this.selectionCriteria = expression;
    }

    /**
     * Pass to this method a table mapped by query's descriptor.
     * Returns the highest descriptor in inheritance hierarchy that mapps this table.
     */
    protected ClassDescriptor getHighestDescriptorMappingTable(DatabaseTable table) {
        // find the highest descriptor in inheritance hierarchy mapped to the table 
        ClassDescriptor desc = getDescriptor();
        ClassDescriptor parentDescriptor = getDescriptor().getInheritancePolicy().getParentDescriptor();
        while(parentDescriptor != null && parentDescriptor.getTables().contains(table)) {
            desc = parentDescriptor;
            parentDescriptor =  parentDescriptor.getInheritancePolicy().getParentDescriptor();
        }
        return desc;
    }    
}
