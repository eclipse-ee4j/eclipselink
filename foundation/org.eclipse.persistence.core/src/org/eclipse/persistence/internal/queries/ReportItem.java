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
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;

/**
 * <b>Purpose</b>: represents an item requested (i.e. field for SELECT)
 *
 * @author Doug Clarke
 * @since 2.0
 */
public class ReportItem implements Cloneable, java.io.Serializable {

    /** Expression (partial) describing the attribute wanted */
    protected Expression attributeExpression;

    /** Name given for item, can be used to retrieve value from result. Useful if same field retrieved multiple times */
    protected String name;

    /** Mapping which relates field to attribute, used to convert value and determine reference descriptor */
    protected DatabaseMapping mapping;
    
    /** Descriptor for object result that is not based on an expression */
    protected ClassDescriptor descriptor;
    
    /** Result type for this report item. */
    protected Class resultType;
    /** Stores the Join information for this item */
    protected JoinedAttributeManager joinedAttributeManager;
    
    /** Stores the row index for this item, given multiple results and joins */
    protected int resultIndex;
    
    public ReportItem() {
        super();
    }

    public ReportItem(String name, Expression attributeExpression) {
        this();
        this.name = name;
        this.attributeExpression = attributeExpression;
    }
    
    public Object clone(){
        try{
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    public Expression getAttributeExpression() {
        return attributeExpression;
    }
    
    public void setAttributeExpression(Expression attributeExpression) {
        this.attributeExpression = attributeExpression;
    }

    public ClassDescriptor getDescriptor(){
        return this.descriptor;
    }
    
    /**
     * INTERNAL:
     * Set the list of expressions that represent elements that are joined because of their
     * mapping for this query.
     */
    public JoinedAttributeManager getJoinedAttributeManager() {
        if (this.joinedAttributeManager == null) {
            this.joinedAttributeManager = new JoinedAttributeManager();
        }
        return this.joinedAttributeManager;
    }
    
    public JoinedAttributeManager getJoinedAttributeManagerInternal(){
        return joinedAttributeManager;
    }
    
    /**
     * INTERNAL:
     * Return if any attributes are joined.
     * To avoid the initialization of the JoinedAttributeManager this should be first checked before accessing.
     */
    public boolean hasJoining() {
        return this.joinedAttributeManager != null;
    }
    
    public DatabaseMapping getMapping() {
        return mapping;
    }

    public String getName() {
        return name;
    }
    
    public int getResultIndex() {
        return resultIndex;
    }

    public Class getResultType() {
        return resultType;
    }

    /**
     * INTERNAL:
     * Looks up mapping for attribute during preExecute of ReportQuery
     */
    public void initialize(ReportQuery query) throws QueryException {
        if (this.mapping == null) {
            if (this.attributeExpression != null) {
                DatabaseMapping mapping = this.attributeExpression.getLeafMapping(query, query.getDescriptor(), query.getSession());
                if (mapping == null){
                    if (this.attributeExpression.isExpressionBuilder()) {
                        Class resultClass = ((ExpressionBuilder)this.attributeExpression).getQueryClass();
                        if (resultClass == null){
                            resultClass = query.getReferenceClass();
                            ((ExpressionBuilder)this.attributeExpression).setQueryClass(resultClass);
                        }
                        setDescriptor(query.getSession().getDescriptor(resultClass));
                        if (getDescriptor().hasInheritance()){
                            ((ExpressionBuilder)this.attributeExpression).setShouldUseOuterJoinForMultitableInheritance(true);
                        }
                    }
                } else {
                    if (mapping.isAbstractDirectMapping() || this.attributeExpression.isMapEntryExpression() || mapping.isAggregateObjectMapping()){
                            setMapping(mapping);
                    //Bug4942640  Widen the check to support collection mapping too
                    } else if (mapping.isForeignReferenceMapping()) {
                        // DirectCollectionMapping doesn't have reference descriptor
                        if(mapping.getReferenceDescriptor() != null) {
                            setDescriptor(mapping.getReferenceDescriptor());
                            if (getDescriptor().hasInheritance()){
                                ((QueryKeyExpression)this.attributeExpression).setShouldUseOuterJoinForMultitableInheritance(true);
                            }
                        }
                    } else {
                        throw QueryException.invalidExpressionForQueryItem(this.attributeExpression, query);
                    }
                }
                if (hasJoining()) {
                    this.joinedAttributeManager.setDescriptor(this.descriptor);
                    this.joinedAttributeManager.setBaseQuery(query);
                }
                if (getAttributeExpression() != null){
                    if (getAttributeExpression().getBuilder().wasQueryClassSetInternally()){
                        //rebuild if class was not set by user this ensures the query has the same base
                        this.attributeExpression = this.attributeExpression.rebuildOn(query.getExpressionBuilder());
                    }
                    if (hasJoining()) {
                        this.joinedAttributeManager.setBaseExpressionBuilder(this.attributeExpression.getBuilder());
                    }
                } else if (hasJoining()) {
                    this.joinedAttributeManager.setBaseExpressionBuilder(query.getExpressionBuilder());
                }
                if (hasJoining()) {
                    this.joinedAttributeManager.prepareJoinExpressions(query.getSession());
                    this.joinedAttributeManager.processJoinedMappings(query.getSession());
                    this.joinedAttributeManager.computeJoiningMappingQueries(query.getSession());
                }
            }
        }
    }

    public boolean isConstructorItem(){
        return false;
    }

    /**
     * @return true if there is no expression (null)
     */
    public boolean isPlaceHolder() {
        return getAttributeExpression() == null;
    }

    public void setDescriptor(ClassDescriptor descriptor){
        this.descriptor = descriptor;
    }
    

    public void setJoinedAttributeManager(JoinedAttributeManager joinManager){
        this.joinedAttributeManager = joinManager;
    }
    
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }
    
    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
        if (hasJoining()) {
            getJoinedAttributeManager().setParentResultIndex(resultIndex);
        }
    }

    public void setResultType(Class resultType) {
        this.resultType = resultType;
    
        // Set it on the attribute expression as well if it is a function.
        if (getAttributeExpression()!=null && getAttributeExpression().isFunctionExpression()) {
            ((FunctionExpression) getAttributeExpression()).setResultType(resultType);
        }
    }

    public String toString() {
        return "ReportQueryItem(" + getName() + " -> " + getAttributeExpression() + ")";
    }
}
