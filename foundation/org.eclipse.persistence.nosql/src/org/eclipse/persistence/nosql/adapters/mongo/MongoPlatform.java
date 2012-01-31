/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.nosql.adapters.mongo;

import java.util.List;
import java.util.Vector;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.EISPlatform;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoInteractionSpec;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoOperation;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoRecord;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseRecord;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Platform for Mongo database.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoPlatform extends EISPlatform {

    /** Mongo interaction spec properties. */
    public static String OPERATION = "mongo.operation";
    public static String COLLECTION = "mongo.collection";
    public static String OPTIONS = "mongo.options";
    public static String READ_PREFERENCE = "mongo.read-preference";
    public static String WRITE_CONCERN = "mongo.write-concern";
    public static String SKIP = "mongo.skip";
    public static String LIMIT = "mongo.limit";
    public static String BATCH_SIZE = "mongo.batch-size";

    /** Configure if like should be SQL or regex. */
    protected boolean isLikeRegex;

    /**
     * Default constructor.
     */
    public MongoPlatform() {
        super();
        setShouldConvertDataToStrings(true);
        setIsMappedRecordSupported(true);
        setIsIndexedRecordSupported(false);
        setIsDOMRecordSupported(true);
        setSupportsLocalTransactions(true);
    }
    
    /**
     * Return if regex should be used for like.
     */
    public boolean isLikeRegex() {
        return isLikeRegex;
    }
    
    /**
     * Set if regex should be used for like.
     */
    public void setIsLikeRegex(boolean isLikeRegex) {
        this.isLikeRegex = isLikeRegex;
    }

    /**
     * Allow the platform to build the interaction spec based on properties defined in the interaction.
     */
    @Override
    public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
        InteractionSpec spec = interaction.getInteractionSpec();
        if (spec == null) {
            MongoInteractionSpec mongoSpec = new MongoInteractionSpec();
            Object operation = interaction.getProperty(OPERATION);
            if (operation == null) {
                throw new EISException("'" + OPERATION + "' property must be set on the query's interation.");
            }
            if (operation instanceof String) {
                operation = MongoOperation.valueOf((String)operation);
            }
            mongoSpec.setOperation((MongoOperation)operation);
            Object collection = interaction.getProperty(COLLECTION);
            if (collection != null) {
                mongoSpec.setCollection((String)collection);
            }
            
            // Allows setting of read preference as a property.
            Object preference = interaction.getProperty(READ_PREFERENCE);
            if (preference instanceof ReadPreference) {
                mongoSpec.setReadPreference((ReadPreference)preference);
            } else if (preference instanceof String) {
                String constant = (String)preference;
                if (constant.equals("PRIMARY")) {
                    mongoSpec.setReadPreference(ReadPreference.PRIMARY);
                } else if (constant.equals("SECONDARY")) {
                    mongoSpec.setReadPreference(ReadPreference.SECONDARY );
                } else {
                    throw new EISException("Invalid read preference property value: " + constant);                    
                }
            }
            
            // Allows setting of write concern as a property.
            Object concern = interaction.getProperty(WRITE_CONCERN);
            if (concern instanceof WriteConcern) {
                mongoSpec.setWriteConcern((WriteConcern)concern);
            } else if (concern instanceof String) {
                String constant = (String)concern;
                if (constant.equals("FSYNC_SAFE")) {
                    mongoSpec.setWriteConcern(WriteConcern.FSYNC_SAFE);
                } else if (constant.equals("JOURNAL_SAFE")) {
                    mongoSpec.setWriteConcern(WriteConcern.JOURNAL_SAFE);
                } else if (constant.equals("MAJORITY")) {
                    mongoSpec.setWriteConcern(WriteConcern.MAJORITY);
                } else if (constant.equals("NONE")) {
                    mongoSpec.setWriteConcern(WriteConcern.NONE);
                } else if (constant.equals("NORMAL")) {
                    mongoSpec.setWriteConcern(WriteConcern.NORMAL);
                } else if (constant.equals("REPLICAS_SAFE")) {
                    mongoSpec.setWriteConcern(WriteConcern.REPLICAS_SAFE);
                } else if (constant.equals("SAFE")) {
                    mongoSpec.setWriteConcern(WriteConcern.SAFE);
                } else {
                    throw new EISException("Invalid read preference property value: " + constant);                    
                }
            }
            
            // Allows setting of options as a property.
            Object options = interaction.getProperty(OPTIONS);
            if (options instanceof Number) {
                mongoSpec.setOptions(((Number)options).intValue());
            } else if (options instanceof String) {
                mongoSpec.setOptions(Integer.valueOf(((String)options)));
            }
            
            // Allows setting of skip as a property.
            Object skip = interaction.getProperty(SKIP);
            if (skip instanceof Number) {
                mongoSpec.setSkip(((Number)skip).intValue());
            } else if (skip instanceof String) {
                mongoSpec.setSkip(Integer.valueOf(((String)skip)));
            }
            
            // Allows setting of limit as a property.
            Object limit = interaction.getProperty(LIMIT);
            if (limit instanceof Number) {
                mongoSpec.setLimit(((Number)limit).intValue());
            } else if (skip instanceof String) {
                mongoSpec.setLimit(Integer.valueOf(((String)limit)));
            }
            
            // Allows setting of batchSize as a property.
            Object batchSize = interaction.getProperty(BATCH_SIZE);
            if (batchSize instanceof Number) {
                mongoSpec.setBatchSize(((Number)batchSize).intValue());
            } else if (skip instanceof String) {
                mongoSpec.setBatchSize(Integer.valueOf(((String)batchSize)));
            }
            
            spec = mongoSpec;
        }
        return spec;
    }


    /**
     * For updates a separate translation record is required.
     * The output row is used for this.
     */
    @Override
    public Record createOutputRecord(EISInteraction interaction, AbstractRecord translationRow, EISAccessor accessor) {
        if (((interaction.getInteractionSpec() != null) && ((MongoInteractionSpec)interaction.getInteractionSpec()).getOperation() == MongoOperation.UPDATE)
                || ((interaction.getProperty(OPERATION) != null)
                        && ((interaction.getProperty(OPERATION) == MongoOperation.UPDATE) || (interaction.getProperty(OPERATION).equals(MongoOperation.UPDATE.name()))))) {
            return (Record)interaction.createRecordElement(interaction.getInputRecordName(), translationRow, accessor);
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Allow the platform to initialize the CRUD queries to defaults.
     * Configure the CRUD operations using GET/PUT and DELETE.
     */
    @Override
    public void initializeDefaultQueries(DescriptorQueryManager queryManager, AbstractSession session) {
        // Insert
        if (!queryManager.hasInsertQuery()) {
            EISInteraction call = new MappedInteraction();
            call.setProperty(MongoPlatform.OPERATION, MongoOperation.INSERT);
            call.setProperty(MongoPlatform.COLLECTION, ((EISDescriptor)queryManager.getDescriptor()).getDataTypeName());
            queryManager.setInsertCall(call);
        }
        
        // Update
        if (!queryManager.hasUpdateQuery()) {
            EISInteraction call = new MappedInteraction();
            call.setProperty(MongoPlatform.OPERATION, MongoOperation.UPDATE);
            call.setProperty(MongoPlatform.COLLECTION, ((EISDescriptor)queryManager.getDescriptor()).getDataTypeName());
            queryManager.setUpdateCall(call);
        }

        // Read
        if (!queryManager.hasReadObjectQuery()) {
            MappedInteraction call = new MappedInteraction();
            call.setProperty(MongoPlatform.OPERATION, MongoOperation.FIND);
            call.setProperty(MongoPlatform.COLLECTION, ((EISDescriptor)queryManager.getDescriptor()).getDataTypeName());
            for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
                call.addArgument(field.getName());
            }
            queryManager.setReadObjectCall(call);
        }
        
        // Delete
        if (!queryManager.hasDeleteQuery()) {
            MappedInteraction call = new MappedInteraction();
            call.setProperty(MongoPlatform.OPERATION, MongoOperation.REMOVE);
            call.setProperty(MongoPlatform.COLLECTION, ((EISDescriptor)queryManager.getDescriptor()).getDataTypeName());
            for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
                call.addArgument(field.getName());
            }
            queryManager.setDeleteCall(call);
        }  
    }

    /**
     * INTERNAL:
     * Override this method to throw an exception by default.
     * Platforms that support dynamic querying can override this to generate an EISInteraction.
     */
    public DatasourceCall buildCallFromStatement(SQLStatement statement, DatabaseQuery query, AbstractSession session) {
        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)query;
            MappedInteraction interaction = new MappedInteraction();
            interaction.setProperty(OPERATION, MongoOperation.FIND);
            interaction.setProperty(COLLECTION, ((EISDescriptor)query.getDescriptor()).getDataTypeName());
            if (statement.getWhereClause() == null) {
                return interaction;
            }
            if (readQuery.getFirstResult() > 0) {
                interaction.setProperty(SKIP, readQuery.getFirstResult());                
            }
            if (readQuery.getMaxRows() > 0) {
                interaction.setProperty(LIMIT, readQuery.getMaxRows());                
            }
            if (readQuery.getFetchSize() > 0) {
                interaction.setProperty(BATCH_SIZE, readQuery.getMaxRows());                
            }
            DatabaseRecord row = new DatabaseRecord();
            appendExpressionToQueryRow(statement.getWhereClause(), row, query);
            if (readQuery.hasOrderByExpressions()) {
                DatabaseRecord sort = new DatabaseRecord();
                for (Expression orderBy : readQuery.getOrderByExpressions()) {
                    appendExpressionToSortRow(orderBy, sort, query);                    
                }
                row.put(MongoRecord.SORT, sort);
            }
            if (readQuery.isReportQuery()) {
                DatabaseRecord select = new DatabaseRecord();
                for (Object field : ((SQLSelectStatement)statement).getFields()) {
                    if (field instanceof DatabaseField) {
                        select.put((DatabaseField)field, 1);
                    } else if (field instanceof Expression) {
                        Object value = extractValueFromExpression((Expression)field, readQuery);
                        if (!(value instanceof DatabaseField)) {
                            throw new EISException("Query too complex for Mongo translation, only field selects are supported in query: " + query);
                        }
                        select.put((DatabaseField)value, 1);
                    }
                }
                row.put("$select", select);
            }
            interaction.setInputRow(row);
            return interaction;
        }
        throw new EISException("Query too complex for Mongo translation, only select queries are supported in query: " + query);
    }
    
    /**
     * Append the expression and recursively to the query row.
     */
    protected void appendExpressionToQueryRow(Expression expression, AbstractRecord row, DatabaseQuery query) {
        if (expression.isRelationExpression()) {
            RelationExpression relation = (RelationExpression)expression;
            Object left = extractValueFromExpression(relation.getFirstChild(), query);
            Object right = extractValueFromExpression(relation.getSecondChild(), query);
            if (relation.getOperator().getSelector() == ExpressionOperator.Equal) {
                row.put(left, right);
            } else {
                DatabaseRecord nested = new DatabaseRecord();
                if (relation.getOperator().getSelector() == ExpressionOperator.GreaterThan) {
                    nested.put("$gt", right);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.LessThan) {
                    nested.put("$lt", right);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.LessThanEqual) {
                    nested.put("$lte", right);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.GreaterThanEqual) {
                    nested.put("$gte", right);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.NotEqual) {
                    nested.put("$ne", right);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.In) {                    
                    nested.put("$in", right);
                    row.put(left, nested);
                } else if (relation.getOperator().getSelector() == ExpressionOperator.NotIn) {
                    nested.put("$nin", right);
                    row.put(left, nested);
                } else {
                    throw new EISException("Query too complex for Mongo translation, relation [" + expression + "] not supported in query: " + query);
                }
                row.put(left, nested);
            }
        } else if (expression.isLogicalExpression()) {
            LogicalExpression logic = (LogicalExpression)expression;
            DatabaseRecord first = new DatabaseRecord();
            DatabaseRecord second = new DatabaseRecord();
            appendExpressionToQueryRow(logic.getFirstChild(), first, query);
            appendExpressionToQueryRow(logic.getSecondChild(), second, query);
            List nested = new Vector();
            nested.add(first);
            nested.add(second);
            if (logic.getOperator().getSelector() == ExpressionOperator.And) {
                row.put("$and", nested);
            } else if (logic.getOperator().getSelector() == ExpressionOperator.Or) {
                row.put("$or", nested);
            } else {
                throw new EISException("Query too complex for Mongo translation, logic [" + expression + "] not supported in query: " + query);
            }
        } else if (expression.isFunctionExpression()) {
            FunctionExpression function = (FunctionExpression)expression;
            if (function.getOperator().getSelector() == ExpressionOperator.Like) {
                Object left = extractValueFromExpression((Expression)function.getChildren().get(0), query);
                Object right = extractValueFromExpression((Expression)function.getChildren().get(1), query);
                if (!(right instanceof String)) {
                    throw new EISException("Query too complex for Mongo translation, like with [" + right + "] not supported in query: " + query);
                }
                String pattern = (String)right;
                DatabaseRecord nested = new DatabaseRecord();
                if (!this.isLikeRegex) {
                    pattern = Helper.convertLikeToRegex(pattern);
                }
                nested.put("$regex", pattern);
                row.put(left, nested);
            } else if (function.getOperator().getSelector() == ExpressionOperator.Not) {
                DatabaseRecord nested = new DatabaseRecord();
                appendExpressionToQueryRow((Expression)function.getChildren().get(0), nested, query);
                row.put("$not", nested);
            } else {
                throw new EISException("Query too complex for Mongo translation, function [" + expression + "] not supported in query: " + query);            
            }
        } else {
            throw new EISException("Query too complex for Mongo translation, expression [" + expression + "] not supported in query: " + query);            
        }
    }
    
    /**
     * Append the order by expression to the sort row.
     */
    protected void appendExpressionToSortRow(Expression expression, AbstractRecord row, DatabaseQuery query) {
        if (expression.isFunctionExpression()) {
            FunctionExpression function = (FunctionExpression)expression;
            if (function.getOperator().getSelector() == ExpressionOperator.Ascending) {
                Object field = extractValueFromExpression((Expression)function.getChildren().get(0), query);
                row.put(field, 1);
            } else if (function.getOperator().getSelector() == ExpressionOperator.Descending) {
                Object field = extractValueFromExpression((Expression)function.getChildren().get(0), query);
                row.put(field, -1);
            } else {
                throw new EISException("Query too complex for Mongo translation, order by [" + expression + "] not supported in query: " + query);
            }
        } else {
            Object field = extractValueFromExpression(expression, query);
            row.put(field, 1);
        }
    }
    
    /**
     * Extract the field or constant value from the comparison expression.
     */
    protected Object extractValueFromExpression(Expression expression, DatabaseQuery query) {
        Object value = null;
        if (expression.isQueryKeyExpression()) {
            QueryKeyExpression queryKeyExpression = (QueryKeyExpression)expression;
            value = queryKeyExpression.getField();
            if ((queryKeyExpression.getMapping() != null) && queryKeyExpression.getMapping().getDescriptor().isDescriptorTypeAggregate()) {
                String name = queryKeyExpression.getField().getName();
                while (queryKeyExpression.getBaseExpression().isQueryKeyExpression()
                        && ((QueryKeyExpression)queryKeyExpression.getBaseExpression()).getMapping().isAbstractCompositeObjectMapping()) {
                    queryKeyExpression = (QueryKeyExpression)queryKeyExpression.getBaseExpression();
                    name = ((AbstractCompositeObjectMapping)queryKeyExpression.getMapping()).getField().getName() + "." + name;
                }
                DatabaseField field = new DatabaseField();
                field.setName(name);
                value = field;
            }
        } else if (expression.isFieldExpression()) {
            value = ((FieldExpression)expression).getField();
        } else if (expression.isConstantExpression()) {
            value = ((ConstantExpression)expression).getValue();
        } else if (expression.isParameterExpression()) {
            value = query.getTranslationRow().get(((ParameterExpression)expression).getField());
        } else {
            throw new EISException("Query too complex for Mongo translation, comparison of [" + expression + "] not supported in query: " + query);
        }
        if (value instanceof List) {
            List values = (List)value;
            for (int index = 0; index < values.size(); index++) {
                Object element = values.get(index);
                if (element instanceof Expression) {
                    element = extractValueFromExpression((Expression)element, query);
                    values.set(index, element);
                }                
            }
        }
        return value;
    }
    
    /**
     * Do not prepare dynamic queries, as the translation row is required.
     */
    @Override
    public boolean shouldPrepare(DatabaseQuery query) {
        return (query.getDatasourceCall() instanceof EISInteraction);
    }

    /**
     * INTERNAL:
     * Create platform-default Sequence
     */
    @Override
    protected Sequence createPlatformDefaultSequence() {
        return new OIDSequence();
    }
}
