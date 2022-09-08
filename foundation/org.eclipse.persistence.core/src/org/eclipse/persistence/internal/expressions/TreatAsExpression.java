/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * @author cdelahun
 *
 */
public class TreatAsExpression extends QueryKeyExpression {
    protected ObjectExpression typeExpressionBase;
    protected Expression typeExpression;

    protected Boolean isDowncast;//we only need a type expression if this is a downcast.

    public Expression convertToUseOuterJoin() {
        typeExpressionBase.convertToUseOuterJoin();
        return this;
    }

    public String descriptionOfNodeType() {
        return "Treat";
    }

    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        TreatAsExpression expression = (TreatAsExpression) object;
        return getCastClass().equals(expression.getCastClass());
    }

    public Vector getFields() {
        return typeExpressionBase.getFields();
    }

    public Object getFieldValue(Object objectValue, AbstractSession session) {
        return typeExpressionBase.getFieldValue(objectValue, session);
    }

    /**
     * This owns (can access) the child's extra tables as well as its parent's tables
     * so we should pull these from super (which gets them from the current descriptor)
     */
    public List<DatabaseTable> getOwnedTables() {
        return super.getOwnedTables();
    }

    public Expression getAlias(Expression subSelect) {
        return typeExpressionBase.getAlias(subSelect);
    }

    /**
     * Calculate the relation table for based on the various QueryKeyExpression
     * usages (join query keys, custom defined query keys, or query keys for
     * mappings).
     * Does not apply to Treat
     *
     * Called from {@link SQLSelectStatement#appendFromClauseForOuterJoin}.
     *
     * @return DatabaseTable
     */
    public DatabaseTable getRelationTable() {
        return null;
    }

    public TableAliasLookup getTableAliases() {
        return typeExpressionBase.getTableAliases();
    }

    public boolean hasAsOfClause() {
        return typeExpressionBase.hasAsOfClause();
    }

    /**
     * INTERNAL:
     */
    public boolean isDowncast() {
        if (this.isDowncast == null) {
            this.getDescriptor();//initializes isDowncast
        }
        return this.isDowncast;
    }

    /**
     * INTERNAL:
     */
    public boolean isTreatExpression() {
        return true;
    }

    public void printSQL(ExpressionSQLPrinter printer) {
        typeExpressionBase.printSQL(printer);
    }

    public boolean selectIfOrderedBy() {
        return typeExpressionBase.selectIfOrderedBy();
    }

    public Expression twistedForBaseAndContext(Expression newBase,
            Expression context, Expression oldBase) {
        if (oldBase == null || this.typeExpressionBase == oldBase) {
            Expression twistedBase = this.typeExpressionBase.twistedForBaseAndContext(newBase, context, oldBase);
            TreatAsExpression result = (TreatAsExpression)twistedBase.treat(this.castClass);
            if (shouldUseOuterJoin) {
                result.doUseOuterJoin();
            }
            if (shouldQueryToManyRelationship) {
                result.doQueryToManyRelationship();
            }
            return result;
        }

        return this;
    }

    public void validateNode() {
        typeExpressionBase.validateNode();
        //getDescriptor currently checks if the descriptor can be found for the castclass.
        //We may want to check that this is a downcast in future
        getDescriptor();
    }

    public Object valueFromObject(Object object, AbstractSession session,
            AbstractRecord translationRow, int valueHolderPolicy,
            boolean isObjectUnregistered) {
        return typeExpressionBase.valueFromObject(object, session,
                translationRow, valueHolderPolicy, isObjectUnregistered);
    }

    public Object valueFromObject(Object object, AbstractSession session,
            AbstractRecord translationRow, int valueHolderPolicy) {
        return typeExpressionBase.valueFromObject(object, session,
                translationRow, valueHolderPolicy);
    }

    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        if (castClass != null){
            writer.write(" AS "+ castClass.getName());
        }
    }

    public void writeFields(ExpressionSQLPrinter printer, Vector newFields,
            SQLSelectStatement statement) {
        typeExpressionBase.writeFields(printer, newFields, statement);
    }

    public void writeSubexpressionsTo(BufferedWriter writer, int indent)
            throws IOException {
        if (this.typeExpressionBase != null) {
            this.typeExpressionBase.toString(writer, indent);
        } else {
            super.writeSubexpressionsTo(writer, indent);
        }
    }

    public ClassDescriptor getLeafDescriptor(DatabaseQuery query,
            ClassDescriptor rootDescriptor, AbstractSession session) {
        return session.getDescriptor(castClass);
    }

    public DatabaseMapping getLeafMapping(DatabaseQuery query,
            ClassDescriptor rootDescriptor, AbstractSession session) {
        return typeExpressionBase
                .getLeafMapping(query, rootDescriptor, session);
    }

    @Override
    public DatabaseTable aliasForTable(DatabaseTable table) {
        return typeExpressionBase.aliasForTable(table);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = this.typeExpressionBase.rebuildOn(newBase);
        return newLocalBase.treat(castClass);
    }

    public ClassDescriptor getDescriptor() {
        if (isAttribute()) {
            //TODO: add support for treat on attributes
            throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
        }
        if (descriptor == null) {
            ClassDescriptor rootDescriptor = typeExpressionBase.getDescriptor();
            descriptor = convertToCastDescriptor(rootDescriptor, getSession());
        }
        return descriptor;
    }

    /**
     * INTERNAL -
     * Return the descriptor which contains this query key, look in the inheritance hierarchy
     * of rootDescriptor for the descriptor.  Does not set the descriptor, only returns it.
     */
    public ClassDescriptor convertToCastDescriptor(ClassDescriptor rootDescriptor, AbstractSession session) {
        isDowncast = Boolean.FALSE;
        if (castClass == null || rootDescriptor == null || rootDescriptor.getJavaClass() == castClass) {
            return rootDescriptor;
        }

        ClassDescriptor castDescriptor = session.getClassDescriptor(castClass);

        if (castDescriptor == null){
            throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
        }
        if (!castDescriptor.hasInheritance()){
            throw QueryException.castMustUseInheritance(getBaseExpression());
        }
        ClassDescriptor parentDescriptor = castDescriptor.getInheritancePolicy().getParentDescriptor();
        while (parentDescriptor != null){
            if (parentDescriptor == rootDescriptor){
                isDowncast = Boolean.TRUE;
                return castDescriptor;
            }
            parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
        }
        //is there value casting an emp to person in a query?
        ClassDescriptor childDescriptor = rootDescriptor;
        while (childDescriptor != null){
            if (childDescriptor == castDescriptor){
                return rootDescriptor;
            }
            childDescriptor = childDescriptor.getInheritancePolicy().getParentDescriptor();
        }

        throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
    }

    public Expression getTypeClause() {
        if (typeExpression == null) {
            if (getDescriptor() !=null && isDowncast()) {
                InheritancePolicy ip = this.getDescriptor().getInheritancePolicy();
                if (ip.isChildDescriptor()) {//or use the isDowncast flag.  Don't need to do anything if its not a downcast
                    //equivalent to typeExpressionBase.type().in(this.getDescriptor().getInheritancePolicy().getChildClasses())
                    typeExpression = ip.getWithAllSubclassesExpression();
                    if (typeExpression == null ) {
                        typeExpression = typeExpressionBase.type().equal(this.getDescriptor().getJavaClass());
                    } else {
                        typeExpression = this.typeExpressionBase.twist(typeExpression, typeExpressionBase);
                    }

                }
            } else {
                typeExpression = this.getBuilder();//equivalent to an empty expression.
            }

        }
        return typeExpression;
    }

    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        this.typeExpressionBase = (ObjectExpression)typeExpressionBase.copiedVersionFrom(alreadyDone);
    }

    public TreatAsExpression(Class castClass, ObjectExpression baseExpression) {
        super();
        this.name = "Treat as "+castClass;
        this.typeExpressionBase = baseExpression;
        if (baseExpression.isExpressionBuilder()){
            this.baseExpression = baseExpression;

            shouldQueryToManyRelationship = false;
            hasQueryKey = false;
            hasMapping = false;
        } else {
            this.baseExpression = baseExpression.getBaseExpression();
        }
        shouldUseOuterJoin = true;//this uses outerjoins to the cast class' tables by default.
        this.castClass = castClass;
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    @Override
    public void printJava(ExpressionJavaPrinter printer) {
        this.typeExpressionBase.printJava(printer);
        if (castClass != null){
            printer.printString(".treat(" + castClass.getName() + ".class)");
        }
    }

    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    protected void assignAlias(DatabaseTable alias, DatabaseTable table) {
        if (tableAliases == null) {
            if (this.typeExpressionBase!=null) {
                if (this.typeExpressionBase.getTableAliases()==null) {
                    typeExpressionBase.setTableAliases(new TableAliasLookup());
                }
                tableAliases = typeExpressionBase.getTableAliases();
            } else {
                tableAliases = new TableAliasLookup();
            }
        }
        tableAliases.put(alias, table);
    }

    /**
     * INTERNAL:
     * Assign aliases to any tables which I own. Start with t(initialValue),
     * and return the new value of  the counter , i.e. if initialValue is one
     * and I have tables ADDRESS and EMPLOYEE I will assign them t1 and t2 respectively, and return 3.
     */
    public int assignTableAliasesStartingAt(int initialValue) {
        //This assumes that the typeExpressionBase will alias its own tables, so we only need to handle
        //the extra's caused by this treat expression.
        if (hasBeenAliased()) {
            return initialValue;
        }
        int counter = initialValue;
        if (this.typeExpressionBase != null) {
            counter = this.typeExpressionBase.assignTableAliasesStartingAt(counter);
        }
        //Only difference between this and ObjectExpression's assignTableAliasesStartingAt is this uses getOwnedSubTables
        // instead of getOwnedTables which returns everything.
        List<DatabaseTable> ownedTables = getOwnedSubTables();
        if (ownedTables != null) {
            for (DatabaseTable table : ownedTables) {
                assignAlias("t" + counter, table);
                counter++;
            }
        }
        this.hasBeenAliased = true;
        return counter;
    }

    /**
     * INTERNAL:
     * much like getOwnedTables(), this gets the tables represented from the descriptor.  Difference is this only returns local tables
     * for the child casted descriptor, and excludes tables owned by the parent descriptor
     */
    public List<DatabaseTable> getOwnedSubTables() {
        ClassDescriptor parentDescriptor = this.typeExpressionBase.getDescriptor();
        Vector<DatabaseTable> childTables = new Vector(2);
        if (parentDescriptor.hasInheritance() && parentDescriptor.getInheritancePolicy().hasMultipleTableChild() ) {
            List parentTables = typeExpressionBase.getOwnedTables();
            //All tables for this child, including parent tables
            Vector<DatabaseTable> tables = getDescriptor().getTables();
            for (DatabaseTable table : tables) {
                if (!parentTables.contains(table)) {
                    childTables.add(table);
                }
            }
        }

        return childTables;
    }


    @Override
    public List<DatabaseTable> getAdditionalTables() {
        //called from ObjectExpression's getOwnedTables but not relevant to treat.
        return null;
    }

    /*
     * INTERNAL:
     * If this query key represents a foreign reference answer the
     * base expression -> foreign reference join criteria.
     * This shouldn't be used on Treat
     */
    public Expression mappingCriteria(Expression base) {
        if (typeExpressionBase.isQueryKeyExpression()) {
            return ((QueryKeyExpression)typeExpressionBase).mappingCriteria(base);
        }
        return null;
    }

    /**
     * INTERNAL:
     * Used in case outer joins should be printed in FROM clause.
     * Each of the additional tables mapped to expressions that joins it.
     */
    public Map additionalTreatExpressionCriteriaMap() {
        if (getDescriptor() == null) {
            return null;
        }
        int tableSize = 0;
        HashMap tablesJoinExpressions = new HashMap();

        ClassDescriptor parentDescriptor = this.typeExpressionBase.getDescriptor();

        //outerjoin our parent->child tables
        if (parentDescriptor.hasInheritance() &&
                parentDescriptor.getInheritancePolicy().hasMultipleTableChild() ) {
            Vector tables = getDescriptor().getTables();//All this child's tables
            tableSize = tables.size();
            //look up the joins from the parent descriptor to our tables.
            for (int i=0; i < tableSize; i++) {
                DatabaseTable table = (DatabaseTable)tables.elementAt(i);
                Expression joinExpression = parentDescriptor.getInheritancePolicy().getChildrenTablesJoinExpressions().get(table);
                //Some of our tables might be the in our parent as well, so ignore the lack of a joinExpression
                if (joinExpression != null) {
                    joinExpression = this.baseExpression.twist(joinExpression, this);
                    tablesJoinExpressions.put(table, joinExpression);
                }
            }
        }

        if (isUsingOuterJoinForMultitableInheritance()) {
            List childrenTables = getDescriptor().getInheritancePolicy().getChildrenTables();
            tableSize = childrenTables.size();
            for (int i=0; i < tableSize; i++) {
                DatabaseTable table = (DatabaseTable)childrenTables.get(i);
                Expression joinExpression = getDescriptor().getInheritancePolicy().getChildrenTablesJoinExpressions().get(table);
                joinExpression = this.baseExpression.twist(joinExpression, this);
                tablesJoinExpressions.put(table, joinExpression);
            }
        }

        return tablesJoinExpressions;
    }

    /**
     * INTERNAL:
     * this returns a single expression to represent the join from the main table to all child descriptor tables
     * Only if outer joins should be printed in the where clause
     * @return Expression
     */
    public Expression getTreatCriteria() {
        if (getDescriptor() == null) {
            return null;
        }
        //need to build this using just the multiple tables on this descriptor not included in the parent's join expression
        Expression criteria = null;
        if(getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
            Vector tables = getDescriptor().getTables();//This child's tables
            ClassDescriptor parentDescriptor = this.typeExpressionBase.getDescriptor();
            int tablesSize = tables.size();
            if (parentDescriptor.hasInheritance() &&
                    parentDescriptor.getInheritancePolicy().hasMultipleTableChild() ) {
                //look up the joins from the parent descriptor to our tables.
                for (int i=0; i < tablesSize; i++) {
                    DatabaseTable table = (DatabaseTable)tables.elementAt(i);
                    Expression joinExpression = parentDescriptor.getInheritancePolicy().getChildrenTablesJoinExpressions().get(table);
                    //Some of our tables might be the in our parent as well, so ignore the lack of a joinExpression
                    if (joinExpression != null) {
                        joinExpression = this.baseExpression.twist(joinExpression, this);
                        if (shouldUseOuterJoin()) {
                            joinExpression = joinExpression.convertToUseOuterJoin();
                        }
                        criteria = joinExpression.and(criteria);
                    }
                }
            }
        }
        return criteria;
    }

    /**
     * INTERNAL:
     * Return the expression to join the main table of this node to any auxiliary tables.
     */
    public Expression additionalTreatExpressionCriteria() {
        if (getDescriptor() == null) {
            return null;
        }
        //need to build this using just the multiple tables on this descriptor not included in the parent's join expression
        Expression criteria = null;
        if(getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
            if(isUsingOuterJoinForMultitableInheritance()) {
                criteria = getDescriptor().getInheritancePolicy().getChildrenJoinExpression();
                criteria = this.baseExpression.twist(criteria, this);
                criteria.convertToUseOuterJoin();
            }
        }
        return criteria;
    }


    public DatabaseTable getSourceTable() {
        //not used currently, but should return the baseExpressionType table if used in the future
        return null;
    }

    public DatabaseTable getReferenceTable() {
        //not used currently, but should return the treat subclass first table if used in the future
        return null;
    }

    public Expression normalize(ExpressionNormalizer normalizer, Expression base, List<Expression> foreignKeyJoinPointer) {
        //need to determine what type this is, as it may need to change the expression its based off slightly
        if (this.hasBeenNormalized) {
            return this;
        }
        this.hasBeenNormalized = true;

        Expression typeExpression = getTypeClause();
        typeExpression.normalize(normalizer);

        if (this.baseExpression != null) {//should never be null
            // First normalize the base.
            setBaseExpression(this.baseExpression.normalize(normalizer));
            if (getAsOfClause() == null) {
                asOf(this.baseExpression.getAsOfClause());
            }
        }

        //This class has no validation but we should still make the method call for consistency
        //bug # 2956674
        //validation is moved into normalize to ensure that expressions are valid before we attempt to work with them
        validateNode();
        //the following is based on QueryKey.normalize

        SQLSelectStatement statement = normalizer.getStatement();
        //no longer directly normalize the typeExpressionBase, or find a way to use it
        this.typeExpressionBase = (ObjectExpression)this.typeExpressionBase.normalize(normalizer);


        // Normalize the ON clause if present.  Need to use rebuild, not twist as parameters are real parameters.
        if (this.onClause != null) {//not sure this is needed/valid
            this.onClause = this.onClause.normalize(normalizer);
        }

        ClassDescriptor parentDescriptor = this.typeExpressionBase.getDescriptor();
        boolean isSTI =  getOwnedSubTables().isEmpty();
        //only really valid if it has inheritance, but better this code than skipping it into the joins

        if (isSTI) {
            if (foreignKeyJoinPointer != null) {
                // If this expression is right side of an objExp.equal(objExp), one
                // need not add additionalExpressionCriteria twice.
                // Also the join will replace the original objExp.equal(objExp).
                // For CR#2456.
                foreignKeyJoinPointer.add(typeExpression.and(this.onClause));
            } else {
                //this just and's in the entire expression to the normalizer's expression.
                //Need to use this for TYPE and none-outerjoin components
                normalizer.addAdditionalLocalExpression(typeExpression.and(this.onClause));
            }
            return this;
        }

        //if shouldPrintOuterJoinInWhereClause is true, this is this child's tables joined together in one expression
        Expression treatJoinTableExpressions = getTreatCriteria();

        boolean parentUsingOuterJoinForMultitableInheritance = typeExpressionBase.isUsingOuterJoinForMultitableInheritance();

        if (treatJoinTableExpressions != null) {
            treatJoinTableExpressions = treatJoinTableExpressions.normalize(normalizer);
        }
        Integer postition = typeExpressionBase.getOuterJoinExpIndex();
        if (postition!=null ) {
            if (parentUsingOuterJoinForMultitableInheritance) {
                //outer join was done, so our class' tables would have been included
                return this;
            }

            if (getSession().getPlatform().isInformixOuterJoin()) {
                normalizer.addAdditionalLocalExpression(typeExpression.and(additionalTreatExpressionCriteria()).and(this.onClause));
                return this;
            } else if (((!getSession().getPlatform().shouldPrintOuterJoinInWhereClause()))
                    || (!getSession().getPlatform().shouldPrintInnerJoinInWhereClause((normalizer.getStatement().getParentStatement() != null ? normalizer.getStatement().getParentStatement().getQuery() : normalizer.getStatement().getQuery())))) {

                //Adds the left joins from treat to the base QKE joins.
                Map<DatabaseTable, Expression> map = statement.getOuterJoinExpressionsHolders().get(postition).outerJoinedAdditionalJoinCriteria;
                if (map !=null) {
                    map.putAll(additionalTreatExpressionCriteriaMap());
                } else {
                    statement.getOuterJoinExpressionsHolders().get(postition).outerJoinedAdditionalJoinCriteria = additionalTreatExpressionCriteriaMap();
                }
                return this;
            }
        } else if (!getSession().getPlatform().shouldPrintOuterJoinInWhereClause()
                || (!getSession().getPlatform().shouldPrintInnerJoinInWhereClause((normalizer.getStatement().getParentStatement() != null ? normalizer.getStatement().getParentStatement().getQuery() : normalizer.getStatement().getQuery())))) {
            //the base is not using an outer join, so we add a new one for this class' tables.
            Map additionalExpMap = additionalTreatExpressionCriteriaMap();
            if (additionalExpMap!=null && !additionalExpMap.isEmpty()) {
                statement.addOuterJoinExpressionsHolders(additionalExpMap, parentDescriptor);
            }
        }
        typeExpression = typeExpression.normalize(normalizer);

        if (foreignKeyJoinPointer != null) {
            // If this expression is right side of an objExp.equal(objExp), one
            // need not add additionalExpressionCriteria twice.
            // Also the join will replace the original objExp.equal(objExp).
            // For CR#2456.
            foreignKeyJoinPointer.add(typeExpression.and(this.onClause));
        } else {
            //this just and's in the entire expression to the normalizer's expression.  Need to use this for TYPE and non-outerjoin components
            normalizer.addAdditionalLocalExpression(typeExpression.and(additionalTreatExpressionCriteria()).and(this.onClause));
        }
        return this;
    }
}
